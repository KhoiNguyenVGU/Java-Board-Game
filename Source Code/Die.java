import java.util.Random;

public class Die {
    private Random random = new Random();

    public int roll() {
        return random.nextInt(6) + 1; // 1 to 6
    }
}
