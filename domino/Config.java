package domino;

import java.io.File;

public class Config {

    static final String HOST = "localhost";
    static final int PORT = 60504;

    public static final String STORAGE_SERVICE = "storage";
    public static final String STORAGE_DATABASE = "domino_storage" + File.separator + "domino_db";

    static final int DECK_SIZE = 7;

    static final String MSG_INVALID_NUMBER_OF_PLAYERS = "Nem megfelelo a jatekosok szama.";
    static final String MSG_START = "START";
    static final String MSG_GIVE_A_CARD = "UJ";
    static final String MSG_NO_CARD_LEFT = "NINCS";
    static final String MSG_WIN = "NYERTEM";
    static final String MSG_LOSE = "VEGE";
    static final String MSG_DRAW = "DONTETLEN";
}
