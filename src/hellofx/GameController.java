package hellofx;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import javafx.scene.paint.PhongMaterial;
import javafx.event.ActionEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GameController {

    @FXML
    private StackPane area1, area2, area3, area4, area5, area6;

    @FXML
    private GridPane cardGrid;

    @FXML
    private VBox cardPile;

    @FXML
    private VBox cubeBox;

    @FXML
    private FlowPane cubeFlowPane;

    @FXML
    private ImageView diceImage;

    @FXML
    private Button rollButton;

    @FXML
    private Button placeCubesButton;

    @FXML
    private Button addCardsButton;

    @FXML
    private VBox player1CardPile, player2CardPile, player3CardPile;

    @FXML
    private Button addCardsButton1, addCardsButton2, addCardsButton3;

    @FXML
    private Label player1ScoreLabel, player2ScoreLabel, player3ScoreLabel;

    private Random random = new Random();

    private Game game;

    private Map<Color, StackPane> colorToAreaMap;
    private Map<Color, Integer> colorToValueMap;
    private Map<StackPane, Integer> areaTotalValueMap;

    private boolean player1CardSelected = false;
    private boolean player2CardSelected = false;
    private boolean player3CardSelected = false;

    private int player1Score = 0;
    private int player2Score = 0;
    private int player3Score = 0;

    private int currentPlayerTurn = 1;

    public void setGame(Game game) {
        this.game = game;
        initializeGame(); // Initialize game-related components
        addCardsToPlayerPile(5, player1CardPile); // Add 5 cards to player 1's pile at the start
        addCardsToPlayerPile(5, player2CardPile); // Add 5 cards to player 2's pile at the start
        addCardsToPlayerPile(5, player3CardPile); // Add 5 cards to player 3's pile at the start
    }

    @FXML
    public void initialize() {
        createCubes();
        setAreaColors();
        updateAddCardsButtons();
        initializeColorToAreaMap();
        initializeColorToValueMap();
        initializeAreaTotalValueMap();
        updatePlayerScores();
        chooseCardsInOrder();
    }

    private void initializeGame() {
        createAndShuffleCards();
    }

    private void initializeColorToAreaMap() {
        colorToAreaMap = new HashMap<>();
        colorToAreaMap.put(Color.YELLOW, area1);
        colorToAreaMap.put(Color.GREEN, area2);
        colorToAreaMap.put(Color.RED, area3);
        colorToAreaMap.put(Color.BLUE, area4);
        colorToAreaMap.put(Color.PURPLE, area5);
        colorToAreaMap.put(Color.ORANGE, area6); // Assuming area6 is for orange cards
    }

    private void initializeColorToValueMap() {
        colorToValueMap = new HashMap<>();
        colorToValueMap.put(Color.GREEN, 1);
        colorToValueMap.put(Color.BLUE, 2);
        colorToValueMap.put(Color.YELLOW, 3);
    }

    private void initializeAreaTotalValueMap() {
        areaTotalValueMap = new HashMap<>();
        areaTotalValueMap.put(area1, 0);
        areaTotalValueMap.put(area2, 0);
        areaTotalValueMap.put(area3, 0);
        areaTotalValueMap.put(area4, 0);
        areaTotalValueMap.put(area5, 0);
        areaTotalValueMap.put(area6, 0);
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

        // Shuffle the cards
        Collections.shuffle(cards);

        // Clear the existing cards in the pile and add the shuffled cards
        cardPile.getChildren().clear();
        cardPile.getChildren().addAll(cards);
    }

    private void createCubes() {
        List<StackPane> cubes = new ArrayList<>();
        for (int i = 0; i < 26; i++) {
            cubes.add(createCubeWithBorder(Color.YELLOW));
            cubes.add(createCubeWithBorder(Color.GREEN));
            cubes.add(createCubeWithBorder(Color.BLUE));
        }

        // Shuffle the cubes to place them randomly
        Collections.shuffle(cubes);

        cubeFlowPane.getChildren().addAll(cubes);
    }

    private StackPane createCubeWithBorder(Color color) {
        StackPane stack = new StackPane();

        // Create the border box
        Box borderBox = new Box(22, 22, 22); // Slightly larger for the border
        borderBox.setMaterial(new PhongMaterial(Color.BLACK));

        // Create the inner cube
        Box cube = new Box(20, 20, 20); // Smaller cube
        cube.setMaterial(new PhongMaterial(color));

        stack.getChildren().addAll(borderBox, cube);
        return stack;
    }

    @FXML
    void roll(ActionEvent event) {
        rollButton.setDisable(true);

        Thread thread = new Thread() {
            public void run() {
                System.out.println("Thread Running");
                try {
                    for (int i = 0; i < 15; i++) {
                        File file = new File("src/hellofx/dice/dice" + (random.nextInt(6) + 1) + ".png");
                        diceImage.setImage(new Image(file.toURI().toString()));
                        Thread.sleep(50);
                    }
                    rollButton.setDisable(false);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();
    }

    private void addCardToPlayerPile(Card card, VBox playerPile) {
    StackPane cardPane = new StackPane();
    cardPane.setStyle("-fx-border-color: black; -fx-pref-width: 100; -fx-pref-height: 150;");
    cardPane.setStyle("-fx-background-color: " + card.getColor() + ";");
    Label label = new Label(card.toString());
    cardPane.getChildren().add(label);

    // Add click event handler to the card
    cardPane.setOnMouseClicked(event -> {
        if (playerPile == player1CardPile && currentPlayerTurn == 1 && !player1CardSelected) {
            handleCardSelection(card, playerPile, cardPane);
            player1CardSelected = true;
            currentPlayerTurn = 2; // Move to the next player
        } else if (playerPile == player2CardPile && currentPlayerTurn == 2 && !player2CardSelected) {
            handleCardSelection(card, playerPile, cardPane);
            player2CardSelected = true;
            currentPlayerTurn = 3; // Move to the next player
        } else if (playerPile == player3CardPile && currentPlayerTurn == 3 && !player3CardSelected) {
            handleCardSelection(card, playerPile, cardPane);
            player3CardSelected = true;
            currentPlayerTurn = 1; // Reset to the first player
        }
    });

    playerPile.getChildren().add(cardPane);
}

    private void handleCardSelection(Card card, VBox playerPile, StackPane cardPane) {
        // Handle the card selection
        System.out.println("Selected card: " + card);
        // Perform desired action with the selected card
        playerPile.getChildren().remove(cardPane);
        moveCardToCorrectArea(card, cardPane);
    }

    private void moveCardToCorrectArea(Card card, StackPane cardPane) {
        Color cardColor = Color.valueOf(card.getColor().toUpperCase());
        StackPane targetArea = colorToAreaMap.get(cardColor);

        if (targetArea != null) {
            VBox vbox = null;

            // Check if there is already a VBox in the StackPane
            for (javafx.scene.Node node : targetArea.getChildren()) {
                if (node instanceof VBox) {
                    vbox = (VBox) node;
                    break;
                }
            }

            // If no VBox exists in this area, create one
            if (vbox == null) {
                vbox = new VBox();
                vbox.setPrefSize(150, 150); // Set fixed size for VBox to match StackPane
                vbox.setStyle("-fx-background-color: transparent;"); // Transparent background
                vbox.setAlignment(Pos.BOTTOM_CENTER); // Set alignment for the VBox to center cubes
                targetArea.getChildren().add(vbox);
            }

            // Add the card to the VBox
            vbox.getChildren().add(cardPane);
        }
    }

    private void addCardsToPlayerPile(int numberOfCards, VBox playerPile) {
        for (int i = 0; i < numberOfCards; i++) {
            if (!game.getDeck().isEmpty() && !cardPile.getChildren().isEmpty()) {
                Card card = game.getDeck().remove(0); // Remove the card from the deck
                addCardToPlayerPile(card, playerPile); // Add the card to the player pile

                // Remove the corresponding card from the card pile
                cardPile.getChildren().remove(0);
            }
        }
        updateAddCardsButtons();
    }

    // Call this method at each turn to add 5 cards to the player pile
    public void onNextTurn() {
        addCardsToPlayerPile(5, player1CardPile);
        addCardsToPlayerPile(5, player2CardPile);
        addCardsToPlayerPile(5, player3CardPile);
        player1CardSelected = false; // Reset the flag for the next turn
        player2CardSelected = false; // Reset the flag for the next turn
        player3CardSelected = false; // Reset the flag for the next turn
        currentPlayerTurn = 1; // Reset to the first player
    }

    @FXML
    private void handlePlaceCubesButtonAction() {
        placeCubesInAreas();
    }

    @FXML
    private void handleAddCardsToPlayer1Action() {
        if (player1CardPile.getChildren().size() == 4) {
            addCardsToPlayerPile(1, player1CardPile);
        }
    }

    @FXML
    private void handleAddCardsToPlayer2Action() {
        if (player2CardPile.getChildren().size() == 4) {
            addCardsToPlayerPile(1, player2CardPile);
        }
    }

    @FXML
    private void handleAddCardsToPlayer3Action() {
        if (player3CardPile.getChildren().size() == 4) {
            addCardsToPlayerPile(1, player3CardPile);
        }
    }

    private void placeCubesInAreas() {
        StackPane[] areas = {area1, area2, area3, area4, area5, area6};

        for (StackPane area : areas) {
            VBox vbox = null;

            // Check if there is already a VBox in the StackPane
            for (javafx.scene.Node node : area.getChildren()) {
                if (node instanceof VBox) {
                    vbox = (VBox) node;
                    break;
                }
            }

            // If no VBox exists in this area, create one
            if (vbox == null) {
                vbox = new VBox();
                vbox.setPrefSize(150, 150); // Set fixed size for VBox to match StackPane
                vbox.setStyle("-fx-background-color: transparent;"); // Transparent background
                vbox.setAlignment(Pos.BOTTOM_CENTER); // Set alignment for the VBox to center cubes
                area.getChildren().add(vbox);
            }

            // Add a new cube to the VBox only if it has less than 6 cubes
            if (vbox.getChildren().size() < 6 && !cubeFlowPane.getChildren().isEmpty()) {
                StackPane originalCubeStack = (StackPane) cubeFlowPane.getChildren().remove(0);

                // Set a margin for each cube for better stacking visualization
                VBox.setMargin(originalCubeStack, new Insets(5, 0, 0, 0));

                // Add the cube to the VBox
                vbox.getChildren().add(originalCubeStack);

                // Get the value of the cube based on its color
                PhongMaterial material = (PhongMaterial) ((Box) originalCubeStack.getChildren().get(1)).getMaterial();
                Color cubeColor = (Color) material.getDiffuseColor();
                Integer cubeValue = colorToValueMap.get(cubeColor);

                // Update the total value of the area
                int currentTotalValue = areaTotalValueMap.get(area);
                int newTotalValue = currentTotalValue + cubeValue;
                areaTotalValueMap.put(area, newTotalValue);

                // Print the total value of the area
                System.out.println("Added cube with value: " + cubeValue);
                System.out.println("Total value of area: " + newTotalValue);
            }
        }
    }

    private void setAreaColors() {
        area1.setStyle("-fx-background-color: yellow;");
        area2.setStyle("-fx-background-color: green;");
        area3.setStyle("-fx-background-color: red;");
        area4.setStyle("-fx-background-color: blue;");
        area5.setStyle("-fx-background-color: purple;");
        area6.setStyle("-fx-background-color: orange;");
    }

    private void updateAddCardsButtons() {
        addCardsButton1.setDisable(player1CardPile.getChildren().size() != 4);
        addCardsButton2.setDisable(player2CardPile.getChildren().size() != 4);
        addCardsButton3.setDisable(player3CardPile.getChildren().size() != 4);
    }

    private void updatePlayerScores() {
        player1ScoreLabel.setText("Player 1 Score: " + player1Score);
        player2ScoreLabel.setText("Player 2 Score: " + player2Score);
        player3ScoreLabel.setText("Player 3 Score: " + player3Score);
    }

    private void addToPlayerScore(int playerNumber, int score) {
        switch (playerNumber) {
            case 1:
                player1Score += score;
                break;
            case 2:
                player2Score += score;
                break;
            case 3:
                player3Score += score;
                break;
        }
        updatePlayerScores();
    }

    private void chooseCardsInOrder() {
        chooseCardFromPlayer(1);
        chooseCardFromPlayer(2);
        chooseCardFromPlayer(3);
    }
    
    private void chooseCardFromPlayer(int playerNumber) {
        // Logic to choose a card from the specified player's pile
        // This is a placeholder for the actual implementation
        System.out.println("Choosing card from player " + playerNumber);
    }
    
}