package hellofx;

import java.util.ArrayList;
import java.util.List;

/**
 * The Player class represents a player in the board game.
 * Each player has a name, a hand of cards, and a score pile that includes cards and corn cubes.
 */
public class Player {
    private String name;
    private List<Card> hand = new ArrayList<>();
    private List<Object> scorePile = new ArrayList<>(); // Includes Corn and Birds

    private int points;

    /**
     * Constructs a new Player with the specified name.
     *
     * @param name the name of the player
     */
    public Player(String name) {
        this.name = name;
        this.points = 0; // Initialize points to 0
    }

    /**
     * Returns the name of the player.
     *
     * @return the name of the player
     */
    public String getName() { return name; }

    /**
     * Returns the hand of the player.
     *
     * @return the hand of the player
     */
    public List<Card> getHand() { return hand; }

    /**
     * Returns the score pile of the player.
     *
     * @return the score pile of the player
     */
    public List<Object> getScorePile() { return scorePile; }

    /**
     * Adds a card to the player's hand.
     *
     * @param card the card to add
     */
    public void addCard(Card card) {
        hand.add(card);
    }

    /**
     * Adds an item to the player's score pile.
     * The item can be either a Card or a CornCube.
     *
     * @param item the item to add
     */
    public void addToScore(Object item) {
        if (item instanceof Card) {
            // Handle card directly
            scorePile.add(item);
        } else if (item instanceof CornCube) {
            // Handle CornCube directly
            scorePile.add(item);
        }
    }

    /**
     * Calculates the total score of the player based on the items in the score pile.
     *
     * @return the total score of the player
     */
    public int calculateScore() {
        int total = 0;
        for (Object obj : scorePile) {
            if (obj instanceof Card) {
                Card card = (Card) obj;
                total += card.getValue();
                // System.out.println("Added Card value: " + card.getValue());
            } else if (obj instanceof CornCube) {
                CornCube cornCube = (CornCube) obj;
                total += cornCube.getPoints();
                // System.out.println("Added CornCube points: " + cornCube.getPoints());
            }
        }
        // System.out.println("Total score: " + total);
        return total;
    }

    /**
     * Prints the items in the player's score pile.
     */
    public void printScorePile() {
        for (Object obj : scorePile) {
            if (obj instanceof Card) {
                Card card = (Card) obj;
                System.out.println("Card: " + card);
            } else if (obj instanceof CornCube) {
                CornCube cornCube = (CornCube) obj;
                System.out.println("CornCube: " + cornCube);
            }
        }
        System.out.print("\n");
    }

    public int getPoints() {
        return points;
    }
    
    public void setPoints(int points) {
        this.points = points;
    }

    /**
     * Returns a string representation of the player.
     *
     * @return a string representation of the player
     */
    @Override
    public String toString() {
        return name + " (Score: " + calculateScore() + ")";
    }
}