package hellofx;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameController {

    private Game game;

    @FXML
    private ImageView image1, image2, image3, image4, image5, image6;

    @FXML
    private GridPane cardGrid;

    @FXML
    private VBox cardPile;

    @FXML
    private VBox cubeBox;

    @FXML
    private FlowPane cubeFlowPane;

    public void setGame(Game game) {
        this.game = game;
        initializeGame(); // Initialize game-related components
    }

    public void initialize() {
        setImages();
        createCubes();
    }

    private void initializeGame() {
        createAndShuffleCards();
    }

    public void setImages() {
        try {
            Image img1 = new Image(getClass().getResource("/hellofx/images/Screenshot 2024-12-18 143410.png").toExternalForm());
            Image img2 = new Image(getClass().getResource("/hellofx/images/Screenshot 2024-12-18 144102.png").toExternalForm());
            Image img3 = new Image(getClass().getResource("/hellofx/images/Screenshot 2024-12-18 144716.png").toExternalForm());
            Image img4 = new Image(getClass().getResource("/hellofx/images/Screenshot 2024-12-18 144858.png").toExternalForm());
            Image img5 = new Image(getClass().getResource("/hellofx/images/Screenshot 2024-12-18 144938.png").toExternalForm());
            Image img6 = new Image(getClass().getResource("/hellofx/images/Screenshot 2024-12-18 145010.png").toExternalForm());

            // Setting the images to the corresponding ImageView elements
            image1.setImage(img1);
            image2.setImage(img2);
            image3.setImage(img3);
            image4.setImage(img4);
            image5.setImage(img5);
            image6.setImage(img6);
        } catch (NullPointerException e) {
            System.err.println("Image resource not found: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createAndShuffleCards() {
        List<StackPane> cards = new ArrayList<>();
        for (Card card : game.getDeck()) {
            StackPane cardPane = new StackPane();
            cardPane.setStyle("-fx-border-color: black; -fx-pref-width: 100; -fx-pref-height: 150;");
            cardPane.setStyle("-fx-background-color: " + card.getColor() + ";");
            Label label = new Label(card.toString());
            cardPane.getChildren().add(label);
            cards.add(cardPane);
        }

        // Add shuffled cards to the VBox (pile)
        cardPile.getChildren().addAll(cards);
    }

    private void createCubes() {
        List<Box> cubes = new ArrayList<>();
        for (int i = 0; i < 26; i++) {
            cubes.add(createCube(Color.YELLOW));
            cubes.add(createCube(Color.GREEN));
            cubes.add(createCube(Color.BLUE));
        }

        // Shuffle the cubes
        Collections.shuffle(cubes);

        // Add cubes to the FlowPane (cubeFlowPane)
        cubeFlowPane.getChildren().addAll(cubes);
    }

    private Box createCube(Color color) {
        Box cube = new Box(20, 20, 20); // Smaller cubes
        cube.setMaterial(new PhongMaterial(color));
        return cube;
    }
}