import os
import getpass
from utils import *

# User credentials
user = input(f"Grid'5000 username (default is {os.getlogin()}): ") or os.getlogin()
password = getpass.getpass("Grid'5000 password (leave blank on frontends): ")
g5k_auth = (user, password) if password else None

# Reservation details

workers = ["grenoble", "nancy", "rennes", "nantes", "luxembourg", "lyon"]
# others : "strasbourg", "nantes", "sophia", "grenoble"
master = "lille"
nodes_per_site = 1
walltime = "0:20"

# Initial workers files sending
workers_source_paths = ["./src/network/node/"]
custom_dir = "network/"
process_sites_in_parallel(user, workers,workers_source_paths,custom_dir)


# Store job IDs for later deletion if necessary
job_ids = []

# submit worker jobs
worker_command = "javac -d bin network/node/*.java && java -cp bin network.node.Node $(hostname)"
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
master_source_paths = ["./src/hosts/","./src/parser/","./src/scheduler/","./src/performance/","./src/network/"]
custom_dir = ""
process_site(user, master,master_source_paths,custom_dir)

# submit Master job

master_command = f'javac -cp bin -d bin -Xlint:unchecked hosts/*.java parser/*.java scheduler/*.java network/node/*.java network/master/*.java performance/*.java && gcc scheduler/premier.c -o premier -lm && java -cp bin scheduler.Main "{assigned_workers}"'
job_id, assigned_nodes = submit_and_initialize_job(master, nodes_per_site, walltime, master_command, g5k_auth)
job_ids.append((master, job_id))
print(f"Assigned master at {master}: {', '.join(assigned_nodes)}")


# # Check state of jobs and delete if necessary
# # check_and_delete_jobs(job_ids, g5k_auth)