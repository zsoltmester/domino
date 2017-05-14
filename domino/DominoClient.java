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

    protected DominoClient(String name) {
        this.name = name;
    }

    public static void main(String[] args) {
        DominoClient client = new DominoClient(args[0]);
        client.run();
    }

    @Override
    public void run() {
        joinToServer();
        sendMessageToServer(name);
        readDeck();
        play();
    }

    private void joinToServer() {
        try {
            //System.out.println("Waiting for join to the server... ");
            Socket socket = new Socket(HOST, PORT);
            reader = new Scanner(socket.getInputStream());
            writer = new PrintWriter(socket.getOutputStream());
            //System.out.println("Joined successfully!");
        } catch (IOException e) {
            throw new RuntimeException("Cannot join to server.", e);
        }
    }

    private void readDeck() {
        //System.out.println("Waiting for the deck... ");
        for (int i = 0; i < DECK_SIZE; ++i) {
            String cardAsString = readMessageFromServer();
            deck.addCardToEnd(DominoCard.fromString(cardAsString));
        }
        //System.out.println("Got starter deck:\n" + deck);
    }

    private void play() {
        //System.out.println("Playing the game...");

        boolean isGameEnded = false;
        while (!isGameEnded) {
            isGameEnded = playOneRound();
        }

        //System.out.println("Game ended!");
    }

    private boolean playOneRound() {
        String messageFromServer = readMessageFromServer();
        switch (messageFromServer) {
            case MSG_START:
                DominoCard firstCard = deck.drawFirstCard();
                sendMessageToServer(String.valueOf(firstCard.getFirstValue()));
                break;
            case MSG_DRAW:
            case MSG_LOSE:
                return true;
            default:
                if (deck.isEmpty()) {
                    sendMessageToServer(MSG_WIN);
                    return true;
                }
                int nextNumber = Integer.valueOf(messageFromServer);
                DominoCard firstMatchingCard = deck.drawFirstCardForNumber(nextNumber);
                if (firstMatchingCard == null) {
                    sendMessageToServer(MSG_GIVE_A_CARD);
                    String newMessageFromServer = readMessageFromServer();
                    if (!newMessageFromServer.equals(MSG_NO_CARD_LEFT)) {
                        DominoCard newCard = DominoCard.fromString(newMessageFromServer);
                        deck.addCardToEnd(newCard);
                    }
                } else {
                    int newNextNumber = firstMatchingCard.getOtherValue(nextNumber);
                    sendMessageToServer(String.valueOf(newNextNumber));
                }
        }
        return false;
    }

    private String readMessageFromServer() {
        String message = reader.nextLine();
        //System.out.println("Read from server: " + message);
        return message;
    }

    private void sendMessageToServer(String message) {
        //System.out.println("Sending to server: " + message);
        writer.println(message);
        writer.flush();
    }
}
