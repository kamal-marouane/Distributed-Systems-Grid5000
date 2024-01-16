package network.node;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PingPongInterface extends Remote {
    int executeCommand(String command) throws RemoteException;
}
