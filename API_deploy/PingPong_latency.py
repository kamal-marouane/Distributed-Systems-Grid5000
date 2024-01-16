import os
import getpass
from utils import *

# User credentials
user = input(f"Grid'5000 username (default is {os.getlogin()}): ") or os.getlogin()
password = getpass.getpass("Grid'5000 password (leave blank on frontends): ")
g5k_auth = (user, password) if password else None

# Reservation details
site = "grenoble"
# ["grenoble", "nancy", "rennes", "nantes", "luxembourg", "lyon"]
nodes_per_site = 10
walltime = "0:10"


# Store job IDs for later deletion if necessary
job_ids = []


# Initial site files sending
source_paths = ["./deploy/","./src/test/"]
custom_dir = ""
process_site(user, site,source_paths,custom_dir)

# submit site job
site_command = f'javac -d bin test/node/*.java && javac -cp bin -d bin test/master/Master.java && sed -i "s/\r//g" deploy/master.sh && ./deploy/master.sh'
job_id, assigned_nodes = submit_and_initialize_job(site, nodes_per_site, walltime, site_command, g5k_auth)
job_ids.append((site, job_id))
print(f"Assigned site at {site}: {', '.join(assigned_nodes)}")


# # Check state of jobs and delete if necessary
# # check_and_delete_jobs(job_ids, g5k_auth)