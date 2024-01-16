package hosts;

import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;


public class RetrieveHosts {

    public static List<Host> hosts = new ArrayList<>();

    public static void retreiveHostsFromList(String hostsList) {

        // Remove the brackets, quotes, and split the string
        String[] hostNames = hostsList.replaceAll("[\\[\\]'\"]", "").split(",");

        // Add trimmed host names to the list
        for (String hostName : hostNames) {
            hosts.add(new Host(hostName.trim()));
        }

    }
}