package domino;

import domino.model.DominoCard;
import domino.model.DominoDeck;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import static domino.Config.*;

public class DominoClient implements Runnable {

    private String name;

    private Scanner reader;
    private PrintWriter writer;

    private DominoDeck deck = new DominoDeck();

    private DominoClient(String name) {
        this.name = name;
    }

    public static void main(String[] args) {
        DominoClient client = new DominoClient(args[0]);
        new Thread(client).start();
    }

    @Override
    public void run() {
        joinToServer();
        sendMessageToServer(name);
        readDeck();
    }

    private void joinToServer() {
        try {
            System.out.print("Waiting for join to the server... ");
            Socket socket = new Socket(HOST, PORT);
            reader = new Scanner(socket.getInputStream());
            writer = new PrintWriter(socket.getOutputStream());
            System.out.println("Joined successfully!");
        } catch (IOException e) {
            throw new RuntimeException("Cannot join to server.", e);
        }
    }

    private void readDeck() {
        System.out.print("Waiting for the deck... ");
        for (int i = 0; i < DECK_SIZE; ++i) {
            String cardAsString = readMessageFromServer();
            deck.addCardToEnd(DominoCard.fromString(cardAsString));
        }
        System.out.println("Got starter deck:\n" + deck);
    }

    private String readMessageFromServer() {
        return reader.nextLine();
    }

    private void sendMessageToServer(String message) {
        writer.println(message);
        writer.flush();
    }
}
