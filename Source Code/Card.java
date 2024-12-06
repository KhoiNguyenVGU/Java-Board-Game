public class Card {
    public enum Type { BIRD, FOX, FLEEING_BIRD }
    private Type type;
    private int value;
    private String color;

    public Card(Type type, int value, String color) {
        this.type = type;
        this.value = value;
        this.color = color;
    }

    public Type getType() { return type; }
    public int getValue() { return value; }
    public String getColor() { return color; }

    @Override
    public String toString() {
        return type + " (" + value + ", " + color + ")";
    }
}
