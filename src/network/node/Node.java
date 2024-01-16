package network.node;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;

public class Node {
    public static void main(String[] args) {

        System.out.println("Node "+args[0]+" started. Creating instances ...");

        try {
            java.rmi.registry.LocateRegistry.createRegistry(3000);
            PingPongImpl impl = new PingPongImpl();
            Naming.rebind("rmi://"+args[0]+":3000/PingPongObject", impl);
            System.out.println("Registry completed. Waiting for requests ...");

        }catch(RemoteException | MalformedURLException e1) {
            System.out.println("Node failed.\n"+e1);
        }

    }

}
