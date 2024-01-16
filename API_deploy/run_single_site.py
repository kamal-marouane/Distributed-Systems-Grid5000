import os
import getpass
from utils import *

# User credentials
user = input(f"Grid'5000 username (default is {os.getlogin()}): ") or os.getlogin()
password = getpass.getpass("Grid'5000 password (leave blank on frontends): ")
g5k_auth = (user, password) if password else None

# Reservation details
site = "nancy"
nodes_per_site = 23
walltime = "0:30"


# Store job IDs for later deletion if necessary
job_ids = []


# Initial site files sending
source_paths = ["./deploy/","./src/network/","./src/hosts/","./src/parser/","./src/scheduler/"]
custom_dir = ""
process_site(user, site,source_paths,custom_dir)

# submit site job
site_command = f'javac -d bin network/node/*.java && javac -cp bin -d bin network/master/Master.java && javac -cp bin -d bin hosts/*.java && javac -cp bin -d bin parser/*.java && javac -cp bin -d bin scheduler/*.java && gcc scheduler/premier.c -o premier -lm && sed -i "s/\r//g" deploy/runner.sh && ./deploy/runner.sh'
job_id, assigned_nodes = submit_and_initialize_job(site, nodes_per_site, walltime, site_command, g5k_auth)
job_ids.append((site, job_id))
print(f"Assigned site at {site}: {', '.join(assigned_nodes)}")


# # Check state of jobs and delete if necessary
# # check_and_delete_jobs(job_ids, g5k_auth)