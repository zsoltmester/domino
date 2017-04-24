package domino.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class DominoDeck {

    private List<DominoCard> cards = new ArrayList<>();

    public static DominoDeck fromFile(String fileName) {
        DominoDeck deck = new DominoDeck();
        Path pathToFile = Paths.get(fileName);
        try (Stream<String> lines = Files.lines(pathToFile)) {
            lines.forEach(line -> {
                DominoCard card = DominoCard.fromString(line);
                deck.addCardToEnd(card);
            });
        } catch (IOException e) {
            throw new RuntimeException("Cannot read file: " + fileName, e);
        }
        return deck;
    }

    public DominoCard drawFirst() {
        return cards.remove(0);
    }

    public void addCardToEnd(DominoCard card) {
        cards.add(card);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (DominoCard card : cards) {
            stringBuilder.append(card.toString()).append(System.lineSeparator());
        }
        return stringBuilder.toString();
    }
}
