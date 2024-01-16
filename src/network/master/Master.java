package network.master;

import network.node.PingPongInterface;
import parser.*;
import performance.FileTransferLogger;
import scheduler.LocalScheduler;


import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.util.List;

public class Master {


    public static FileTransferLogger logger = new FileTransferLogger();

    private static int executeACommand(String command,String host,String master, Node node){
        int exitCode = -1;
        try {
            PingPongInterface exec = (PingPongInterface) Naming.lookup("rmi://"+host+":3000/PingPongObject");
            List<Node> deps = LocalScheduler.graph.get(node);
            if(deps !=null){
                for (Node dep : deps) {
                    int exists = exec.executeCommand("ls " + dep.getNodeName());
                    if(exists!=0){
                        if(dep.getIsFile()) sendFile(master,host,"./"+dep.getNodeName());
                        else if(hasFileExtension(dep.getNodeName())) sendFile(master,host,dep.getNodeName());
                    }
                    else System.out.println("*-*-*-*-*-*-*-*-*-* File " + dep.getNodeName() + " Already sent to " + host);
                }
            }
            exitCode = exec.executeCommand(command);


            // Print the result
            if (exitCode == 0) {
                System.out.println("The File has been created.");
                // scp target.txt from host to master
                if(hasFileExtension(node.getNodeName())) sendFile(host,master,node.getNodeName());
            } else {
                System.out.println("Error executing command. Exit code: " + exitCode);
            }



        } catch (Exception e) {
            System.out.println("Master exception in File Creation : ");
            e.printStackTrace();
        }
        return exitCode;
    }


    
    private static void sendFile(String source,String destination, String target){

        long startTime0 = System.nanoTime();
        long endTime0 = System.nanoTime();
        long latency0 = endTime0 - startTime0;
        long fileSendingStartTime = System.nanoTime();
        String command = "scp " + source + ":" + target + " " + destination + ":~";
        System.out.println("+++++++++ "+command);

        try {
            Process process = Runtime.getRuntime().exec(command);
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("File copied successfully");
            } else {
                System.out.println("SCP failed with exit code " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        long fileSendingEndTime = System.nanoTime();
        long fileSendingDuration = (fileSendingEndTime - fileSendingStartTime - latency0) / 1_000_000;
        System.out.println("File Sending Execution time : " + fileSendingDuration + " ms  for file : " +target);
        logger.addRecord(target,destination,fileSendingDuration);
    }

    public static boolean hasFileExtension(String fileName) {
        return fileName.contains(".") && fileName.lastIndexOf('.') > 0;
    }

    public static boolean canSend(String filePath) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        // Command to find the file
        processBuilder.command("bash", "-c", "find . -name " + filePath);

        try {
            Process process = processBuilder.start();

            // Reading the output of the command
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            boolean fileFound = false;

            while ((line = reader.readLine()) != null) {
                if (line.contains(filePath)) {
                    System.out.println("/////////////////////////////// :"+line);
                    fileFound = true;
                    break;
                }
            }
            // Wait to get exit value
            int exitCode = process.waitFor();
            System.out.println("Exit value is : " + exitCode);

            return !fileFound;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;

        }
    }

    public static int master(String[] args,String masterName, Node node) {

        if (args.length > 1) {

            return executeACommand(args[0],args[1],masterName,node);

        } else {
            System.out.println("Please provide command and a host as a command-line argument.");
            return -1; //pour tester
        }
    }
}
