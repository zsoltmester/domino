package domino;

import domino.model.DominoCard;
import domino.model.DominoDeck;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static domino.Config.*;

public class DominoServer implements Runnable {

    private int numberOfPlayers;
    private String inputFileName;
    private String outputFileName;

    private ServerSocket serverSocket;
    private List<Client> clients = new ArrayList<>();

    private DominoDeck deck;
    private int nextNumber = -1;

    private List<String> events = new ArrayList<>();

    private DominoServer(int numberOfPlayers, String inputFileName, String outputFileName) {
        this.numberOfPlayers = numberOfPlayers;
        this.inputFileName = inputFileName;
        this.outputFileName = outputFileName;
    }

    public static void main(String[] args) {
        DominoServer server = new DominoServer(Integer.valueOf(args[0]), args[1], args[2]);
        server.run();
    }

    @Override
    public void run() {
        updateNumberOfPlayers();
        readDeck();
        waitForClients();
        sendDecks();
        play();
        writeEventsToFile();
        closeServer();
    }

    private void updateNumberOfPlayers() {
        if (numberOfPlayers < 2 || numberOfPlayers > 4) {
            System.out.println(MSG_INVALID_NUMBER_OF_PLAYERS);
            numberOfPlayers = 2;
        }
        System.out.println("numberOfPlayers: " + numberOfPlayers);
    }

    private void readDeck() {
        deck = DominoDeck.fromFile(inputFileName);
        System.out.println("Untouched deck:\n" + deck);
    }

    private void waitForClients() {
        try {
            serverSocket = new ServerSocket(PORT);
            while (clients.size() < numberOfPlayers) {
                System.out.println("Waiting for a client to join... ");
                Socket clientSocket = serverSocket.accept();
                Client client = new Client(null, clientSocket);
                String clientName = client.read();
                client.name = clientName;
                clients.add(client);
                System.out.println("A client joined: " + client);
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot initialise the server or the clients.", e);
        }
    }

    private void sendDecks() {
        System.out.println("Sending the decks... ");
        for (Client client : clients) {
            for (int i = 0; i < DECK_SIZE; ++i) {
                DominoCard card = deck.drawFirstCard();
                client.write(card.toString());
            }
        }
        System.out.println("Decks are sent. Left:\n" + deck);
    }

    private void play() {
        System.out.println("Playing the game...");
        boolean isGameEnded = false;
        while (!isGameEnded) {
            isGameEnded = playOneRound();
        }
        System.out.println("Game ended!");
    }

    private boolean playOneRound() {
        boolean anyMatch = false;

        for (Client client : clients) {
            if (nextNumber == -1) {
                client.write(MSG_START);
                nextNumber = Integer.valueOf(client.read());
                events.add(client + ": " + nextNumber);
                anyMatch = true;
                continue;
            }

            client.write(String.valueOf(nextNumber));
            String messageFromClient = client.read();
            events.add(client + ": " + messageFromClient);
            switch (messageFromClient) {
                case MSG_GIVE_A_CARD:
                    DominoCard cardToGive = deck.drawFirstCard();
                    if (cardToGive == null) {
                        client.write(MSG_NO_CARD_LEFT);
                    } else {
                        client.write(cardToGive.toString());
                    }
                    break;
                case MSG_WIN:
                    for (Client otherClient : clients) {
                        if (otherClient != client) {
                            otherClient.write(MSG_LOSE);
                        }
                    }
                    return true;
                default:
                    anyMatch = true;
                    nextNumber = Integer.valueOf(messageFromClient);
            }
        }

        if (!anyMatch) {
            for (Client client : clients) {
                client.write(MSG_DRAW);
            }
            events.add(MSG_DRAW);
            return true;
        }

        return false;
    }

    private void writeEventsToFile() {
        Path outputFile = Paths.get(outputFileName);
        try {
            Files.write(outputFile, events, Charset.forName("UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException("Cannot write to file: " + outputFileName, e);
        }
    }

    private void closeServer() {
        try {
            serverSocket.close();
            for (Client client : clients) {
                client.close();
            }
        } catch (IOException e) {
            // Maybe the port will be in use for a while, but we can't do anything.
        }
    }

    private class Client {

        private String name;

        private Socket socket;
        private Scanner reader;
        private PrintWriter writer;

        private Client(String name, Socket socket) {
            this.name = name;
            this.socket = socket;
            try {
                reader = new Scanner(socket.getInputStream());
                writer = new PrintWriter(socket.getOutputStream());
            } catch (IOException e) {
                throw new RuntimeException("Cannot create a writer or reader for a client.", e);
            }
        }

        private String read() {
            String message = reader.nextLine();
            System.out.println(name + " sent a message: " + message);
            return message;
        }

        private void write(String message) {
            System.out.println("Sending message to " + name + ": " + message);
            writer.println(message);
            writer.flush();
        }

        private void close() throws IOException {
            socket.close();
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
