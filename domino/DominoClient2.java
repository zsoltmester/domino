package domino;

public class DominoClient2 extends DominoClient {

    private DominoClient2(String name) {
        super(name);
    }

    public static void main(String[] args) {
        if (args.length == 1) {
            DominoClient.main(args);
            return;
        }

        // TODO A kliens az eredeti paraméter mellett kap még egy egész számot (saveAfterStep), az RMI szolgáltatás nevét (dominoStorageName) és egy felhasználónevet (userName).
        // A kliens felveszi a kapcsolatot az RMI szerverrel, és betölti a kapott felhasználónévhez tartozó dominókat.
        // Ezután a szervertől megkapja a 7 dominót. Ha kevesebb mint 7 dominót kapott RMI-n, akkor ezekből sorban feltölti a készletét 7 dominóig; a felesleget eldobja.
        // Ezután a szokásos módon kommunikál a szerverrel, azonban számolja, hogy hány lépést tett meg. Egy lépés akkor kezdődik meg, amikor a kliens a szervertől a következő üzenetet fogadja (az UJ üzenetre küldött választ ebbe bele nem értve).
        // Amikor saveAfterStep lépés megtörtént, ebbe beleértve a beküldött dominó eltávolítását, illetve az UJ dominó beérkezését, akkor a kliens felveszi a kapcsolatot az RMI szerverrel, és feltölti rá a dominóit a felhasználónevén. (Csak egyszer! Nem kell saveAfterStep lépésenként ezt megismételni, csak az első saveAfterStep lépés után mentünk.)
    }
}
