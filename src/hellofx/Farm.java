package hellofx;

import java.util.ArrayList;
import java.util.List;

/**
 * The Farm class represents a farm in the board game.
 * Each farm has a color and a list of corn cubes.
 */
public class Farm {
    private String color;
    private List<CornCube> cornCubes = new ArrayList<>();

    /**
     * Constructs a new Farm with the specified color.
     *
     * @param color the color of the farm
     */
    public Farm(String color) {
        this.color = color;
    }

    /**
     * Adds a corn cube to the farm.
     *
     * @param cube the corn cube to add
     */
    public void addCorn(CornCube cube) {
        cornCubes.add(cube);
    }

    /**
     * Returns the list of corn cubes in the farm.
     *
     * @return the list of corn cubes
     */
    public List<CornCube> getCornCubes() {
        return cornCubes;
    }

    /**
     * Returns the color of the farm.
     *
     * @return the color of the farm
     */
    public String getColor() {
        return color;
    }

    /**
     * Clears all corn cubes from the farm.
     */
    public void clearCorn() {
        cornCubes.clear();
    }

    /**
     * Returns a string representation of the farm.
     *
     * @return a string representation of the farm
     */
    @Override
    public String toString() {
        return "Farm (" + color + ") with corn: " + cornCubes;
    }
}