from concurrent.futures import ThreadPoolExecutor
import requests
import subprocess
import os
from time import sleep

# Function definitions for check_job_status, transfer_and_execute, and any other utility functions

def submit_job(api_url, payload, auth):
    job = requests.post(api_url, data=payload, auth=auth).json()
    return job["uid"]

def check_job_status(api_url, job_id, auth):
    response = requests.get(api_url + f"/{job_id}", auth=auth).json()
    return response["state"]

def wait_for_job_start(api_url, job_id, auth, max_wait_time, check_interval):
    elapsed_time = 0
    while elapsed_time < max_wait_time:
        state = check_job_status(api_url, job_id, auth)
        if state == "running":
            return True
        sleep(check_interval)
        elapsed_time += check_interval
    return False

def get_assigned_nodes(api_url, job_id, auth):
    job_info = requests.get(api_url + f"/{job_id}", auth=auth).json()
    return job_info.get("assigned_nodes", [])

def submit_and_initialize_job(site, nodes_per_site, walltime, command, g5k_auth):
    api_job_url = f"https://api.grid5000.fr/stable/sites/{site}/jobs"
    payload = {
        "resources": f"nodes={nodes_per_site},walltime={walltime}",
        "command": f"{command} > {site}_output.log 2>&1 "
    }
    job_id = submit_job(api_job_url, payload, g5k_auth)
    print(f"Job submitted at {site} ({job_id})")

    max_wait_time = 30
    check_interval = 1
    wait_for_job_start(api_job_url, job_id, g5k_auth, max_wait_time, check_interval)

    assigned_nodes = get_assigned_nodes(api_job_url, job_id, g5k_auth)
    print(f"Assigned nodes at {site}: {', '.join(assigned_nodes)}")
    
    return job_id, assigned_nodes

def check_and_delete_jobs(job_ids, auth):
    for site, job_id in job_ids:
        api_job_url = f"https://api.grid5000.fr/stable/sites/{site}/jobs"
        state = requests.get(api_job_url + f"/{job_id}", auth=auth).json()["state"]
        if state != "terminated":
            requests.delete(api_job_url + f"/{job_id}", auth=auth)
            print(f"Job at {site} ({job_id}) deleted.")

# Function definitions for sending files

def ssh_and_create_directory(user, site, custom_folder):
  # Use ssh to connect to the site and run the mkdir command
  subprocess.run(["ssh", f"{user}@access.grid5000.fr", f"mkdir -p {site}/{custom_folder}"])

def scp_to_site(user, site, source_path, custom_folder):
  # Get the absolute path of the source
  abs_source_path = os.path.abspath(source_path)

  # Define the destination path based on the site name and target directory
  destination_path = f"{user}@access.grid5000.fr:{site}/{custom_folder}"

  # Print the scp command
  scp_command = ["scp", "-r", abs_source_path, f"{destination_path}/{os.path.basename(abs_source_path)}"]
  print(" ".join(scp_command))

  # Use scp to copy files/folders to the destination path
  subprocess.run(scp_command)

def process_site(user, site, source_paths, custom_folder):
  print(f"Sending data to {site}; data = {source_paths}")
  # Create the directory on the site
  ssh_and_create_directory(user, site, custom_folder)

  # Copy files/folders to their respective directories
  for source_path in source_paths:
    if os.path.exists(source_path):
      scp_to_site(user, site, source_path, custom_folder)
    else:
      print(f"Error: {source_path} does not exist.")

def process_sites_in_parallel(user, sites, source_paths, custom_dir):
    with ThreadPoolExecutor(max_workers=len(sites)) as executor:
        futures = [executor.submit(process_site, user, site, source_paths, custom_dir) for site in sites]
        for future in futures:
            future.result()
