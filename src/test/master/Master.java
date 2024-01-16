package test.master;

import test.node.PingPongInterface;

import java.io.FileWriter;
import java.io.IOException;
import java.rmi.Naming;
import java.util.ArrayList;
import java.util.List;

public class Master {
  public static List<String> hosts = new ArrayList<>();

  public static void retreiveHostsFromList(String hostsList) {

    // Remove the brackets, quotes, and split the string
    String[] hostNames = hostsList.replaceAll("[\\[\\]'\"]", "").split(",");

    // Add trimmed host names to the list
    for (String hostName : hostNames) {
      hosts.add(hostName.trim());
    }

  }

  private static void getMessage(String message, String host) {
    FileWriter fileWriter = null;
    long startTime0 = System.nanoTime();
    String simulateVariableAffect = "a";
    long endTime0 = System.nanoTime();
    long latency0 = endTime0 - startTime0;
    try {

      long registerStartTime = System.nanoTime();
      PingPongInterface stub = (PingPongInterface) Naming.lookup("rmi://" + host + ":3000/PingPongObject");
      long registerEndTime = System.nanoTime();
      long registerLatency = registerEndTime - registerStartTime - latency0;
      
      long pingStartTime = System.nanoTime();
      String response = stub.ping(message, host);
      long pingEndTime = System.nanoTime();
      long pingLatency = pingEndTime - pingStartTime - latency0;
      // Écriture dans le fichier CSV
      fileWriter = new FileWriter("latency_optimized_results.csv", true); // 'true' pour append au fichier
      fileWriter.write(host + "," + registerLatency + "," + pingLatency + "\n"); // Format CSV : host,registerLatency,pingLatency
      System.out.println("Response => " + response);
      System.out.println("pingLatency: " + pingLatency + " nanoseconds");
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
  }

  public static void main(String[] args) {

    if (args.length > 1) {
      retreiveHostsFromList(args[1]);
      for (String host : hosts) {
        getMessage(args[0], host);
      }

    } else {
      System.out.println("Please provide message and a host as a message-line argument.");
    }
  }
}
