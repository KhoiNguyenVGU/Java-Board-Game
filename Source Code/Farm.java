import java.util.ArrayList;
import java.util.List;

public class Farm {
    private String color;
    private List<CornCube> cornCubes = new ArrayList<>();

    public Farm(String color) {
        this.color = color;
    }

    public void addCorn(CornCube cube) {
        cornCubes.add(cube);
    }

    public List<CornCube> getCornCubes() {
        return cornCubes;
    }

    public String getColor() {
        return color;
    }

    public void clearCorn() {
        cornCubes.clear();
    }

    @Override
    public String toString() {
        return "Farm (" + color + ") with corn: " + cornCubes;
    }
}
