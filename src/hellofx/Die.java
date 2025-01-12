package hellofx;
import java.util.Random;

/**
 * The Die class represents a six-sided die used in the board game.
 * It provides a method to roll the die and get a random result between 1 and 6.
 */
public class Die {
    private Random random = new Random();

    /**
     * Rolls the die and returns a random result between 1 and 6.
     *
     * @return a random integer between 1 and 6
     */
    public int roll() {
        return random.nextInt(6) + 1;
    }
}