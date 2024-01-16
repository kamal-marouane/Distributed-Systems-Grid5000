package test.node;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class PingPongImpl extends UnicastRemoteObject implements PingPongInterface {

  protected PingPongImpl() throws RemoteException {
    super();
  }

  @Override
  public String ping(String command, String host) throws RemoteException {
    return "Node " + host + " received: " + command;
  }
}
