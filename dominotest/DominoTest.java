package dominotest;
import static java.nio.file.Files.readAllLines;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;

import domino.DominoClient;
import domino.DominoServer;

public class DominoTest {
    @Test(timeout=4000)
    public void testPlayerCount2() throws Exception {
        // Ehhez hasonloan inditja el a foprogramokat:
        // java DominoServer input1.txt output1_2.txt
        // java DominoClient Jatekos1
        // java DominoClient Jatekos2
        playStandardGame(2, 2, "input1.txt", "output1_2.txt", "output1_2_mo.txt");
    }

    @Test(timeout=4000)
    public void testPlayerCount1() throws Exception {
        // DominoServer ket klienst indit, mert annyi a minimum
        playStandardGame(1, 2, "input1.txt", "output1_1.txt", "output1_2_mo.txt");
    }

    @Test(timeout=4000)
    public void testPlayerCount3() throws Exception {
        playStandardGame(3, 3, "input1.txt", "output1_3.txt", "output1_3_mo.txt");
    }

    @Test(timeout=4000)
    public void testPlayerCount10000() throws Exception {
        playStandardGame(10000, 2, "input1.txt", "output1_10000.txt", "output1_2_mo.txt");
    }

    @Test(timeout=4000)
    public void testInput2() throws Exception {
        playStandardGame(3, 3, "input2.txt", "output2.txt", "output2_mo.txt");
    }

    @Test(timeout=4000)
    public void testPlayerCount4Input3() throws Exception {
        playStandardGame(4, 4, "input3.txt", "output3_4.txt", "output3_4_mo.txt");
    }

    @Test(timeout=4000)
    public void testPlayerCount4Input4() throws Exception {
        playStandardGame(4, 4, "input4.txt", "output4_4.txt", "output4_4_mo.txt");
    }

    public void playStandardGame(int serverPlayerCount, int realPlayerCount, String inputFilename, String outputFilename, String sampleOutputFilename)
            throws InterruptedException, IOException {
        // DominoServer elinditasa
        runMain(1,
                args -> DominoServer.main(args),
                n -> serverPlayerCount + " " + inputFilename + " " + outputFilename,
                "DServer");

        sleepMsec(100);

        // DominoClient elinditasa
        runMain(realPlayerCount,
                args -> DominoClient.main(args),
                n -> "Jatekos" + n,
                "DClient");

        waitForMainsToFinish();

        compareWithExpectedFile(outputFilename, sampleOutputFilename);
    }

    // ----------------------------------------
    // Technikai reszletek

    List<Thread> mainThreads;

    @Before
    public void before() {
        mainThreads = new ArrayList<>();
    }

    public void waitForMainsToFinish() throws InterruptedException {
        for (Thread thread : mainThreads) {
            thread.join();
        }

        mainThreads = new ArrayList<>();
    }

    public void runMain(int count, ConsumerEx<String[]> main, Function<Integer, String> getServerArgsTxt, String threadNamePrefix) {
        for (int i = 1; i <= count; i++) {
            String[] serverArgs = getServerArgsTxt.apply(i).split(" ");

            String threadName = threadNamePrefix + (count == 1 ? "" : i);

            Thread thread = new Thread(withoutException(() -> main.accept(serverArgs)), threadName);
            thread.start();

            mainThreads.add(thread);

            sleepMsec(50);
        }
    }

    public void compareWithExpectedFile(String filename, String filename2) throws IOException {
        List<String> lines1 = readAllLines(Paths.get(filename));
        List<String> lines2 = readAllLines(Paths.get(filename2));
        if (lines1.size() != lines2.size()) {
            fail("Differing number of lines in files: " + filename + ", " + filename2);
        }

        for (int i = 0; i < lines1.size(); i++) {
            if (!lines1.get(i).equals(lines2.get(i))) {
                String msg = String.format("Line %d differs in %s (\"%s\") and %s (\"%s\")", i+1, filename, lines1.get(i), filename2, lines2.get(i));
                fail(msg);
            }
        }
    }

    private static void sleepMsec(int msec) {
        try {
            Thread.sleep(msec);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public static Runnable withoutException(RunnableEx r) {
        return () -> {
            try {
                r.run();
            } catch (Exception e) {
                synchronized (System.out) {
                    System.out.println("A futas soran hiba lepett fel a " + Thread.currentThread().getName() + " szalban:");
                    e.printStackTrace(System.out);
                }
            }
        };
    }

    public static <T> Consumer<T> withoutExceptionC(ConsumerEx<T> c) {
        return args -> {
            try {
                c.accept(args);
            } catch (Exception e) {
                synchronized (System.out) {
                    System.out.println("A futas soran hiba lepett fel a " + Thread.currentThread().getName() + " szalban:");
                    e.printStackTrace(System.out);
                }
            }
        };
    }

    interface ConsumerEx<T> {
        public void accept(T t) throws Exception;
    }

    interface RunnableEx {
        public void run() throws Exception;
    }

}
