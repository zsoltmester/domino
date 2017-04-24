package domino.model;

public class DominoCard {

    private int[] values;

    private DominoCard(int firstValue, int secondValue) {
        values = new int[]{firstValue, secondValue};
    }

    public static DominoCard fromString(String cardAsString) {
        String[] cardValuesAsString = cardAsString.split(" ");
        int firstValue = Integer.valueOf(cardValuesAsString[0]);
        int secondValue = Integer.valueOf(cardValuesAsString[1]);
        DominoCard card = new DominoCard(firstValue, secondValue);
        return card;
    }

    @Override
    public String toString() {
        return values[0] + " " + values[1];
    }
}
