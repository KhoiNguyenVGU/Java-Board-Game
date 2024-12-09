import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private List<Card> hand = new ArrayList<>();
    private List<Object> scorePile = new ArrayList<>(); // Includes Corn and Birds

    public Player(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public List<Card> getHand() { return hand; }
    public List<Object> getScorePile() { return scorePile; }

    public void addCard(Card card) {
        hand.add(card);
    }

    public void addToScore(Object item) {
        if (item instanceof Integer) {
            // Handle integer points directly
            scorePile.add(item);
        } else if (item instanceof Card) {
            // Handle card directly
            scorePile.add(item);
        } else if (item instanceof CornCube) {
            // Handle CornCube directly
            scorePile.add(item);
        } else {
            System.out.println("Unknown object in addToScore: " + item.getClass().getName());
        }
    }

    public int calculateScore() {
        int total = 0;
        for (Object obj : scorePile) {
            if (obj instanceof Card) {
                Card card = (Card) obj;
                total += card.getValue();
                System.out.println("Added Card value: " + card.getValue());
            } else if (obj instanceof CornCube) {
                CornCube cornCube = (CornCube) obj;
                total += cornCube.getPoints();
                System.out.println("Added CornCube points: " + cornCube.getPoints());
            } else if (obj instanceof Integer) {
                total += (Integer) obj;
                System.out.println("Added Integer points: " + obj);
            } else {
                System.out.println("Unknown object in scorePile: " + obj.getClass().getName());
            }
        }
        System.out.println("Total score: " + total);
        return total;
    }

    public void printScorePile() {
        for (Object obj : scorePile) {
            if (obj instanceof Card) {
                Card card = (Card) obj;
                System.out.println("Card: " + card);
            } else if (obj instanceof CornCube) {
                CornCube cornCube = (CornCube) obj;
                System.out.println("CornCube: " + cornCube);
            } else if (obj instanceof Integer) {
                System.out.println("Integer points: " + obj);
            } else {
                System.out.println("Unknown object: " + obj.getClass().getName());
            }
        }
        System.out.println("\n");
    }

    @Override
    public String toString() {
        return name + " (Score: " + calculateScore() + ")";
    }
}