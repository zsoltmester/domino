package domino.server;

import domino.DominoStorageIface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

class DominoStorageImpl extends UnicastRemoteObject implements DominoStorageIface {

    private static final long serialVersionUID = 1L;

    protected DominoStorageImpl(String databaseConnection) throws RemoteException {
        // TODO: A konstruktora egy szöveget vár, és megnyit egy hsqldb adatbázis-kapcsolatot erre a fájlra (a getConnection paraméterezésénél: jdbc:hsqldb:file:kapott_szöveg). A kapcsolat az objektum léte alatt végig maradjon fenn, és az egyszerűség kedvéért a lezárásával sem kell törődni. A konstruktor törölje az adatbázis Dominos tábláját, ha létezik, és hozza létre egy  user és egy domino szöveges oszloppal, valamint egy idx egész számmal.
    }

    @Override
    public void save(String saveName, List<String> dominos) throws RemoteException {
        // TODO A save metódusa a felhasználó nevét várja (userName) és dominók egy listáját, amelyben a dominók szövegesen, a két oldalukat szóközzel elválasztva szerepelnek. Törölje az adatbázisból az összes olyan sort, amely a kapott felhasználóhoz tartozik, majd illessze be a dominókat egytől növekvő indexszel.
    }

    @Override
    public List<String> load(String loadName) throws RemoteException {
        // TODO A load metódusa szintén userName paramétert kap, és egy listában adja vissza a felhasználó dominóit az adatbázisból. Előfordulhat, hogy a felhasználóhoz nincsen dominó rendelve, ekkor üres listát ad vissza.
        return null;
    }
}
