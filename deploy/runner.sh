#! /usr/bin/env bash

# Extract the hostnames from the OAR_NODEFILE
HOSTNAMES=$(uniq $OAR_NODEFILE)

# Get the first node to be considered as Master
MASTER_NODE=$(hostname)


# Run the Node class on other nodes
for hostname in $HOSTNAMES; do
    if [ "$hostname" != "$MASTER_NODE" ]; then
        ssh $hostname "java -cp bin network.node.Node $hostname > node_output.log 2>&1" &
        sleep 5
    fi
done

# java -cp bin scheduler.Main 
HOSTLIST=$(echo "$HOSTNAMES" | grep -v "$MASTER_NODE" | awk '{printf "\"%s\",", $0}' | sed 's/,$//')
java -cp bin scheduler.Main "[$HOSTLIST]"