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

    public void addCardToEnd(DominoCard card) {
        cards.add(card);
    }

    public DominoCard drawFirstCard() {
        return cards.size() == 0 ? null : cards.remove(0);
    }

    public DominoCard drawFirstCardForNumber(int number) {
        DominoCard firstMatchingCard = null;
        for (DominoCard card : cards) {
            if (card.hasValue(number)) {
                firstMatchingCard = card;
                break;
            }
        }
        if (firstMatchingCard != null) {
            cards.remove(firstMatchingCard);
        }
        return firstMatchingCard;
    }

    public boolean isEmpty() {
        return cards.size() == 0;
    }

    public int size() {
        return cards.size();
    }

    public List<String> getAsRawCardList() {
        List<String> rawCardList = new ArrayList<>();
        for (DominoCard card : cards) {
            rawCardList.add(card.toString());
        }
        return rawCardList;
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
