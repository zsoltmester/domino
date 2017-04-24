package dominotest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import domino.DominoClient2;
import domino.DominoServer;
import domino.DominoStorageIface;
import domino.server.DominoDeploy;

public class DominoRmiTest {
    @Test(timeout=4000)
    public void testRmiInput1() throws Exception {
        // 1. jatek, az eredmeny meg kell, hogy egyezzen testPlayerCount3 lefutasaval
        playRmiStorageGame(3, 3, "input1.txt", "output1_3_rmi.txt", "output1_3_mo.txt");

        // 2. jatek, a kliensek RMI-n keresztul betoltik, amit elmentettek
        // Jatekos1 (user1): 2 9, 3 7, 1 1, 0 3, 6 8
        // Jatekos2 (user2): 4 9, 3 9, 5 7, 4 8, 0 2
        // Jatekos3 (user3): 3 5, 0 7, 0 9, 0 5, 4 4
        playRmiStorageGame(3, 3, "input1.txt", "output1_3b_rmi.txt", "output1_3b_rmi_mo.txt");
    }

    @Test(timeout=4000)
    public void testRmiInput4() throws Exception {
        playRmiStorageGame(3, 3, "input4.txt", "output4_3_rmi.txt", "output4_3_rmi_mo.txt");
        playRmiStorageGame(3, 3, "input4.txt", "output4_3b_rmi.txt", "output4_3b_rmi_mo.txt");
    }

    public void playRmiStorageGame(int serverPlayerCount, int realPlayerCount, String inputFilename, String outputFilename, String sampleOutputFilename)
            throws InterruptedException, IOException {
        // DominoServer elinditasa
        dt.runMain(1,
                args -> DominoServer.main(args),
                n -> serverPlayerCount + " " + inputFilename + " " + outputFilename,
                "DServer");

        // DominoClient2 elinditasa
        dt.runMain(realPlayerCount,
                args -> DominoClient2.main(args),
                // a kliens parameterezese valtozott a playStandardGame-hez kepest
                n -> ("Jatekos" + n) + " 2 dominoStorage " + ("user" + n),
                "DClient");

        dt.waitForMainsToFinish();

        dt.compareWithExpectedFile(outputFilename, sampleOutputFilename);
    }

    // ----------------------------------------
    // Technikai reszletek

    final static String DOMINO_DB_DIR = "domino_db_dir";
    final static String DOMINO_RMI_NAME = "dominoStorage";
    static Registry reg;
    DominoTest dt;

    @BeforeClass
    public static void beforeClass() throws Exception {
        reg = LocateRegistry.createRegistry(1099);
    }

    @Before
    public void before() throws Exception {
        deleteDirIfExists(DOMINO_DB_DIR);

        dt = new DominoTest();
        dt.before();

        String dbFilePath = Paths.get(DOMINO_DB_DIR, "dominodb").toString();
        forceUnbindService(DOMINO_RMI_NAME);
        DominoDeploy.deploy(reg, DOMINO_RMI_NAME, dbFilePath);
    }

    private void forceUnbindService(String DOMINO_RMI_NAME) throws RemoteException, AccessException {
        try {
            reg.lookup(DOMINO_RMI_NAME);
            reg.unbind(DOMINO_RMI_NAME);
        } catch (NotBoundException e) {
        }
    }

    private static void deleteDirIfExists(String dominoDbDir) {
        File file = new File(dominoDbDir);
        if (file.isDirectory()) {
            file.delete();
        }
    }
}
