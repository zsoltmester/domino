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

    public int getFirstValue() {
        return values[0];
    }

    public int getOtherValue(int value) {
        assert hasValue(value);
        return values[0] == value ? values[1] : values[0];
    }

    boolean hasValue(int value) {
        return values[0] == value || values[1] == value;
    }

    @Override
    public String toString() {
        return values[0] + " " + values[1];
    }
}
