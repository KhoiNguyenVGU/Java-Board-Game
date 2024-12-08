public class CornCube {
    public enum CornType { GREEN, BLUE, YELLOW }
    private CornType type;

    public CornCube(CornType type) {
        this.type = type;
    }

    public int getPoints() {
        switch (type) {
            case GREEN: return 1;
            case BLUE: return 2;
            case YELLOW: return 3;
        }
        return 0;
    }

    public CornType getType() {
        return type;
    }

    @Override
    public String toString() {
        return type.name();
    }
}
