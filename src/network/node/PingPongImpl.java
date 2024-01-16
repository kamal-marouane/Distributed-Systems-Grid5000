package network.node;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class PingPongImpl extends UnicastRemoteObject implements PingPongInterface {

    protected PingPongImpl() throws RemoteException {
        super();
    }

    @Override
    public int executeCommand(String command) throws RemoteException {
        try {
            Process process = Runtime.getRuntime().exec(new String[] { "/bin/bash", "-c", command });

            // Capture the standard output and error streams
            BufferedReader outputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            int exitCode = process.waitFor();

            // Print the standard output
            System.out.println("Standard Output:");
            outputReader.lines().forEach(System.out::println);

            // Print the standard error
            System.out.println("Standard Error:");
            errorReader.lines().forEach(System.out::println);

            // Print the result
            if (exitCode == 0) {
                System.out.println("Command executed successfully: " + command);
            } else {
                System.out.println("Error executing command. Exit code: " + exitCode);
            }
            return exitCode;
        } catch (IOException | InterruptedException e) {
            System.out.println("Error executing command.\n" + e);
            throw new RemoteException("Error executing command", e);
        }
    }
}
