package hellofx;
/**
 * The CornCube class represents a corn cube in the board game.
 * Each corn cube has a type which determines its points.
 */
public class CornCube {

    /**
     * Enum representing the type of the corn cube.
     */
    public enum CornType { GREEN, BLUE, YELLOW }

    private CornType type;

    /**
     * Constructs a new CornCube with the specified type.
     *
     * @param type the type of the corn cube
     */
    public CornCube(CornType type) {
        this.type = type;
    }

    /**
     * Returns the points associated with the corn cube type.
     *
     * @return the points of the corn cube
     */
    public int getPoints() {
        switch (type) {
            case GREEN: return 1;
            case BLUE: return 2;
            case YELLOW: return 3;
        }
        return 0;
    }

    /**
     * Returns the type of the corn cube.
     *
     * @return the type of the corn cube
     */
    public CornType getType() {
        return type;
    }

    /**
     * Returns a string representation of the corn cube.
     *
     * @return a string representation of the corn cube
     */
    @Override
    public String toString() {
        return type.name();
    }
}