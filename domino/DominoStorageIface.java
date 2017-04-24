package domino;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface DominoStorageIface extends Remote {
    void save(String saveName, List<String> dominos) throws RemoteException;
    List<String> load(String loadName) throws RemoteException;
}
