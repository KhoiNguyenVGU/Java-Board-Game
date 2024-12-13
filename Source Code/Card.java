/**
 * The Card class represents a card in the board game.
 * Each card has a type, value, and color.
 */
public class Card {

    /**
     * Enum representing the type of the card.
     */
    public enum Type { BIRD, FOX, FLEEING_BIRD }

    private Type type;
    private int value;
    private String color;

    /**
     * Constructs a new Card with the specified type, value, and color.
     *
     * @param type  the type of the card
     * @param value the value of the card
     * @param color the color of the card
     */
    public Card(Type type, int value, String color) {
        this.type = type;
        this.value = value;
        this.color = color;
    }

    /**
     * Returns the type of the card.
     *
     * @return the type of the card
     */
    public Type getType() { return type; }

    /**
     * Returns the value of the card.
     *
     * @return the value of the card
     */
    public int getValue() { return value; }

    /**
     * Returns the color of the card.
     *
     * @return the color of the card
     */
    public String getColor() { return color; }

    /**
     * Returns a string representation of the card.
     *
     * @return a string representation of the card
     */
    @Override
    public String toString() {
        return type + " (" + value + ", " + color + ")";
    }
}