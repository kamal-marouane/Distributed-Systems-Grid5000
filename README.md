# Grid5000-distributed-system
A distributed make execution in Grid5000.

### Members of the team

- GHAZAOUI Badr
- MAROUANE Kamal
- RIMAOUI Nabila
- ZERKTOUNI Ismail

## Connect to Grid5000
First, to simplify ssh connection to a site frontend, place the next `config` file inside your ~/.ssh folder :
```
Host g5k
  User <login>
  Hostname access.grid5000.fr
  ForwardAgent no

Host *.g5k
  User <login>
  ProxyCommand ssh g5k -W "$(basename %h .g5k):%p"
  ForwardAgent no
```

`<login>` being your grid5000 account pseudo.

Now you can simply run `ssh <site>.g5k` to directly access the site's frontend without having to go through the access machine first. You can also directly copy local files using `scp <my-file> <site>.g5k:`. Refer to the official Grid5000 documentation for more details. 

## Setting up the environment
You first need to copy the .java source files and the .sh scritps to the frontend node, so that they can be accessed from the cluster nodes. From your local bash CLI and from the root directory of this project, run `scp -r src/network/ <site>.g5k:` and `scp -r deploy/ <site>.g5k:` to copy the folder as specified above.

Now connect to the frontend site, and compile the .java files, use `javac -d bin network/node/*.java` and `javac -cp bin -d bin network/master/Master.java`. The executable files will be placed inside a `bin/` folder and will be accessible everywhere in the cluster (in the same site).

This part needs further automating to replace the manual process.

## Running the automated process for RMI pingpong testing
Now, from the frontend node, run the `./deploy/automate.sh` script to run the process.

After job completion, you should find a bunch of files in the home directory :
+ `master_output.log` : contains the output of the master node showing its communication messages with the worker nodes.
+ `node_output.log` : contains the output of the worker nodes (only the first one, the script needs to be updated ...).
+ `<host-name>_file.txt` : these files are created as intended by the master. Each worker node creates its own file.

The deletion of these files as well as the job termination at the end of the execution should be configured as well.

