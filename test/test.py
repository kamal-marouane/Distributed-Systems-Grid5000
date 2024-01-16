from utils import *
import os
import getpass

# User credentials
user = input(f"Grid'5000 username (default is {os.getlogin()}): ") or os.getlogin()
password = getpass.getpass("Grid'5000 password (leave blank on frontends): ")
g5k_auth = (user, password) if password else None


# Reservation details
# zooook  
workers = ["grenoble", "nancy", "rennes", "nantes", "luxembourg", "lyon"]
# others : "strasbourg", "nantes", "sophia", "grenoble"
master = "lille"
# mhaweeer
nodes_per_site = 1
walltime = "0:05"

# Initial workers files sending
workers_source_paths = ["./src/test/node/"]
custom_dir = "test/"
process_sites_in_parallel(user, workers,workers_source_paths,custom_dir)


# Store job IDs for later deletion if necessary
job_ids = []

# submit worker jobs
worker_command = "javac -d bin test/node/*.java && java -cp bin test.node.Node $(hostname)"
assigned_workers = []

# parallel submission of jobs
def submit_single_job(site, nodes_per_site, walltime, command, auth):
    job_id, assigned_nodes = submit_and_initialize_job(site, nodes_per_site, walltime, command, auth)
    return (site, job_id), assigned_nodes

with ThreadPoolExecutor(len(workers)) as executor:
        futures = [executor.submit(submit_single_job, site, nodes_per_site, walltime, worker_command, g5k_auth) for site in workers]
        for future in futures:
            job_info, assigned_nodes = future.result()
            job_ids.append(job_info)
            assigned_workers.extend(assigned_nodes)
            print(f"Assigned worker at {job_info[0]}: {', '.join(assigned_nodes)}")

# Initial master files sending
master_source_paths = ["./src/test/"]
custom_dir = ""
process_site(user, master,master_source_paths,custom_dir)

# submit Master job
# zoooook
master_command = f'javac -d bin test/node/*.java &&  javac -d bin test/master/Master.java && java -cp bin test.master.Master "message " "{assigned_workers}"'
job_id, assigned_nodes = submit_and_initialize_job(master, nodes_per_site, walltime, master_command, g5k_auth)
job_ids.append((master, job_id))
# mhaaweeer
print(f"Assigned master at {master}: {', '.join(assigned_nodes)}")


# # Check state of jobs and delete if necessary
# # check_and_delete_jobs(job_ids, g5k_auth)