package domino.server;

import domino.DominoStorageIface;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;

import static domino.Config.STORAGE_DATABASE;
import static domino.Config.STORAGE_SERVICE;

public class DominoDeploy {

    public static void deploy(Registry registry, String service, String database) throws RemoteException, AlreadyBoundException, SQLException, ClassNotFoundException {
        DominoStorageIface storage = new DominoStorageImpl(database);
        registry.bind(service, storage);
    }

    public static void main(String[] args) throws RemoteException, AlreadyBoundException, SQLException, ClassNotFoundException {
        Registry registry = LocateRegistry.getRegistry();
        deploy(registry, STORAGE_SERVICE, STORAGE_DATABASE);
    }
}
