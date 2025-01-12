package hellofx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
// import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;

import javafx.stage.Stage;
import javafx.scene.paint.PhongMaterial;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.util.Duration;


import java.util.Iterator;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class GameController {

    @FXML
    private StackPane area1, area2, area3, area4, area5, area6;

    @FXML
    private GridPane cardGrid;

    @FXML
    private StackPane cardPile;

    @FXML
    private Button backToStartButton;

    @FXML
    private VBox cubeBox;

    @FXML
    private FlowPane cubeFlowPane;

    @FXML
    private ImageView diceImage;

    @FXML
    private Button placeCubesButton;

    private Map<StackPane, Integer> areaTotalValueMap = new HashMap<>();

    @FXML
    private Button resolveFarmButton;

    @FXML
    private Button addCardsButton;

    @FXML
    private VBox player1CardPile, player2CardPile, player3CardPile;

    @FXML
    private FlowPane player1CardFlowPane;

    @FXML
    private Button addCardsButton1, addCardsButton2, addCardsButton3;

    @FXML
    private Label player1ScoreLabel, player2ScoreLabel, player3ScoreLabel;

    private Game game;

    private Map<Color, StackPane> colorToAreaMap;
    private Map<Color, Integer> colorToValueMap;

    private boolean player1CardSelected = false;
    private boolean player2CardSelected = false;
    private boolean player3CardSelected = false;

    private Map<Integer, Card> playerCardMap = new HashMap<>();

    private int currentPlayerTurn = 1;

    private Player player1 = new Player("Player 1");
    private Player player2 = new Player("Player 2");
    private Player player3 = new Player("Player 3");

    private int scorePlayer1 = 0;
    private int scorePlayer2 = 0;
    private int scorePlayer3 = 0;

    private Main mainApp;

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }
    

    public void setGame(Game game) {
        this.game = game;
        initializeGame(); // Initialize game-related components
        addCardsToPlayerPile(5, player1CardPile, 1); // Add 5 cards to player 1's pile at the start
        addCardsToPlayerPile(5, player2CardPile, 2); // Add 5 cards to player 2's pile at the start
        addCardsToPlayerPile(5, player3CardPile, 3); // Add 5 cards to player 3's pile at the start
    }

    @FXML
    public void initialize() {
        createCubes();
        initializeColorToAreaMap(); // Initialize the color to area map first
        setAreaBackgroundImages(); // Call the method to set background images
        updateAddCardsButtons();
        initializeColorToValueMap();
        initializeAreaTotalValueMap();
        updatePlayerScores();
        chooseCardsInOrder();
        applyAnimations();

        // Hide player 2 and player 3 piles initially
        player2CardPile.setVisible(false);
        player3CardPile.setVisible(false);
        resolveFarmButton.setOnAction(_ -> {
            handleResolveFarmAction();
            if (cubeFlowPane.getChildren().isEmpty()) {
                endGame();
            }
        });
    }

    private void initializeGame() {
        createAndShuffleCards();
    }

    private void applyAnimations() {
        // Bounce animation for buttons
        Timeline bounceTimeline = new Timeline(
            new KeyFrame(Duration.ZERO, 
                new KeyValue(addCardsButton1.translateYProperty(), 0),
                new KeyValue(addCardsButton2.translateYProperty(), 0),
                new KeyValue(addCardsButton3.translateYProperty(), 0),
                new KeyValue(resolveFarmButton.translateYProperty(), 0),
                new KeyValue(reshuffleButton.translateYProperty(), 0),
                new KeyValue(placeCubesButton.translateYProperty(), 0)
            ),
            new KeyFrame(Duration.seconds(0.4), 
                new KeyValue(addCardsButton1.translateYProperty(), -15),
                new KeyValue(addCardsButton2.translateYProperty(), -15),
                new KeyValue(addCardsButton3.translateYProperty(), -15),
                new KeyValue(resolveFarmButton.translateYProperty(), -15),
                new KeyValue(reshuffleButton.translateYProperty(), -15),
                new KeyValue(placeCubesButton.translateYProperty(), -15)
            ),
            new KeyFrame(Duration.seconds(0.6), 
                new KeyValue(addCardsButton1.translateYProperty(), -7),
                new KeyValue(addCardsButton2.translateYProperty(), -7),
                new KeyValue(addCardsButton3.translateYProperty(), -7),
                new KeyValue(resolveFarmButton.translateYProperty(), -7),
                new KeyValue(reshuffleButton.translateYProperty(), -7),
                new KeyValue(placeCubesButton.translateYProperty(), -7)
            ),
            new KeyFrame(Duration.seconds(1.0), 
                new KeyValue(addCardsButton1.translateYProperty(), 0),
                new KeyValue(addCardsButton2.translateYProperty(), 0),
                new KeyValue(addCardsButton3.translateYProperty(), 0),
                new KeyValue(resolveFarmButton.translateYProperty(), 0),
                new KeyValue(reshuffleButton.translateYProperty(), 0),
                new KeyValue(placeCubesButton.translateYProperty(), 0)
            )
        );
        bounceTimeline.setCycleCount(Timeline.INDEFINITE);
        bounceTimeline.play();
    }

    private void setAreaBackgroundImages() {
        // Define the path to the folder containing the images
        String imageFolderPath = "src/hellofx/resources/areas/";
    
        // Map each color to its corresponding image file name
        Map<Color, String> colorToImageFileMap = new HashMap<>();
        colorToImageFileMap.put(Color.YELLOW, "yellow.png");
        colorToImageFileMap.put(Color.GREEN, "green.png");
        colorToImageFileMap.put(Color.RED, "red.png");
        colorToImageFileMap.put(Color.BLUE, "blue.png");
        colorToImageFileMap.put(Color.PURPLE, "purple.png");
        colorToImageFileMap.put(Color.BLACK, "black.png");
    
        // Iterate over the colorToAreaMap to set the background images
        for (Map.Entry<Color, StackPane> entry : colorToAreaMap.entrySet()) {
            Color color = entry.getKey();
            StackPane area = entry.getValue();
            String imageFileName = colorToImageFileMap.get(color);
    
            if (imageFileName != null) {
                String imagePath = imageFolderPath + imageFileName;
                try {
                    Image image = new Image(new FileInputStream(imagePath));
                    BackgroundImage backgroundImage = new BackgroundImage(
                        image,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundPosition.CENTER,
                        new BackgroundSize(BackgroundSize.DEFAULT.getWidth(), BackgroundSize.DEFAULT.getHeight(), true, true, true, false)
                    );
                    area.setBackground(new Background(backgroundImage));
                    System.out.println("Set background image for area: " + color);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    System.out.println("Image file not found: " + imagePath);
                }
            }
        }
    }

    private void initializeColorToAreaMap() {
        colorToAreaMap = new HashMap<>();
        colorToAreaMap.put(Color.YELLOW, area1);
        colorToAreaMap.put(Color.GREEN, area2);
        colorToAreaMap.put(Color.RED, area3);
        colorToAreaMap.put(Color.BLUE, area4);
        colorToAreaMap.put(Color.PURPLE, area5);
        colorToAreaMap.put(Color.BLACK, area6); // Assuming area6 is for orange cards
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

    private ImageView createCardImageView(Card card) {
        // Change card color from orange to black if necessary
        String cardColor = card.getColor().equalsIgnoreCase("orange") ? "black" : card.getColor();
        
        // Load the image for the card
        String imagePath = "src/hellofx/resources/cards/" + card.getType() + "_" + card.getValue() + "_" + cardColor + ".png";
        try {
            Image image = new Image(new FileInputStream(imagePath));
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(100);
            imageView.setFitHeight(150);
            imageView.setPreserveRatio(false); // Do not preserve the aspect ratio of the image
            imageView.setSmooth(true); // Enable smooth scaling
            imageView.setUserData(card); // Store the card as user data in the imageView
            return imageView;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            // Fallback to text if image is not found
            Label label = new Label(card.toString());
            label.setStyle("-fx-text-fill: white;"); // Set text color to white
            StackPane fallbackPane = new StackPane(label);
            fallbackPane.setStyle("-fx-border-color: black; -fx-pref-width: 100; -fx-pref-height: 150;");
            fallbackPane.setUserData(card); // Store the card as user data in the fallbackPane
            return new ImageView(); // Return an empty ImageView as a fallback
        }
    }

    private void createAndShuffleCards() {
        List<ImageView> cards = new ArrayList<>();
        for (Card card : game.getDeck()) {
            ImageView cardImageView = createCardImageView(card);
            cardImageView.setFitWidth(100); // Set the desired width
            cardImageView.setFitHeight(150); // Set the desired height
            cards.add(cardImageView);
        }
    
        // Shuffle the cards
        Collections.shuffle(cards);
    
        // Clear the existing cards in the pile and add the shuffled cards
        cardPile.getChildren().clear();
        for (ImageView cardImageView : cards) {
            cardPile.getChildren().add(cardImageView);
        }
    
        // Debug: Print the number of cards added
        System.out.println("Number of cards added to cardPile: " + cards.size());
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
        borderBox.setMaterial(new PhongMaterial(Color.WHITE));

        // Create the inner cube
        Box cube = new Box(20, 20, 20); // Smaller cube
        cube.setMaterial(new PhongMaterial(color));

        stack.getChildren().addAll(borderBox, cube);
        return stack;
    }

    private void addCardToPlayerPile(Card card, VBox playerPile, int playerNumber) {
        ImageView cardImageView = createCardImageView(card);
        cardImageView.setFitWidth(50);
        cardImageView.setFitHeight(75);
        cardImageView.setPreserveRatio(false); // Do not preserve the aspect ratio of the image
        cardImageView.setSmooth(true); // Enable smooth scaling
        cardImageView.setUserData(card); // Store the card as user data in the imageView
    
        // Add click event handler to the card
        cardImageView.setOnMouseClicked(_ -> {
            if (playerPile == player1CardPile && currentPlayerTurn == 1 && !player1CardSelected) {
                handleCardSelection(card, player1CardFlowPane, cardImageView, playerNumber);
                player1CardSelected = true;
                currentPlayerTurn = 2; // Move to the next player
                transitionToNextPlayer(player2CardPile, player1CardPile, player3CardPile);
            } else if (playerPile == player2CardPile && currentPlayerTurn == 2 && !player2CardSelected) {
                handleCardSelection(card, playerPile, cardImageView, playerNumber);
                player2CardSelected = true;
                currentPlayerTurn = 3; // Move to the next player
                transitionToNextPlayer(player3CardPile, player1CardPile, player2CardPile);
            } else if (playerPile == player3CardPile && currentPlayerTurn == 3 && !player3CardSelected) {
                handleCardSelection(card, playerPile, cardImageView, playerNumber);
                player3CardSelected = true;
                currentPlayerTurn = 1; // Reset to the first player
                transitionToNextPlayer(player1CardPile, player2CardPile, player3CardPile);
            }
    
            // Check if all players have selected their cards
            if (player1CardSelected && player2CardSelected && player3CardSelected) {
                // Enable the resolve farm button
                resolveFarmButton.setDisable(false);
            }
        });
    
        if (playerPile == player1CardPile) {
            player1CardFlowPane.getChildren().add(cardImageView);
        } else {
            playerPile.getChildren().add(cardImageView);
        }
    }

    private void transitionToNextPlayer(VBox nextPlayerPile, VBox otherPlayerPile1, VBox otherPlayerPile2) {
        nextPlayerPile.setVisible(true);
        otherPlayerPile1.setVisible(false);
        otherPlayerPile2.setVisible(false);
    }

    private void handleCardSelection(Card card, Pane playerPile, ImageView cardImageView, int playerNumber) {
        // Debug: Print the player number and the card they chose
        System.out.println("Player " + playerNumber + " selected card: " + card);
        // Perform desired action with the selected card
        playerPile.getChildren().remove(cardImageView);
        playerCardMap.put(playerNumber, card);
    
        // Determine the target area based on the card's color
        String cardColorStr = card.getColor().equalsIgnoreCase("orange") ? "black" : card.getColor();
        StackPane targetArea = getTargetAreaByColor((GridPane) playerPile.getScene().lookup("#mainGrid"), cardColorStr);
    
        // Transition the card to the correct area
        if (targetArea != null) {
            transitionCard(cardImageView, playerPile, cardColorStr, playerNumber);
        } else {
            System.out.println("Target area not found for color: " + cardColorStr);
        }
    
        // Enable the add cards buttons
        addCardsButton1.setDisable(false);
        addCardsButton2.setDisable(false);
        addCardsButton3.setDisable(false);
    
        // Immediately switch to the next player
        if (playerNumber == 1) {
            currentPlayerTurn = 2;
            transitionToNextPlayer(player2CardPile, player1CardPile, player3CardPile);
        } else if (playerNumber == 2) {
            currentPlayerTurn = 3;
            transitionToNextPlayer(player3CardPile, player1CardPile, player2CardPile);
        } else if (playerNumber == 3) {
            currentPlayerTurn = 1;
            transitionToNextPlayer(player1CardPile, player2CardPile, player3CardPile);
        }
    
        // Check if all players have selected their cards
        if (player1CardSelected && player2CardSelected && player3CardSelected) {
            // Enable the resolve farm button
            resolveFarmButton.setDisable(false);
        }
    }
    
    private void transitionCard(ImageView cardImageView, Pane startPane, String cardColor, int playerNumber) {
        // Create a clone of the cardImageView for the transition
        ImageView cardClone = new ImageView(cardImageView.getImage());
        cardClone.setFitWidth(50); // Set the width to 50
        cardClone.setFitHeight(75); // Set the height to 75
    
        // Set up transition
        GridPane mainGrid = (GridPane) startPane.getScene().lookup("#mainGrid");
    
        // Determine the target area based on the card color
        StackPane targetArea = getTargetAreaByColor(mainGrid, cardColor);
        Bounds targetBounds = targetArea.localToScene(targetArea.getBoundsInLocal());
    
        // Calculate the center positions for the transition
        double centerX = targetBounds.getMinX() + targetBounds.getWidth() / 2 - cardClone.getFitWidth() / 2;
        double centerY = targetBounds.getMinY() + targetBounds.getHeight() / 2 - cardClone.getFitHeight() / 2;
    
        // Get the bounds of the player pile
        Bounds playerPileBounds;
        if (playerNumber == 1) {
            playerPileBounds = startPane.getScene().lookup("#player1CardPile").localToScene(startPane.getBoundsInLocal());
        } else if (playerNumber == 2) {
            playerPileBounds = startPane.getScene().lookup("#player2CardPile").localToScene(startPane.getBoundsInLocal());
        } else {
            playerPileBounds = startPane.getScene().lookup("#player3CardPile").localToScene(startPane.getBoundsInLocal());
        }
    
        // Debug: Print coordinates of player piles
        System.out.println("Player 1 pile position: " + startPane.getScene().lookup("#player1CardPile").localToScene(startPane.getBoundsInLocal()).getMinX() + ", " + startPane.getScene().lookup("#player1CardPile").localToScene(startPane.getBoundsInLocal()).getMinY());
        System.out.println("Player 2 pile position: " + startPane.getScene().lookup("#player2CardPile").localToScene(startPane.getBoundsInLocal()).getMinX() + ", " + startPane.getScene().lookup("#player2CardPile").localToScene(startPane.getBoundsInLocal()).getMinY());
        System.out.println("Player 3 pile position: " + startPane.getScene().lookup("#player3CardPile").localToScene(startPane.getBoundsInLocal()).getMinX() + ", " + startPane.getScene().lookup("#player3CardPile").localToScene(startPane.getBoundsInLocal()).getMinY());
    
        // Debug: Print initial position of the clone before setting it
        System.out.println("Initial card clone position before setting: " + cardClone.getLayoutX() + ", " + cardClone.getLayoutY());
    
        // Set initial position of the clone based on player pile position
        cardClone.setLayoutX(playerPileBounds.getMinX());
        cardClone.setLayoutY(playerPileBounds.getMinY());
    
        // Debug: Print initial position of the clone after setting it
        System.out.println("Initial card clone position after setting: " + cardClone.getLayoutX() + ", " + cardClone.getLayoutY());
    
        // Debug: Print initial and target positions
        System.out.println("Starting card position: " + cardClone.getLayoutX() + ", " + cardClone.getLayoutY());
        System.out.println("Target area position: " + centerX + ", " + centerY);
    
        // Debug: Print main grid and target area dimensions
        System.out.println("Main grid dimensions: " + mainGrid.getWidth() + "x" + mainGrid.getHeight());
        System.out.println("Target area dimensions: " + targetBounds.getWidth() + "x" + targetBounds.getHeight());
    
        // Temporarily add the clone to the main grid for the transition
        mainGrid.getChildren().add(cardClone);
    
        // Debug: Print position of the clone after adding to the main grid
        System.out.println("Card clone position after adding to main grid: " + cardClone.getLayoutX() + ", " + cardClone.getLayoutY());
    
        // Create a TranslateTransition for the card movement
        TranslateTransition transition = new TranslateTransition(Duration.seconds(1), cardClone);
        transition.setToX(centerX - cardClone.getLayoutX());
        transition.setToY(centerY - cardClone.getLayoutY());
        transition.setInterpolator(javafx.animation.Interpolator.EASE_BOTH);
    
        // When the animation finishes, move the original card to the correct VBox
        transition.setOnFinished(_ -> {
            // Remove clone from main grid
            mainGrid.getChildren().remove(cardClone);
            // Add original card to the target area
            VBox cardVBox = null;
            for (javafx.scene.Node node : targetArea.getChildren()) {
                if (node instanceof VBox && "cards".equals(node.getId())) {
                    cardVBox = (VBox) node;
                    break;
                }
            }
            if (cardVBox == null) {
                cardVBox = new VBox();
                cardVBox.setId("cards");
                cardVBox.setPrefSize(150, 150); // Set fixed size for VBox to match StackPane
                cardVBox.setStyle("-fx-background-color: transparent;"); // Transparent background
                cardVBox.setAlignment(Pos.TOP_CENTER); // Set alignment for the VBox to center cubes
                targetArea.getChildren().add(cardVBox);
            }
            cardVBox.getChildren().add(cardImageView);
            // Debug: Print the number of cards in the VBox
            System.out.println("Number of cards in VBox for area " + targetArea.getId() + ": " + cardVBox.getChildren().size());
        });
    
        // Play the transition
        transition.play();
    }

    private StackPane getTargetAreaByColor(GridPane mainGrid, String cardColor) {
        switch (cardColor.toLowerCase()) {
            case "yellow":
                return (StackPane) mainGrid.lookup("#area1");
            case "green":
                return (StackPane) mainGrid.lookup("#area2");
            case "red":
                return (StackPane) mainGrid.lookup("#area3");
            case "blue":
                return (StackPane) mainGrid.lookup("#area4");
            case "purple":
                return (StackPane) mainGrid.lookup("#area5");
            case "orange":
                return (StackPane) mainGrid.lookup("#area6");
            case "black":
                // Assuming black maps to a specific area, update accordingly
                return (StackPane) mainGrid.lookup("#area6"); // Example: mapping black to area6
            default:
                throw new IllegalArgumentException("Unknown card color: " + cardColor);
        }
    }
    

    private void addCardsToPlayerPile(int numberOfCards, VBox playerPile, int playerNumber) {
        for (int i = 0; i < numberOfCards; i++) {
            if (!cardPile.getChildren().isEmpty()) {
                // Remove the top card from the card pile
                ImageView topCardImageView = (ImageView) cardPile.getChildren().remove(cardPile.getChildren().size() - 1);
                Card topCard = (Card) topCardImageView.getUserData();
    
                // Add the top card to the player pile
                addCardToPlayerPile(topCard, playerPile, playerNumber);
            }
        }
        updateAddCardsButtons();
    
        // Disable the add cards buttons after adding new cards
        addCardsButton1.setDisable(true);
        addCardsButton2.setDisable(true);
        addCardsButton3.setDisable(true);
    }

    // Call this method at each turn to add 5 cards to the player pile
    public void onNextTurn() {
        addCardsToPlayerPile(5, player1CardPile, 1);
        addCardsToPlayerPile(5, player2CardPile, 2);
        addCardsToPlayerPile(5, player3CardPile, 3);
        player1CardSelected = false; // Reset the flag for the next turn
        player2CardSelected = false; // Reset the flag for the next turn
        player3CardSelected = false; // Reset the flag for the next turn
        currentPlayerTurn = 1; // Reset to the first player
        
    }

    @FXML
    private void handlePlaceCubesButtonAction(ActionEvent event) {
        placeCubesInAreas();
        // placeCubesButton.setDisable(true); // Disable the button after it is pressed
    }

    @FXML
    private void handleAddCardsToPlayer1Action() {
        addCardsToPlayerPile(1, player1CardPile, 1);
        player1CardSelected = false; // Reset the flag after adding a card
        updateAddCardsButtons(); // Update the buttons after adding a card
        placeCubesButton.setDisable(false); // Re-enable the button at the start of the next turn
        // addCardsButton2.setDisable(true); // Enable Player 2's button
        // addCardsButton3.setDisable(false); // Disable Player 3's button
    }


    @FXML
    private void handleAddCardsToPlayer2Action() {
        addCardsToPlayerPile(1, player2CardPile, 2);
        player2CardSelected = false; // Reset the flag after adding a card
        updateAddCardsButtons(); // Update the buttons after adding a card
        // addCardsButton3.setDisable(true); // Enable Player 3's button
        // addCardsButton1.setDisable(false); // Disable Player 1's button
    }

    @FXML
    private void handleAddCardsToPlayer3Action() {
        addCardsToPlayerPile(1, player3CardPile, 3);
        player3CardSelected = false; // Reset the flag after adding a card
        updateAddCardsButtons(); // Update the buttons after adding a card
        // addCardsButton1.setDisable(true); // Enable Player1's button
        // addCardsButton2.setDisable(false); // Disable Player 2's button
    }

    @FXML
    private void handleBackToStartAction(ActionEvent event) {
        try {
            // Load the begin.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hellofx/resources/begin.fxml"));
            Parent root = loader.load();

            // Get the controller and set the mainApp reference
            BeginController beginController = loader.getController();
            beginController.setMainApp(mainApp);

            // Get the current stage
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();

            // Set the new scene
            stage.setScene(new Scene(root));
            stage.setFullScreen(true); // Maximize the stage instead of setting it to full screen
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void placeCubesInAreas() {
        StackPane[] areas = {area1, area2, area3, area4, area5, area6};
    
        for (StackPane area : areas) {
            VBox cubeVBox = null;
    
            // Check if there is already a VBox for cubes in the StackPane
            for (javafx.scene.Node node : area.getChildren()) {
                if (node instanceof VBox && "cubes".equals(node.getId())) {
                    cubeVBox = (VBox) node;
                    break;
                }
            }
    
            // If no VBox for cubes exists in this area, create one
            if (cubeVBox == null) {
                cubeVBox = new VBox();
                cubeVBox.setId("cubes");
                cubeVBox.setPrefSize(150, 150); // Set fixed size for VBox to match StackPane
                cubeVBox.setStyle("-fx-background-color: transparent;"); // Transparent background
                cubeVBox.setAlignment(Pos.BOTTOM_CENTER); // Set alignment for the VBox to center cubes
                area.getChildren().add(cubeVBox);
            }
    
            // Calculate the total value of the area
            int totalValue = 0;
            for (javafx.scene.Node node : cubeVBox.getChildren()) {
                if (node instanceof StackPane) {
                    StackPane cubeStack = (StackPane) node;
                    if (cubeStack.getChildren().size() > 1) {
                        PhongMaterial material = (PhongMaterial) ((Box) cubeStack.getChildren().get(1)).getMaterial();
                        Color cubeColor = (Color) material.getDiffuseColor();
                        Integer cubeValue = colorToValueMap.get(cubeColor);
                        if (cubeValue != null) {
                            totalValue += cubeValue;
                        }
                    }
                }
            }
    
            // Store the total value in the map
            areaTotalValueMap.put(area, totalValue);
    
            // Add a new cube to the VBox only if it has less than 6 cubes
            if (cubeVBox.getChildren().size() < 10 && !cubeFlowPane.getChildren().isEmpty()) {
                StackPane originalCubeStack = (StackPane) cubeFlowPane.getChildren().remove(0);
    
                // Set a margin for each cube for better stacking visualization
                VBox.setMargin(originalCubeStack, new Insets(5, 0, 0, 0));
    
                // Add the cube to the VBox
                cubeVBox.getChildren().add(originalCubeStack);
    
                // Get the value of the cube based on its color
                if (originalCubeStack.getChildren().size() > 1) {
                    PhongMaterial material = (PhongMaterial) ((Box) originalCubeStack.getChildren().get(1)).getMaterial();
                    Color cubeColor = (Color) material.getDiffuseColor();
                    Integer cubeValue = colorToValueMap.get(cubeColor);
    
                    // Update the total value of the area
                    totalValue += cubeValue;
    
                    // Update the map with the new total value
                    areaTotalValueMap.put(area, totalValue);
                }
            }
    
            // Debug: Print the total value of the area
            System.out.println("Total value of area " + area.getId() + ": " + totalValue);
    
            // Print the cubes in the area
            printCubesInArea(area);
        }

    }

    // private void setAreaColors() {
    //     area1.setStyle("-fx-background-color: yellow;");
    //     area2.setStyle("-fx-background-color: green;");
    //     area3.setStyle("-fx-background-color: red;");
    //     area4.setStyle("-fx-background-color: blue;");
    //     area5.setStyle("-fx-background-color: purple;");
    //     area6.setStyle("-fx-background-color: black;");
    // }

    private void updateAddCardsButtons() {
        addCardsButton1.setDisable(player1CardPile.getChildren().size() != 4);
        addCardsButton2.setDisable(player2CardPile.getChildren().size() != 4);
        addCardsButton3.setDisable(player3CardPile.getChildren().size() != 4);
    }

    private void updatePlayerScores() {
        Map<Integer, Integer> playerScores = calculatePlayerScores();
    
        int player1Score = playerScores.getOrDefault(1, 0);
        int player2Score = playerScores.getOrDefault(2, 0);
        int player3Score = playerScores.getOrDefault(3, 0);
    
        scorePlayer1 += player1Score;
        scorePlayer2 += player2Score;
        scorePlayer3 += player3Score;

        // Update the Player objects with the new scores
        player1.setPoints(scorePlayer1);
        player2.setPoints(scorePlayer2);
        player3.setPoints(scorePlayer3);

        // Debug: Print the calculated scores
        System.out.println("Player 1 Score: " + scorePlayer1);
        System.out.println("Player 2 Score: " + scorePlayer2);
        System.out.println("Player 3 Score: " + scorePlayer3);
    
        // Update the UI or internal state with the new scores
        player1ScoreLabel.setText("Player 1 Score: " + scorePlayer1);
        player2ScoreLabel.setText("Player 2 Score: " + scorePlayer2);
        player3ScoreLabel.setText("Player 3 Score: " + scorePlayer3);
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

    @FXML
    private StackPane discardPile;


    private Map<Integer, Integer> calculatePlayerScores() {
        Map<Integer, Integer> playerScores = new HashMap<>();
        Random random = new Random();
    
        // Iterate through all areas
        StackPane[] areas = {area1, area2, area3, area4, area5, area6};
        for (StackPane area : areas) {
            VBox cardVBox = null;
    
            // Check if there is already a VBox for cards in the StackPane
            for (javafx.scene.Node node : area.getChildren()) {
                if (node instanceof VBox && "cards".equals(node.getId())) {
                    cardVBox = (VBox) node;
                    break;
                }
            }
    
            if (cardVBox != null) {
                // Debug: Print the number of cards in the VBox
                System.out.println("Number of cards in VBox for area " + area.getId() + ": " + cardVBox.getChildren().size());
                // Add bird card to the discard pile
                
                
                // If there is only one card in the VBox
                if (cardVBox.getChildren().size() == 1) {
                    ImageView cardImageView = (ImageView) cardVBox.getChildren().get(0);
                    Card card = (Card) cardImageView.getUserData();
                    int playerNumber = getPlayerNumberByCard(card);
                
                    // Check if the card belongs to the player and is a bird, fox, or fleeing bird
                    if (card != null) {
                        if (card.getType() == Card.Type.BIRD) {
                            int totalScore = playerScores.getOrDefault(playerNumber, 0);
                            totalScore += areaTotalValueMap.getOrDefault(area, 0);
                            playerScores.put(playerNumber, totalScore);
                            System.out.println("Single bird card in VBox for player " + playerNumber + ", total score: " + totalScore);
                            discardPile.getChildren().add(cardImageView);
                            // Clear the cubes in the VBox
                            clearCubesInVBox(area);
                        } else if (card.getType() == Card.Type.FOX) {
                            System.out.println("Single fox card in VBox for player " + playerNumber + ", no score added.");
                            // Add fox card to the discard pile
                            discardPile.getChildren().add(cardImageView);
                        } else if (card.getType() == Card.Type.FLEEING_BIRD) {
                            int totalScore = playerScores.getOrDefault(playerNumber, 0);
                            totalScore += areaTotalValueMap.getOrDefault(area, 0);
                            playerScores.put(playerNumber, totalScore);
                            System.out.println("Single fleeing bird card in VBox for player " + playerNumber + ", total score: " + totalScore);
                            // Add fleeing bird card to the discard pile
                            discardPile.getChildren().add(cardImageView);
                            // Clear the cubes in the VBox
                            clearCubesInVBox(area);
                        }
                
                        // Remove the VBox of cards
                        area.getChildren().remove(cardVBox);
                    }
                }
    
                // If there are two cards in the VBox
                
                if (cardVBox.getChildren().size() == 2) {
                    Card fleeingBirdCard = null;
                    Card foxCard = null;
                    int foxCount = 0;
                    List<Card> birdCards = new ArrayList<>();
                    for (javafx.scene.Node node : cardVBox.getChildren()) {
                        ImageView cardImageView = (ImageView) node;
                        Card card = (Card) cardImageView.getUserData();
                        if (card != null) {
                            if (card.getType() == Card.Type.BIRD) {
                                birdCards.add(card);
                            } else if (card.getType() == Card.Type.FLEEING_BIRD) {
                                fleeingBirdCard = card;
                            } else if (card.getType() == Card.Type.FOX) {
                                foxCard = card;
                                foxCount++;
                            }
                        }
                    }

                    // If there are two bird cards, roll dice to determine the winner
                    if (birdCards.size() == 2) {
                        Map<Card, Integer> cardTotalValues = new HashMap<>();
                        boolean tie;

                        do {
                            tie = false;
                            cardTotalValues.clear();

                            for (Card card : birdCards) {
                                int diceRoll = random.nextInt(6) + 1; // Roll a dice for the card
                                int totalValue = card.getValue() + diceRoll;
                                cardTotalValues.put(card, totalValue);
                                int cardPlayerNumber = getPlayerNumberByCard(card);
                                System.out.println("Player " + cardPlayerNumber + " rolled " + diceRoll + " for card with value " + card.getValue() + ", total: " + totalValue);
                            }

                            // Determine the card with the highest total value
                            Card winningCard = null;
                            int maxTotalValue = 0;
                            int maxCount = 0;
                            for (Map.Entry<Card, Integer> entry : cardTotalValues.entrySet()) {
                                if (entry.getValue() > maxTotalValue) {
                                    maxTotalValue = entry.getValue();
                                    winningCard = entry.getKey();
                                    maxCount = 1;
                                } else if (entry.getValue() == maxTotalValue) {
                                    maxCount++;
                                }
                            }

                            // Check for a tie
                            if (maxCount > 1) {
                                tie = true;
                                System.out.println("Tie detected, rolling dice again...");
                            } else {
                                // The card with the highest total value gets the total value of the area
                                if (winningCard != null) {
                                    int winningPlayerNumber = getPlayerNumberByCard(winningCard);
                                    int totalScore = playerScores.getOrDefault(winningPlayerNumber, 0);
                                    totalScore += areaTotalValueMap.getOrDefault(area, 0);
                                    playerScores.put(winningPlayerNumber, totalScore);
                                    System.out.println("Winning card for player " + winningPlayerNumber + ", total score: " + totalScore);

                                    // Clear the cubes in the VBox
                                    clearCubesInVBox(area);

                                    // Add bird cards to the discard pile
                                    for (Card birdCard : birdCards) {
                                        ImageView birdCardImageView = findCardImageView(birdCard);
                                        if (birdCardImageView != null) {
                                            discardPile.getChildren().add(birdCardImageView);
                                        }
                                    }

                                    // Remove the VBox of cards
                                    area.getChildren().remove(cardVBox);

                                    // Clear the cubes in the VBox
                                    clearCubesInVBox(area);
                                }
                            }
                        } while (tie);
                    }
                    // If there are two fox cards, do nothing
                    else if (foxCount == 2) {
                        System.out.println("Two fox cards in VBox, no score added.");
                        for (javafx.scene.Node node : cardVBox.getChildren()) {
                            ImageView cardImageView = (ImageView) node;
                            Card card = (Card) cardImageView.getUserData();
                            if (card != null && card.getType() == Card.Type.FOX) {
                                discardPile.getChildren().add(cardImageView);
                            }
                        }
                        area.getChildren().remove(cardVBox);
                    }
                    // If there is one bird card and one fleeing card, the bird card eats the fleeing card
                    else if (birdCards.size() == 1 && fleeingBirdCard != null) {
                        int fleeingBirdPlayerNumber = getPlayerNumberByCard(fleeingBirdCard);
                        int birdPlayerNumber = getPlayerNumberByCard(birdCards.get(0));
                        int totalValue = areaTotalValueMap.getOrDefault(area, 0);
                        boolean hasGreenCube = false;
                    
                        // Check for the presence of a green cube
                        for (javafx.scene.Node node : area.getChildren()) {
                            if (node instanceof VBox && "cubes".equals(node.getId())) {
                                VBox cubeVBox = (VBox) node;
                                for (javafx.scene.Node cubeNode : cubeVBox.getChildren()) {
                                    if (cubeNode instanceof StackPane) {
                                        StackPane cubeStack = (StackPane) cubeNode;
                                        if (cubeStack.getChildren().size() > 1) {
                                            PhongMaterial material = (PhongMaterial) ((Box) cubeStack.getChildren().get(1)).getMaterial();
                                            Color cubeColor = (Color) material.getDiffuseColor();
                                            if (Color.GREEN.equals(cubeColor)) {
                                                hasGreenCube = true;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                            if (hasGreenCube) {
                                break;
                            }
                        }
                    
                        // Fleeing bird gets 1 point only if there is a green cube
                        if (hasGreenCube) {
                            int fleeingBirdScore = playerScores.getOrDefault(fleeingBirdPlayerNumber, 0);
                            fleeingBirdScore += 1;
                            playerScores.put(fleeingBirdPlayerNumber, fleeingBirdScore);
                            System.out.println("Fleeing bird card in VBox for player " + fleeingBirdPlayerNumber + ", score: 1, total score: " + fleeingBirdScore);
                            // Bird gets the remaining total value
                        
                            int birdScore = playerScores.getOrDefault(birdPlayerNumber, 0);
                            birdScore += (totalValue-1);
                            playerScores.put(birdPlayerNumber, birdScore);
                            System.out.println("Bird card in VBox for player " + birdPlayerNumber + ", score: " + (totalValue - 1) + ", total score: " + birdScore);
                            // Add bird card to the discard pile
                            ImageView birdCardImageView = findCardImageView(birdCards.get(0));
                            if (birdCardImageView != null) {
                                discardPile.getChildren().add(birdCardImageView);
                            }
                            ImageView fleeingCardImageView = findCardImageView(fleeingBirdCard);
                            if (fleeingCardImageView != null) {
                                discardPile.getChildren().add(fleeingCardImageView);
                            }
                            // Remove the VBox of cards
                            area.getChildren().remove(cardVBox);
                        
                            // Clear the cubes in the VBox
                            clearCubesInVBox(area);
                        }
                        else 
                        {
                            // Bird gets the remaining total value
                            int birdScore = playerScores.getOrDefault(birdPlayerNumber, 0);
                            birdScore += totalValue;
                            playerScores.put(birdPlayerNumber, birdScore);
                            System.out.println("Bird card in VBox for player " + birdPlayerNumber + ", score: " + (totalValue) + ", total score: " + birdScore);
                        
                            // Add bird card to the discard pile
                            ImageView birdCardImageView = findCardImageView(birdCards.get(0));
                            if (birdCardImageView != null) {
                                discardPile.getChildren().add(birdCardImageView);
                            }
                            ImageView fleeingCardImageView = findCardImageView(fleeingBirdCard);
                            if (fleeingCardImageView != null) {
                                discardPile.getChildren().add(fleeingCardImageView);
                            }
                            // Remove the VBox of cards
                            area.getChildren().remove(cardVBox);
                        
                            // Clear the cubes in the VBox
                            clearCubesInVBox(area);
                        }
                    }
                    // If there is one bird card and one fox card, fox eats the bird
                    else if (birdCards.size() == 1 && foxCard != null) {
                        int foxPlayerNumber = getPlayerNumberByCard(foxCard);
                        int birdCardValue = birdCards.get(0).getValue();
                        int totalScore = playerScores.getOrDefault(foxPlayerNumber, 0);
                        totalScore += birdCardValue;
                        playerScores.put(foxPlayerNumber, totalScore);
                        System.out.println("Fox card in VBox for player " + foxPlayerNumber + ", score added by bird card value: " + birdCardValue + ", total score: " + totalScore);

                        // Add bird card to the discard pile
                        ImageView foxCardImageView = findCardImageView(foxCard);
                        if (foxCardImageView != null) {
                            discardPile.getChildren().add(foxCardImageView);
                        }

                        // Remove the VBox of cards
                        area.getChildren().remove(cardVBox);
                    }
                    // If there is one fleeing bird and a fox, the fox eats the fleeing bird
                    else if (fleeingBirdCard != null && foxCard != null) {
                        int foxPlayerNumber = getPlayerNumberByCard(foxCard);
                        int fleeingBirdCardValue = fleeingBirdCard.getValue();
                        int totalScore1 = playerScores.getOrDefault(foxPlayerNumber, 0);
                        totalScore1 += fleeingBirdCardValue;
                        playerScores.put(foxPlayerNumber, totalScore1);
                        System.out.println("Fox card in VBox for player " + foxPlayerNumber + ", score added by fleeing bird card value: " + fleeingBirdCardValue + ", total score: " + totalScore1);
                    
                        boolean hasGreenCube = false;
                    
                        // Check for the presence of a green cube
                        for (javafx.scene.Node node : area.getChildren()) {
                            if (node instanceof VBox && "cubes".equals(node.getId())) {
                                VBox cubeVBox = (VBox) node;
                                for (javafx.scene.Node cubeNode : cubeVBox.getChildren()) {
                                    if (cubeNode instanceof StackPane) {
                                        StackPane cubeStack = (StackPane) cubeNode;
                                        if (cubeStack.getChildren().size() > 1) {
                                            PhongMaterial material = (PhongMaterial) ((Box) cubeStack.getChildren().get(1)).getMaterial();
                                            Color cubeColor = (Color) material.getDiffuseColor();
                                            if (Color.GREEN.equals(cubeColor)) {
                                                hasGreenCube = true;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                            if (hasGreenCube) {
                                break;
                            }
                        }
                    
                        // Fleeing bird gets 1 point only if there is a green cube
                        if (hasGreenCube) {
                            int fleeingBirdNumber = getPlayerNumberByCard(fleeingBirdCard);
                            int totalScore2 = playerScores.getOrDefault(fleeingBirdNumber, 0);
                            totalScore2 += 1;
                            playerScores.put(fleeingBirdNumber, totalScore2);
                            System.out.println("Fleeing bird card in VBox for player " + fleeingBirdNumber + ", score: 1, total score: " + totalScore2);
                        }
                    
                        // Add fox card to the discard pile before removing the VBox
                        ImageView foxCardImageView = findCardImageView(foxCard);
                        if (foxCardImageView != null) {
                            discardPile.getChildren().add(foxCardImageView);
                        }
                    
                    
                        // Remove a green cube from the cube VBox
                        removeGreenCube(area);
                    
                        // Remove the VBox of cards
                        area.getChildren().remove(cardVBox);
                    }
                }
    
                // If there are three cards in the VBox
                if (cardVBox.getChildren().size() == 3) {
                    List<Card> birdCards = new ArrayList<>();
                    List<Card> foxCards = new ArrayList<>();
                    Card fleeingBirdCard = null;
                    for (javafx.scene.Node node : cardVBox.getChildren()) {
                        ImageView cardImageView = (ImageView) node;
                        Card card = (Card) cardImageView.getUserData();
                        if (card != null) {
                            if (card.getType() == Card.Type.BIRD) {
                                birdCards.add(card);
                            } else if (card.getType() == Card.Type.FOX) {
                                foxCards.add(card);
                            } else if (card.getType() == Card.Type.FLEEING_BIRD) {
                                fleeingBirdCard = card;
                            }
                        }
                    }
                    if (birdCards.size() == 3) {
                        Map<Card, Integer> cardTotalValues = new HashMap<>();
                        boolean tie;
                
                        do {
                            tie = false;
                            cardTotalValues.clear();
                
                            for (Card card : birdCards) {
                                int diceRoll = random.nextInt(6) + 1; // Roll a dice for the card
                                int totalValue = card.getValue() + diceRoll;
                                cardTotalValues.put(card, totalValue);
                                int cardPlayerNumber = getPlayerNumberByCard(card);
                                System.out.println("Player " + cardPlayerNumber + " rolled " + diceRoll + " for card with value " + card.getValue() + ", total: " + totalValue);
                            }
                
                            // Determine the card with the highest total value
                            Card winningCard = null;
                            int maxTotalValue = 0;
                            int maxCount = 0;
                            for (Map.Entry<Card, Integer> entry : cardTotalValues.entrySet()) {
                                if (entry.getValue() > maxTotalValue) {
                                    maxTotalValue = entry.getValue();
                                    winningCard = entry.getKey();
                                    maxCount = 1;
                                } else if (entry.getValue() == maxTotalValue) {
                                    maxCount++;
                                }
                            }
                
                            // Check for a tie
                            if (maxCount > 1) {
                                tie = true;
                                System.out.println("Tie detected, rolling dice again...");
                            } else {
                                // The card with the highest total value gets the total value of the area
                                if (winningCard != null) {
                                    int winningPlayerNumber = getPlayerNumberByCard(winningCard);
                                    int totalScore = playerScores.getOrDefault(winningPlayerNumber, 0);
                                    totalScore += areaTotalValueMap.getOrDefault(area, 0);
                                    playerScores.put(winningPlayerNumber, totalScore);
                                    System.out.println("Winning card for player " + winningPlayerNumber + ", total score: " + totalScore);
                
                                    // Clear the cubes in the VBox
                                    clearCubesInVBox(area);
                
                                    // Add bird cards to the discard pile
                                    for (Card birdCard : birdCards) {
                                        ImageView birdCardImageView = findCardImageView(birdCard);
                                        if (birdCardImageView != null) {
                                            discardPile.getChildren().add(birdCardImageView);
                                        }
                                    }

                                    // Remove the VBox of cards
                                    area.getChildren().remove(cardVBox);
                
                                    // Clear the cubes in the VBox
                                    clearCubesInVBox(area);
                                }
                            }
                        } while (tie);
                    }
                    // If there are three fox cards, do nothing
                    else if (foxCards.size() == 3) {
                        System.out.println("Three fox cards in VBox, no score added.");
                        
                        // Add fox cards to the discard pile
                        for (Card foxCard : foxCards) {
                            ImageView foxCardImageView = findCardImageView(foxCard);
                            if (foxCardImageView != null) {
                                discardPile.getChildren().add(foxCardImageView);
                            }
                        }
                        area.getChildren().remove(cardVBox);
                    } 
                    //If there are two fox cards and one fleeing bird card, fleeing bird has 1 point and the foxes fight to eat the fleeing bird
                    else if (foxCards.size() == 2 && fleeingBirdCard != null) {
                        int fleeingBirdPlayerNumber = getPlayerNumberByCard(fleeingBirdCard);
                        int fleeingBirdCardValue = fleeingBirdCard.getValue();
                        Map<Card, Integer> foxCardTotalValues = new HashMap<>();
                        boolean hasGreenCube = false;
                    
                        // Check for the presence of a green cube
                        for (javafx.scene.Node node : area.getChildren()) {
                            if (node instanceof VBox && "cubes".equals(node.getId())) {
                                VBox cubeVBox = (VBox) node;
                                for (javafx.scene.Node cubeNode : cubeVBox.getChildren()) {
                                    if (cubeNode instanceof StackPane) {
                                        StackPane cubeStack = (StackPane) cubeNode;
                                        if (cubeStack.getChildren().size() > 1) {
                                            PhongMaterial material = (PhongMaterial) ((Box) cubeStack.getChildren().get(1)).getMaterial();
                                            Color cubeColor = (Color) material.getDiffuseColor();
                                            if (Color.GREEN.equals(cubeColor)) {
                                                hasGreenCube = true;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                            if (hasGreenCube) {
                                break;
                            }
                        }
                    
                        // Fleeing bird gets 1 point only if there is a green cube
                        if (hasGreenCube) {
                            int fleeingBirdScore = playerScores.getOrDefault(fleeingBirdPlayerNumber, 0);
                            fleeingBirdScore += 1;
                            playerScores.put(fleeingBirdPlayerNumber, fleeingBirdScore);
                            System.out.println("Fleeing bird card in VBox for player " + fleeingBirdPlayerNumber + ", score: 1, total score: " + fleeingBirdScore);
                        }
                    
                        for (Card foxCard : foxCards) {
                            int diceRoll = random.nextInt(6) + 1; // Roll a dice for the fox card
                            int totalValue = foxCard.getValue() + diceRoll;
                            foxCardTotalValues.put(foxCard, totalValue);
                            int foxPlayerNumber = getPlayerNumberByCard(foxCard);
                            System.out.println("Player " + foxPlayerNumber + " rolled " + diceRoll + " for fox card with value " + foxCard.getValue() + ", total: " + totalValue);
                        }
                    
                        // Determine the fox card with the highest total value
                        Card winningFoxCard = null;
                        int maxTotalValue = 0;
                        for (Map.Entry<Card, Integer> entry : foxCardTotalValues.entrySet()) {
                            if (entry.getValue() > maxTotalValue) {
                                maxTotalValue = entry.getValue();
                                winningFoxCard = entry.getKey();
                            }
                        }
                    
                        if (winningFoxCard != null) {
                            int winningFoxPlayerNumber = getPlayerNumberByCard(winningFoxCard);
                            int totalScore = playerScores.getOrDefault(winningFoxPlayerNumber, 0);
                            totalScore += fleeingBirdCardValue;
                            playerScores.put(winningFoxPlayerNumber, totalScore);
                            System.out.println("Winning fox card for player " + winningFoxPlayerNumber + ", score added by fleeing bird card value: " + fleeingBirdCardValue + ", total score: " + totalScore);
                    
                            // Add fox cards to the discard pile
                            for (Card foxCard : foxCards) {
                                ImageView foxCardImageView = findCardImageView(foxCard);
                                if (foxCardImageView != null) {
                                    discardPile.getChildren().add(foxCardImageView);
                                }
                            }
                    
                            // Remove a green cube from the cube VBox
                            removeGreenCube(area);
                    
                            // Remove the VBox of cards
                            area.getChildren().remove(cardVBox);
                        }
                    }
                    // If there are two bird cards and one fox card, then the fox eat all birds
                    else if (birdCards.size() == 2 && foxCards.size() == 1) {
                        int foxPlayerNumber = getPlayerNumberByCard(foxCards.get(0));
                        int birdCardValueSum = birdCards.stream().mapToInt(Card::getValue).sum();
                        int totalScore = playerScores.getOrDefault(foxPlayerNumber, 0);
                        totalScore += birdCardValueSum;
                        playerScores.put(foxPlayerNumber, totalScore);
                        System.out.println("Fox card in VBox for player " + foxPlayerNumber + ", score added by bird card values sum: " + birdCardValueSum + ", total score: " + totalScore);
                        
                        // Add fox card to the discard pile before removing the VBox
                        ImageView foxCardImageView = findCardImageView(foxCards.get(0));
                        if (foxCardImageView != null) {
                            discardPile.getChildren().add(foxCardImageView);
                        }
                    
                    
                        // Remove the VBox of cards
                        area.getChildren().remove(cardVBox);
                    }
                    // If there is one fox card, one bird card, and one fleeing bird card, then the fleeing bird has 1 point, the fox eats all birds
                    else if (foxCards.size() == 1 && birdCards.size() == 1 && fleeingBirdCard != null) {
                        int fleeingBirdPlayerNumber = getPlayerNumberByCard(fleeingBirdCard);
                        int foxPlayerNumber = getPlayerNumberByCard(foxCards.get(0));
                        int birdCardValue = birdCards.get(0).getValue();
                        int fleeingBirdCardValue = fleeingBirdCard.getValue();
                        boolean hasGreenCube = false;

                        // Check for the presence of a green cube
                        for (javafx.scene.Node node : area.getChildren()) {
                            if (node instanceof VBox && "cubes".equals(node.getId())) {
                                VBox cubeVBox = (VBox) node;
                                for (javafx.scene.Node cubeNode : cubeVBox.getChildren()) {
                                    if (cubeNode instanceof StackPane) {
                                        StackPane cubeStack = (StackPane) cubeNode;
                                        if (cubeStack.getChildren().size() > 1) {
                                            PhongMaterial material = (PhongMaterial) ((Box) cubeStack.getChildren().get(1)).getMaterial();
                                            Color cubeColor = (Color) material.getDiffuseColor();
                                            if (Color.GREEN.equals(cubeColor)) {
                                                hasGreenCube = true;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                            if (hasGreenCube) {
                                break;
                            }
                        }
                    
                        // Fleeing bird gets 1 point only if there is a green cube
                        if (hasGreenCube) {
                            int fleeingBirdScore = playerScores.getOrDefault(fleeingBirdPlayerNumber, 0);
                            fleeingBirdScore += 1;
                            playerScores.put(fleeingBirdPlayerNumber, fleeingBirdScore);
                            System.out.println("Fleeing bird card in VBox for player " + fleeingBirdPlayerNumber + ", score: 1, total score: " + fleeingBirdScore);
                        }
                    
                        // Fox gets the value of the bird and fleeing bird
                        int totalScore = playerScores.getOrDefault(foxPlayerNumber, 0);
                        totalScore += birdCardValue + fleeingBirdCardValue;
                        playerScores.put(foxPlayerNumber, totalScore);
                        System.out.println("Fox card in VBox for player " + foxPlayerNumber + ", score added by bird and fleeing bird card values: " + (birdCardValue + fleeingBirdCardValue) + ", total score: " + totalScore);
                    
                        // Add fox card to the discard pile before removing the VBox
                        ImageView foxCardImageView = findCardImageView(foxCards.get(0));
                        if (foxCardImageView != null) {
                            discardPile.getChildren().add(foxCardImageView);
                        }
                    
                    
                        // Remove a green cube from the cube VBox
                        removeGreenCube(area);
                    
                        // Remove the VBox of cards
                        area.getChildren().remove(cardVBox);
                    }
                    //If there are two bird cards and one fleeing bird card, the fleeing bird has 1 points and the birds fight to have the other points
                    else if (birdCards.size() == 2 && fleeingBirdCard != null) {
                        int fleeingBirdPlayerNumber = getPlayerNumberByCard(fleeingBirdCard);
                        int totalValue = areaTotalValueMap.getOrDefault(area, 0);
                        Map<Card, Integer> birdCardTotalValues = new HashMap<>();
                        boolean hasGreenCube = false;
                    
                        // Check for the presence of a green cube
                        for (javafx.scene.Node node : area.getChildren()) {
                            if (node instanceof VBox && "cubes".equals(node.getId())) {
                                VBox cubeVBox = (VBox) node;
                                for (javafx.scene.Node cubeNode : cubeVBox.getChildren()) {
                                    if (cubeNode instanceof StackPane) {
                                        StackPane cubeStack = (StackPane) cubeNode;
                                        if (cubeStack.getChildren().size() > 1) {
                                            PhongMaterial material = (PhongMaterial) ((Box) cubeStack.getChildren().get(1)).getMaterial();
                                            Color cubeColor = (Color) material.getDiffuseColor();
                                            if (Color.GREEN.equals(cubeColor)) {
                                                hasGreenCube = true;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                            if (hasGreenCube) {
                                break;
                            }
                        }
                    
                        // Fleeing bird gets 1 point only if there is a green cube
                        if (hasGreenCube) {
                            int fleeingBirdScore = playerScores.getOrDefault(fleeingBirdPlayerNumber, 0);
                            fleeingBirdScore += 1;
                            playerScores.put(fleeingBirdPlayerNumber, fleeingBirdScore);
                            System.out.println("Fleeing bird card in VBox for player " + fleeingBirdPlayerNumber + ", score: 1, total score: " + fleeingBirdScore);
                            for (Card birdCard : birdCards) {
                                int diceRoll = random.nextInt(6) + 1; // Roll a dice for the bird card
                                int totalValueWithDice = birdCard.getValue() + diceRoll;
                                birdCardTotalValues.put(birdCard, totalValueWithDice);
                                int birdPlayerNumber = getPlayerNumberByCard(birdCard);
                                System.out.println("Player " + birdPlayerNumber + " rolled " + diceRoll + " for bird card with value " + birdCard.getValue() + ", total: " + totalValueWithDice);
                            }
                        
                            // Determine the bird card with the highest total value
                            Card winningBirdCard = null;
                            int maxTotalValue = 0;
                            for (Map.Entry<Card, Integer> entry : birdCardTotalValues.entrySet()) {
                                if (entry.getValue() > maxTotalValue) {
                                    maxTotalValue = entry.getValue();
                                    winningBirdCard = entry.getKey();
                                }
                            }
                        
                            if (winningBirdCard != null) {
                                int winningBirdPlayerNumber = getPlayerNumberByCard(winningBirdCard);
                                int totalScore = playerScores.getOrDefault(winningBirdPlayerNumber, 0);
                                totalScore += (totalValue - 1);
                                playerScores.put(winningBirdPlayerNumber, totalScore);
                                System.out.println("Winning bird card for player " + winningBirdPlayerNumber + ", score added by remaining total value: " + (totalValue - 1) + ", total score: " + totalScore);
                        
                                // Add bird cards to the discard pile
                                for (Card birdCard : birdCards) {
                                    ImageView birdCardImageView = findCardImageView(birdCard);
                                    if (birdCardImageView != null) {
                                        discardPile.getChildren().add(birdCardImageView);
                                    }
                                }
                        
                                // Add fleeing bird card to the discard pile
                                ImageView fleeingBirdCardImageView = findCardImageView(fleeingBirdCard);
                                if (fleeingBirdCardImageView != null) {
                                    discardPile.getChildren().add(fleeingBirdCardImageView);
                                }
                        
                                // Remove the VBox of cards
                                area.getChildren().remove(cardVBox);
                        
                                // Remove a green cube from the cube VBox
                                removeGreenCube(area);
                        
                                // Clear the cubes in the VBox
                                clearCubesInVBox(area);
                            }
                        }
                        else
                        {
                            for (Card birdCard : birdCards) {
                                int diceRoll = random.nextInt(6) + 1; // Roll a dice for the bird card
                                int totalValueWithDice = birdCard.getValue() + diceRoll;
                                birdCardTotalValues.put(birdCard, totalValueWithDice);
                                int birdPlayerNumber = getPlayerNumberByCard(birdCard);
                                System.out.println("Player " + birdPlayerNumber + " rolled " + diceRoll + " for bird card with value " + birdCard.getValue() + ", total: " + totalValueWithDice);
                            }
                        
                            // Determine the bird card with the highest total value
                            Card winningBirdCard = null;
                            int maxTotalValue = 0;
                            for (Map.Entry<Card, Integer> entry : birdCardTotalValues.entrySet()) {
                                if (entry.getValue() > maxTotalValue) {
                                    maxTotalValue = entry.getValue();
                                    winningBirdCard = entry.getKey();
                                }
                            }
                        
                            if (winningBirdCard != null) {
                                int winningBirdPlayerNumber = getPlayerNumberByCard(winningBirdCard);
                                int totalScore = playerScores.getOrDefault(winningBirdPlayerNumber, 0);
                                totalScore += totalValue;
                                playerScores.put(winningBirdPlayerNumber, totalScore);
                                System.out.println("Winning bird card for player " + winningBirdPlayerNumber + ", score added by remaining total value: " + totalValue  + ", total score: " + totalScore);
                        
                                // Add bird cards to the discard pile
                                for (Card birdCard : birdCards) {
                                    ImageView birdCardImageView = findCardImageView(birdCard);
                                    if (birdCardImageView != null) {
                                        discardPile.getChildren().add(birdCardImageView);
                                    }
                                }
                        
                                // Add fleeing bird card to the discard pile
                                ImageView fleeingBirdCardImageView = findCardImageView(fleeingBirdCard);
                                if (fleeingBirdCardImageView != null) {
                                    discardPile.getChildren().add(fleeingBirdCardImageView);
                                }
                        
                                // Remove the VBox of cards
                                area.getChildren().remove(cardVBox);
                        
                                // Remove a green cube from the cube VBox
                                removeGreenCube(area);
                        
                                // Clear the cubes in the VBox
                                clearCubesInVBox(area);
                            }
                        }
                    }
                }
            }
        }
    
        return playerScores;
    }

    // Helper method to remove a green cube from the cube VBox
    private void removeGreenCube(StackPane area) {
        VBox cubeVBox = null;
    
        // Find the VBox for cubes
        for (javafx.scene.Node node : area.getChildren()) {
            if (node instanceof VBox && "cubes".equals(node.getId())) {
                cubeVBox = (VBox) node;
                break;
            }
        }
    
        if (cubeVBox != null) {
            Iterator<javafx.scene.Node> iterator = cubeVBox.getChildren().iterator();
            while (iterator.hasNext()) {
                javafx.scene.Node node = iterator.next();
                if (node instanceof StackPane) {
                    StackPane cubeStack = (StackPane) node;
                    if (cubeStack.getChildren().size() > 1) {
                        PhongMaterial material = (PhongMaterial) ((Box) cubeStack.getChildren().get(1)).getMaterial();
                        Color cubeColor = (Color) material.getDiffuseColor();
                        if (Color.GREEN.equals(cubeColor)) {
                            iterator.remove();
                            System.out.println("Removed a green cube from the VBox");
                            break;
                        }
                    }
                }
            }
        } else {
            System.out.println("No VBox for cubes found in the area " + area.getId());
        }
    }
    
    // Helper method to get the player number by card
    private int getPlayerNumberByCard(Card card) {
        for (Map.Entry<Integer, Card> entry : playerCardMap.entrySet()) {
            if (entry.getValue().equals(card)) {
                return entry.getKey();
            }
        }
        return -1; // Return -1 if the card is not found
    }
    
    // Helper method to clear the cubes in the VBox
    private void clearCubesInVBox(StackPane area) {
        VBox cubeVBox = null;
    
        // Check if there is already a VBox for cubes in the StackPane
        for (javafx.scene.Node node : area.getChildren()) {
            if (node instanceof VBox && "cubes".equals(node.getId())) {
                cubeVBox = (VBox) node;
                break;
            }
        }
    
        // If no VBox for cubes exists in this area, create one
        if (cubeVBox == null) {
            cubeVBox = new VBox();
            cubeVBox.setId("cubes");
            cubeVBox.setPrefSize(150, 150); // Set fixed size for VBox to match StackPane
            cubeVBox.setStyle("-fx-background-color: transparent;"); // Transparent background
            cubeVBox.setAlignment(Pos.BOTTOM_CENTER); // Set alignment for the VBox to bottom center
            area.getChildren().add(cubeVBox);
        }
    
        // Clear the VBox to free up all the cubes
        cubeVBox.getChildren().clear();
        System.out.println("All cubes cleared from the VBox in area " + area.getId());
    }

    private void printCubesInArea(StackPane area) {
        VBox cubeVBox = null;
    
        // Find the VBox for cubes
        for (javafx.scene.Node node : area.getChildren()) {
            if (node instanceof VBox && "cubes".equals(node.getId())) {
                cubeVBox = (VBox) node;
                break;
            }
        }
    
        if (cubeVBox != null) {
            System.out.println("Cubes in area " + area.getId() + ":");
            for (javafx.scene.Node node : cubeVBox.getChildren()) {
                if (node instanceof StackPane) {
                    StackPane cubeStack = (StackPane) node;
                    if (cubeStack.getChildren().size() > 1) {
                        PhongMaterial material = (PhongMaterial) ((Box) cubeStack.getChildren().get(1)).getMaterial();
                        Color cubeColor = (Color) material.getDiffuseColor();
                        String colorName = getColorName(cubeColor);
                        System.out.println("Cube color: " + colorName);
                    }
                }
            }
        } else {
            System.out.println("No VBox for cubes found in the area " + area.getId());
        }
    }

    private void addCardToDiscardPile(Card card) {
        ImageView cardImageView = createCardImageView(card);
        cardImageView.setFitWidth(100); // Set the desired width
        cardImageView.setFitHeight(150); // Set the desired height
    
        // Clear the existing cards in the discard pile
        discardPile.getChildren().clear();
    
        // Add the new card to the discard pile
        discardPile.getChildren().add(cardImageView);
    }

    @FXML
    private void handleResolveFarmAction() {
        System.out.println("Resolving farm action...");

        // Update player scores
        updatePlayerScores();

        // Add chosen cards to the discard pile
        for (Map.Entry<Integer, Card> entry : playerCardMap.entrySet()) {
            Card card = entry.getValue();
            if (card != null) {
                ImageView cardImageView = findCardImageView(card);
                if (cardImageView != null) {
                    // Remove the cardImageView from its current parent
                    ((Pane) cardImageView.getParent()).getChildren().remove(cardImageView);
                    // Add the card to the discard pile
                    addCardToDiscardPile(card);
                    System.out.println("Added card to discard pile: " + card);
                }
            }
        }

        // Clear the playerCardMap after resolving the farm
        playerCardMap.clear();

        // Check if all cubes have been placed
        if (cubeFlowPane.getChildren().isEmpty()) {
            endGame();
        }
    }

    private ImageView findCardImageView(Card card) {
        // Search in player piles
        for (VBox playerPile : new VBox[]{player1CardPile, player2CardPile, player3CardPile}) {
            for (javafx.scene.Node node : playerPile.getChildren()) {
                if (node instanceof ImageView) {
                    ImageView cardImageView = (ImageView) node;
                    if (card.equals(cardImageView.getUserData())) {
                        return cardImageView;
                    }
                }
            }
        }
    
        // Search in areas
        StackPane[] areas = {area1, area2, area3, area4, area5, area6};
        for (StackPane area : areas) {
            for (javafx.scene.Node node : area.getChildren()) {
                if (node instanceof VBox && "cards".equals(node.getId())) {
                    VBox cardVBox = (VBox) node;
                    for (javafx.scene.Node cardNode : cardVBox.getChildren()) {
                        if (cardNode instanceof ImageView) {
                            ImageView cardImageView = (ImageView) cardNode;
                            if (card.equals(cardImageView.getUserData())) {
                                return cardImageView;
                            }
                        }
                    }
                }
            }
        }
    
        return null;
    }
    private String getColorName(Color color) {
        if (Color.YELLOW.equals(color)) {
            return "yellow";
        } else if (Color.GREEN.equals(color)) {
            return "green";
        } else if (Color.BLUE.equals(color)) {
            return "blue";
        } else {
            return "unknown";
        }
    }
    
    @FXML
    private Button reshuffleButton;

    @FXML
    private void handleReshuffleButtonAction() {
        if (cardPile.getChildren().isEmpty()) {
            // Shuffle the discard pile and add the cards back to the card pile
            List<ImageView> cards = new ArrayList<>();
            for (javafx.scene.Node node : discardPile.getChildren()) {
                if (node instanceof ImageView) {
                    cards.add((ImageView) node);
                }
            }
            Collections.shuffle(cards);
            cardPile.getChildren().addAll(cards);
            discardPile.getChildren().clear();
            System.out.println("Discard pile shuffled and added back to the card pile. Number of cards added: " + cards.size());
        } else {
            System.out.println("Card pile is not empty.");
        }
    }

    

    // private String getPlayerWithHighestScore() {
    //     if (scorePlayer1 > scorePlayer2 && scorePlayer1 > scorePlayer3) {
    //         return "Player 1";
    //     } else if (scorePlayer2 > scorePlayer1 && scorePlayer2 > scorePlayer3) {
    //         return "Player 2";
    //     } else if (scorePlayer3 > scorePlayer1 && scorePlayer3 > scorePlayer2) {
    //         return "Player 3";
    //     } else {
    //         return "It's a tie!";
    //     }
    // }

    private Stage winnerStage;
    private WinnerController winnerController;

    private void declareWinner() {
        List<Player> players = getPlayersSortedByScore();
        showWinnerScreen(players);
    }

    private void showWinnerScreen(List<Player> players) {
        try {
            if (winnerStage == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/hellofx/resources/winner.fxml"));
                Parent root = loader.load();
                winnerController = loader.getController();
                winnerController.setWinners(players);
    
                // Create a new stage for the winner screen
                winnerStage = new Stage();
                Scene scene = new Scene(root);
                winnerStage.setTitle("Hick Hack");
                InputStream logoStream = getClass().getResourceAsStream("/hellofx/resources/logo/logo.jpg");
                if (logoStream == null) {
                    System.out.println("Logo not found!");
                } else {
                    winnerStage.getIcons().add(new Image(logoStream));
                }
                winnerStage.setScene(scene);
                winnerStage.setFullScreen(true); // Set the stage to full screen
                winnerStage.show();
            } else {
                winnerController.setWinners(players);
                winnerStage.setFullScreen(true); // Ensure the stage is full screen
                winnerStage.show();
            }

            // Close the current game stage
            Stage currentStage = (Stage) cubeFlowPane.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Call this method at the end of the game when all cubes are added
    private void endGame() {
        updatePlayerScores();
        declareWinner();
    }

    private List<Player> getPlayersSortedByScore() {
        // Assuming you have a method to get all players
        List<Player> players = getAllPlayers();
        // Sort players by points in descending order
        return players.stream()
                .sorted((p1, p2) -> Integer.compare(p2.getPoints(), p1.getPoints()))
                .collect(Collectors.toList());
    }

    private List<Player> getAllPlayers() {
        // Return the list of all players
        return Arrays.asList(player1, player2, player3);
        // Add more players to the list as needed
    }
}