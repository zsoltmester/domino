package domino;

// Ezután a szokásos módon kommunikál a szerverrel, azonban számolja, hogy hány lépést tett meg. Egy lépés akkor kezdődik meg, amikor a kliens a szervertől a következő üzenetet fogadja (az UJ üzenetre küldött választ ebbe bele nem értve).
// Amikor saveAfterStep lépés megtörtént, ebbe beleértve a beküldött dominó eltávolítását, illetve az UJ dominó beérkezését, akkor a kliens felveszi a kapcsolatot az RMI szerverrel, és feltölti rá a dominóit a felhasználónevén. (Csak egyszer! Nem kell saveAfterStep lépésenként ezt megismételni, csak az első saveAfterStep lépés után mentünk.)


import domino.model.DominoCard;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.List;

public class DominoClient2 extends DominoClient {

    private final int saveAfterStep;
    private final String userName;
    private final DominoStorageIface dominoStorage;

    private int currentStep = 0;

    private DominoClient2(String name, int saveAfterStep, String userName, DominoStorageIface dominoStorage) {
        super(name);
        this.saveAfterStep = saveAfterStep;
        this.userName = userName;
        this.dominoStorage = dominoStorage;
    }

    public static void main(String[] args) {
        if (args.length == 1) {
            DominoClient.main(args);
            return;
        }

        Registry registry;
        DominoStorageIface dominoStorage;
        try {
            registry = LocateRegistry.getRegistry();
            dominoStorage = (DominoStorageIface) registry.lookup(args[2]);
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }

        DominoClient2 client = new DominoClient2(args[0], Integer.valueOf(args[1]), args[3], dominoStorage);
        client.run();
    }

    @Override
    protected void readDeck() {
        try {
            List<String> storedDeck = dominoStorage.load(userName);
            for (String card : storedDeck) {
                deck.addCardToEnd(DominoCard.fromString(card));
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }

        super.readDeck();
    }

    @Override
    protected boolean playOneRound() {
        boolean isGameEnded = super.playOneRound();

        currentStep++;
        if (currentStep == saveAfterStep) {
            try {
                dominoStorage.save(userName, deck.getAsRawCardList());
            } catch (RemoteException e) {
                e.printStackTrace();
                throw new RuntimeException();
            }
        }

        return isGameEnded;
    }
}
