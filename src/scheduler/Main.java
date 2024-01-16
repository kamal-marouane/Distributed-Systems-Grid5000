package scheduler;

import parser.MakefileParser;
import parser.Node;
import hosts.RetrieveHosts;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import static network.master.Master.logger;

public class Main {


    public static void main(String[] args) throws InterruptedException {

        FileWriter fileWriter = null;
        long startTime0 = System.nanoTime();
        long endTime0 = System.nanoTime();
        long latency0 = endTime0 - startTime0;

        long schedulerStartTime = System.nanoTime();
        RetrieveHosts.retreiveHostsFromList(args[0]);
        LocalScheduler scheduler = new LocalScheduler();

        MakefileParser parser = new MakefileParser();
        long parserStartTime = System.nanoTime();
        Map<Node,List<Node>> graph = parser.processFile("./scheduler/Makefile");
        long parserEndTime = System.nanoTime();
        long parserDuration = (parserStartTime - parserEndTime - latency0) / 1_000_000;
        System.out.println("Parser Execution time: " + parserDuration + " ms");
        System.out.println("************* Starting Scheduler now *****************");

        for (Map.Entry<Node, List<Node>> entry : graph.entrySet()) {
            scheduler.addNode(entry.getKey(), entry.getValue());
        }
        scheduler.executeTasks();
        long schedulerEndTime = System.nanoTime();
        long schedulerDuration = (schedulerEndTime - schedulerStartTime - latency0) / 1_000_000;;
        try {
            fileWriter = new FileWriter("scheduler_results.csv", true);
            fileWriter.write(schedulerDuration + "\n");
            System.out.println("Scheduler Execution time: " + schedulerDuration + " ms");
            fileWriter.write((RetrieveHosts.hosts).size()+","+schedulerDuration + "\n");  
            System.out.println("HELLO");
        } catch (Exception e) {
            System.out.println("Master exception In Message Transmission : " + e);
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    System.out.println("Error closing FileWriter: " + e);
                }
            }
        }
        logger.saveRecordsToCSV("file_transfers.csv");
    }
}
