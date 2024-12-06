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
        scorePile.add(item);
    }

    // public int calculateScore() {
    //     int total = 0;
    //     for (Object obj : scorePile) {
    //         if (obj instanceof Card) {
    //             Card card = (Card) obj;
    //             total += card.getValue();
    //         } else if (obj instanceof CornCube) {
    //             total += ((CornCube) obj).getPoints();
    //         }
    //     }
    //     return total;
    // }

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
            } else {
                System.out.println("Unknown object in scorePile: " + obj.getClass().getName());
            }
        }
        System.out.println("Total score: " + total);
        return total;
    }

    @Override
    public String toString() {
        return name + " (Score: " + calculateScore() + ")";
    }
}
