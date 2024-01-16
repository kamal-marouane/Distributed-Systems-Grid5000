package parser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;

import network.master.Master;
import hosts.*;
import scheduler.LocalScheduler;


public class Node {

    private String nodeName;
    private List<String> commands;
    public TaskStatus status;
    private boolean isFile = false;

    public Node(String nodeName, List<String> commands) {
        this.nodeName = nodeName;
        this.commands = commands;
        this.status = TaskStatus.NOT_STARTED;
    }

    public Node(){
        this.commands = new ArrayList<>();
        this.status = TaskStatus.NOT_STARTED;
    }

    public Node(String nodeName) {
        this.nodeName = nodeName;
        this.commands = new ArrayList<>();
        this.status = TaskStatus.NOT_STARTED;
    }
    public void execute() {
        
        try {
            for (String command : commands) {
                System.out.println("Executing commands for nodeName: " + nodeName);

                String[] arguments=new String[0];
                Host thisHost=null;
                Process process = Runtime.getRuntime().exec(new String[]{"/bin/bash", "-c", "uniq $OAR_NODEFILE | awk 'NR<2'"});


                // Capture the output
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                // get master host
                Host master = new Host(reader.readLine().trim());

                

                int n = (RetrieveHosts.hosts).size();
                int i = n;
                Random random = new Random();


                // Node.lock.lock();
                while (i == n) {
                    synchronized(RetrieveHosts.hosts){
                        i = 0;
                        for (Host host : RetrieveHosts.hosts) {
                            //System.out.println(host.hostname);
                            if (host.status == HostStatus.FREE) {
                                thisHost = host;
                                arguments = new String[]{command, thisHost.hostname};
                                thisHost.status = HostStatus.OCCUPIED;
                                break;
                            }
                            i++;
                        }
                    }
                    
                    // If no host was free, consider waiting or retrying after a delay
                    if (i == n) {
                        Thread.sleep(random.nextInt(201) + 100); // someDelay is a delay in milliseconds
                    }
                }

                int responseCode = Master.master(arguments,master.hostname,this);

                if(responseCode==0){
                    thisHost.status = HostStatus.FREE;
                }else{
                    throw new RuntimeException("ABORT ! Task failed.");
                }
            }

            this.status = TaskStatus.FINISHED;
            System.out.println("Target completed: " + nodeName);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getNodeName() {
        return nodeName;
    }

    public boolean getIsFile(){
        return isFile;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public void setIsFile(boolean bool){
        this.isFile = bool ;
    }

    public List<String> getCommands() {
        return commands;
    }

    public void addCommand(String command){
        commands.add(command);
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

    public void clearCommands() {
        commands.clear();
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public static boolean hasFileExtension(String fileName) {
        return fileName.contains(".") && fileName.lastIndexOf('.') > 0;
    }

    @Override
    public String toString() {
        return "Node{" +
                "nodeName='" + nodeName + '\'' +
                ", commands=" + commands +
                '}';
    }
}
