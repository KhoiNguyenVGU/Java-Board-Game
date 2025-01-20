/**
 * SingleGameController.java
 * Controller class for managing single-player game mode in Hick Hack game.
 * Handles game logic, UI interactions, and player management.
 */

package hellofx;

// JavaFX UI component and animation imports
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
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
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.util.Duration;

// Java utility imports
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

public class SingleGameController {

    @FXML private StackPane area1, area2, area3, area4, area5, area6;     // Game board areas
    @FXML private GridPane cardGrid;                                      // Card layout grid
    @FXML private StackPane cardPile;                                     // Deck of cards
    @FXML private Button backToStartButton;                               // Return to menu button
    @FXML private VBox cubeBox;                                           // Cube storage
    @FXML private FlowPane cubeFlowPane;                                  // Display area for available cubes
    @FXML private ImageView diceImage;                                    // Dice display image
    @FXML private Button placeCubesButton;                                // Trigger cube placement
    @FXML private Button resolveFarmButton;                               // Trigger farm resolving
    @FXML private Button addCardsButton;                                  // Add cards to hand
    @FXML private VBox player1CardPile, player2CardPile, player3CardPile; // Player hand
    @FXML private FlowPane player1CardFlowPane;                           // Player 1's card display area
    @FXML private Button addCardsButton1;                                 // Player 1's draw button
    @FXML private Label cardPileCountLabel;                               // Remaining cards counter
    //private Label discardPileCountLabel;
    
    // Tracks total value in each area
    private Map<StackPane, Integer> areaTotalValueMap = new HashMap<>();
    // Score display labels
    @FXML private Label player1ScoreLabel, player2ScoreLabel, player3ScoreLabel;

    private Game game;

    private Map<Color, StackPane> colorToAreaMap; // Maps colors to board areas
    private Map<Color, Integer> colorToValueMap;  // Maps colors to point values

    // Player state tracking
    private boolean player1CardSelected = false;
    private boolean player2CardSelected = false;
    private boolean player3CardSelected = false;

    // Maps players to their selected cards
    private Map<Integer, Card> playerCardMap = new HashMap<>();

    // Player management
    private int currentPlayerTurn = 1;               // Current player's turn
    private Player player1 = new Player("Player 1"); // Player instances
    private Player player2 = new Player("Player 2");
    private Player player3 = new Player("Player 3");

    // Score tracking
    private int scorePlayer1 = 0;
    private int scorePlayer2 = 0;
    private int scorePlayer3 = 0;

    /**
     * Sets the main application reference
     * @param mainApp The main application instance
     */
    private Main mainApp;
    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * Initializes the game
     * Sets up initial card distribution
     * @param game The game instance to initialize
     */
    public void setGame(Game game) {
        this.game = game;
        initializeGame();
        addCardsToPlayerPile(5, player1CardPile, 1);
        addCardsToPlayerPile(5, player2CardPile, 2);
        addCardsToPlayerPile(5, player3CardPile, 3);
    }

    @FXML
    public void initialize() {
        createCubes();                 // Create game cubes
        initializeColorToAreaMap();    // Set up area mapping
        setAreaBackgroundImages();     // Load area images
        updateAddCardsButtons();       // Update buttons
        initializeColorToValueMap();   // Set up scoring values
        initializeAreaTotalValueMap(); // Initialize area tracking
        updatePlayerScores();          // Set initial scores
        //chooseCardsInOrder();
        
        player2CardPile.setVisible(false);
        player3CardPile.setVisible(false);

        // Configure resolve button
        resolveFarmButton.setOnAction(_ -> {
            handleResolveFarmAction();
            if (cubeFlowPane.getChildren().isEmpty()) {
                endGame();
            }
        });
        
        updateCardPileCount();          // Update deck counter
        chooseCardFromPlayer(1);        // Start with player 1
    }

    // Initializes the game state
    private void initializeGame() {
        createAndShuffleCards();
    }

    /**
     * Sets up background images for each area
     * Maps colors to their corresponding image files
     */
    private void setAreaBackgroundImages() {
        String imageFolderPath = "src/hellofx/resources/areas/";

        // Define color to image file mapping
        Map<Color, String> colorToImageFileMap = new HashMap<>();
        colorToImageFileMap.put(Color.YELLOW, "yellow.png");
        colorToImageFileMap.put(Color.GREEN, "green.png");
        colorToImageFileMap.put(Color.RED, "red.png");
        colorToImageFileMap.put(Color.BLUE, "blue.png");
        colorToImageFileMap.put(Color.PURPLE, "purple.png");
        colorToImageFileMap.put(Color.BLACK, "black.png");

        // Apply background images to each area
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
                }
            }
        }
    }

    /**
     * Initializes the mapping between colors and areas
     * Associates each color with its corresponding StackPane area
     */
    private void initializeColorToAreaMap() {
        colorToAreaMap = new HashMap<>();
        colorToAreaMap.put(Color.YELLOW, area1);
        colorToAreaMap.put(Color.GREEN, area2);
        colorToAreaMap.put(Color.RED, area3);
        colorToAreaMap.put(Color.BLUE, area4);
        colorToAreaMap.put(Color.PURPLE, area5);
        colorToAreaMap.put(Color.BLACK, area6);
    }

    /**
     * Initializes the scoring values for different colors
     * Sets up point values for scoring calculations
     */
    private void initializeColorToValueMap() {
        colorToValueMap = new HashMap<>();
        colorToValueMap.put(Color.GREEN, 1);
        colorToValueMap.put(Color.BLUE, 2);
        colorToValueMap.put(Color.YELLOW, 3);
    }

    // Initializes the total value tracking for each area
    private void initializeAreaTotalValueMap() {
        areaTotalValueMap = new HashMap<>();
        areaTotalValueMap.put(area1, 0);
        areaTotalValueMap.put(area2, 0);
        areaTotalValueMap.put(area3, 0);
        areaTotalValueMap.put(area4, 0);
        areaTotalValueMap.put(area5, 0);
        areaTotalValueMap.put(area6, 0);
    }

    /**
     * Creates an ImageView for a card with proper styling and event handling
     * @param card The card to create an image view for
     * @return ImageView representing the card
     */
    private ImageView createCardImageView(Card card) {
        String cardColor = card.getColor().equalsIgnoreCase("orange") ? "black" : card.getColor();
        String imagePath = "src/hellofx/resources/cards/" + card.getType() + "_" + card.getValue() + "_" + cardColor + ".png";
        try {
            Image image = new Image(new FileInputStream(imagePath));
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(100);
            imageView.setFitHeight(150);
            imageView.setPreserveRatio(false);
            imageView.setSmooth(true);
            imageView.setUserData(card);
            return imageView;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Label label = new Label(card.toString());
            label.setStyle("-fx-text-fill: white;");
            StackPane fallbackPane = new StackPane(label);
            fallbackPane.setStyle("-fx-border-color: black; -fx-pref-width: 100; -fx-pref-height: 150;");
            fallbackPane.setUserData(card);
            return new ImageView();
        }
    }

    // Initializes the card pile with shuffled card images
    private void createAndShuffleCards() {
        List<ImageView> cards = new ArrayList<>();
        for (Card card : game.getDeck()) {
            ImageView cardImageView = createCardImageView(card);
            cardImageView.setFitWidth(100);
            cardImageView.setFitHeight(150);
            cards.add(cardImageView);
        }
        
        // Shuffle and add to card pile
        Collections.shuffle(cards);
        cardPile.getChildren().clear();
        for (ImageView cardImageView : cards) {
            cardPile.getChildren().add(cardImageView);
        }
    }

    /**
     * Creates the game cubes and adds them to the cube flow pane
     * Sets up cube appearance and interaction handlers
     */
    private void createCubes() {
        List<StackPane> cubes = new ArrayList<>();
        for (int i = 0; i < 26; i++) {
            cubes.add(createCubeWithBorder(Color.YELLOW));
            cubes.add(createCubeWithBorder(Color.GREEN));
            cubes.add(createCubeWithBorder(Color.BLUE));
        }
        // Randomize cube order and add to display
        Collections.shuffle(cubes);
        cubeFlowPane.getChildren().addAll(cubes);
    }

    /**
     * Creates a single 3D cube with a white border
     * @param color The color of the inner cube
     * @return StackPane containing the bordered cube
     */
    private StackPane createCubeWithBorder(Color color) {
        StackPane stack = new StackPane();
        Box borderBox = new Box(22, 22, 22); // Slightly larger for the border
        borderBox.setMaterial(new PhongMaterial(Color.WHITE));
        Box cube = new Box(20, 20, 20); // Smaller cube
        cube.setMaterial(new PhongMaterial(color));
        stack.getChildren().addAll(borderBox, cube);
        return stack;
    }
    /**
     * Handles automated play for bots (Player 2 and Player 3)
     * @param playerNumber The player number (2 or 3)
     */
    private void playAutomaticallyForPlayer(int playerNumber) {
        // Determine which player's pile to use
        VBox playerPile = (playerNumber == 2) ? player2CardPile : player3CardPile;
        // Get all cards in player's pile
        List<ImageView> cardImageViews = playerPile.getChildren().stream()
                .filter(node -> node instanceof ImageView)
                .map(node -> (ImageView) node)
                .collect(Collectors.toList());
        
        // Check if player needs more cards
        if (cardImageViews.size() == 4) {
            PauseTransition addCardPause = new PauseTransition(Duration.seconds(2)); // 2-second delay before adding a card
            addCardPause.setOnFinished(event -> {
                addCardsToPlayerPile(1, playerPile, playerNumber);
                playCardAfterDelay(playerNumber, playerPile);
            });
            addCardPause.play();
        } else {
            playCardAfterDelay(playerNumber, playerPile);
        }
    }

    /**
     * Handles the delayed playing of cards for bots
     * @param playerNumber The player number
     * @param playerPile The player's card pile
     */
    private void playCardAfterDelay(int playerNumber, VBox playerPile) {
        List<ImageView> cardImageViews = playerPile.getChildren().stream()
                .filter(node -> node instanceof ImageView)
                .map(node -> (ImageView) node)
                .collect(Collectors.toList());

        if (!cardImageViews.isEmpty()) {
            // Randomly select a card to play
            Random random = new Random();
            int randomIndex = random.nextInt(cardImageViews.size());
            ImageView cardImageView = cardImageViews.get(randomIndex);
            Card card = (Card) cardImageView.getUserData();

            PauseTransition playCardPause = new PauseTransition(Duration.seconds(2)); // 2-second delay before playing a card
            playCardPause.setOnFinished(event -> {
                handleCardSelection(card, playerPile, cardImageView, playerNumber);

                // Update game state based on player
                if (playerNumber == 2) {
                    player2CardSelected = true;
                    currentPlayerTurn = 3;
                    transitionToNextPlayer(player3CardPile, player1CardPile, player2CardPile);
                } else if (playerNumber == 3) {
                    player3CardSelected = true;
                    currentPlayerTurn = 1;
                    transitionToNextPlayer(player1CardPile, player2CardPile, player3CardPile);
                }

                // Enable resolve button if all players have selected cards
                if (player1CardSelected && player2CardSelected && player3CardSelected) {
                    resolveFarmButton.setDisable(false);
                }
            });
            playCardPause.play();
        }
    }

    /**
     * Creates a mapping of cards to their indices in a player's pile
     * @param playerPile The player's card pile
     * @return Map of Cards to their indices
     */
    private Map<Card, Integer> getCardIndicesInPlayerPile(VBox playerPile) {
        Map<Card, Integer> cardIndexMap = new HashMap<>();
        for (int i = 0; i < playerPile.getChildren().size(); i++) {
            if (playerPile.getChildren().get(i) instanceof ImageView) {
                ImageView cardImageView = (ImageView) playerPile.getChildren().get(i);
                Card card = (Card) cardImageView.getUserData();
                cardIndexMap.put(card, i);
            }
        }
        return cardIndexMap;
    }

    /**
     * Adds a card to a player's pile
     * @param card The card to add
     * @param playerPile The target player's pile
     * @param playerNumber The player number
     */
    private void addCardToPlayerPile(Card card, VBox playerPile, int playerNumber) {
        ImageView cardImageView = createCardImageView(card);
        cardImageView.setFitWidth(50);
        cardImageView.setFitHeight(75);
        cardImageView.setPreserveRatio(false);
        cardImageView.setSmooth(true);
        cardImageView.setUserData(card);

        // Handle differently for human player (player 1)
        if (playerNumber == 1) {
            // Add click handler for human player
            cardImageView.setOnMouseClicked(_ -> {
                if (currentPlayerTurn == 1 && !player1CardSelected) {
                    handleCardSelection(card, player1CardFlowPane, cardImageView, playerNumber);
                    player1CardSelected = true;
                    currentPlayerTurn = 2;
                    transitionToNextPlayer(player2CardPile, player1CardPile, player3CardPile);
                }
                if (player1CardSelected && player2CardSelected && player3CardSelected) {
                    resolveFarmButton.setDisable(false);
                }
            });
            player1CardFlowPane.getChildren().add(cardImageView);
        } else {
            // Add card to bot's pile
            playerPile.getChildren().add(cardImageView);
        }
    }

    // Shows the current count of cards in the card pile
    private void updateCardPileCount() {
        int cardCount = cardPile.getChildren().size();
        cardPileCountLabel.setText("Cards: " + cardCount);
    }

    /**
     * Manages the visibility of player piles during turn transitions
     * @param nextPlayerPile The pile of the next player to be visible
     * @param otherPlayerPile1 First pile to hide
     * @param otherPlayerPile2 Second pile to hide
     */
    private void transitionToNextPlayer(VBox nextPlayerPile, VBox otherPlayerPile1, VBox otherPlayerPile2) {
        nextPlayerPile.setVisible(true);
        otherPlayerPile1.setVisible(false);
        otherPlayerPile2.setVisible(false);
    }

    /**
     * Handles the selection and playing of cards by players
     * @param card The selected card
     * @param playerPile The pile from which the card was selected
     * @param cardImageView The visual representation of the card
     * @param playerNumber The player who selected the card
     */
    private void handleCardSelection(Card card, Pane playerPile, ImageView cardImageView, int playerNumber) {
        System.out.println("Player " + playerNumber + " selected card: " + card);
        playerPile.getChildren().remove(cardImageView);
        playerCardMap.put(playerNumber, card);
        String cardColorStr = card.getColor().equalsIgnoreCase("orange") ? "black" : card.getColor();

        // Find target area for card placement
        StackPane targetArea = getTargetAreaByColor((GridPane) playerPile.getScene().lookup("#mainGrid"), cardColorStr);
        if (targetArea != null) transitionCard(cardImageView, playerPile, cardColorStr, playerNumber);
        else System.out.println("Target area not found for color: " + cardColorStr);
        addCardsButton1.setDisable(false);
        //addCardsButton2.setDisable(false);
        //addCardsButton3.setDisable(false);

        // Handle turn transitions and bots actions
        if (playerNumber == 1) {
            currentPlayerTurn = 2;
            transitionToNextPlayer(player2CardPile, player1CardPile, player3CardPile);
            playAutomaticallyForPlayer(2);
        } else if (playerNumber == 2) {
            currentPlayerTurn = 3;
            transitionToNextPlayer(player3CardPile, player1CardPile, player2CardPile);
            playAutomaticallyForPlayer(3);
        } else if (playerNumber == 3) {
            currentPlayerTurn = 1;
            transitionToNextPlayer(player1CardPile, player2CardPile, player3CardPile);
        }
        if (player1CardSelected && player2CardSelected && player3CardSelected) resolveFarmButton.setDisable(false);
    }

    /**
     * Animates card movement from player's hand to target area
     * @param cardImageView The card to animate
     * @param startPane The starting position
     * @param cardColor The color of the card (determines target area)
     * @param playerNumber The player who played the card
     */
    private void transitionCard(ImageView cardImageView, Pane startPane, String cardColor, int playerNumber) {
        ImageView cardClone = new ImageView(cardImageView.getImage());
        cardClone.setFitWidth(50);
        cardClone.setFitHeight(75);

        // Get reference to main grid and target area
        GridPane mainGrid = (GridPane) startPane.getScene().lookup("#mainGrid");
        StackPane targetArea = getTargetAreaByColor(mainGrid, cardColor);
        Bounds targetBounds = targetArea.localToScene(targetArea.getBoundsInLocal());
        double centerX = targetBounds.getMinX() + targetBounds.getWidth() / 2 - cardClone.getFitWidth() / 2;
        double centerY = targetBounds.getMinY() + targetBounds.getHeight() / 2 - cardClone.getFitHeight() / 2;
        Bounds playerPileBounds;
        if (playerNumber == 1) playerPileBounds = startPane.getScene().lookup("#player1CardPile").localToScene(startPane.getBoundsInLocal());
        else if (playerNumber == 2) playerPileBounds = startPane.getScene().lookup("#player2CardPile").localToScene(startPane.getBoundsInLocal());
        else playerPileBounds = startPane.getScene().lookup("#player3CardPile").localToScene(startPane.getBoundsInLocal());
        cardClone.setLayoutX(playerPileBounds.getMinX());
        cardClone.setLayoutY(playerPileBounds.getMinY());
        mainGrid.getChildren().add(cardClone);
        TranslateTransition transition = new TranslateTransition(Duration.seconds(1), cardClone);
        transition.setToX(centerX - cardClone.getLayoutX());
        transition.setToY(centerY - cardClone.getLayoutY());
        transition.setInterpolator(javafx.animation.Interpolator.EASE_BOTH);

        // Handle animation completion
        transition.setOnFinished(_ -> {
            mainGrid.getChildren().remove(cardClone);
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
                cardVBox.setPrefSize(150, 150);
                cardVBox.setStyle("-fx-background-color: transparent;");
                cardVBox.setAlignment(Pos.TOP_CENTER);
                targetArea.getChildren().add(cardVBox);
            }
            cardVBox.getChildren().add(cardImageView);
        });
        transition.play();
    }

    /**
     * Maps card colors to their corresponding target areas in the game grid
     * @param mainGrid The main game grid containing all areas
     * @param cardColor The color of the card to map
     * @return StackPane representing the target area for the given color
     * @throws IllegalArgumentException if the card color is unknown
     */
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
                return (StackPane) mainGrid.lookup("#area6");
            default:
                throw new IllegalArgumentException("Unknown card color: " + cardColor);
        }
    }

    /**
     * Deals cards from the main deck to a player's pile
     * @param numberOfCards Number of cards to deal
     * @param playerPile The target player's pile
     * @param playerNumber The player receiving the cards
     */
    private void addCardsToPlayerPile(int numberOfCards, VBox playerPile, int playerNumber) {
        for (int i = 0; i < numberOfCards; i++) {
            if (!cardPile.getChildren().isEmpty()) {
                ImageView topCardImageView = (ImageView) cardPile.getChildren().remove(cardPile.getChildren().size() - 1);
                Card topCard = (Card) topCardImageView.getUserData();
                addCardToPlayerPile(topCard, playerPile, playerNumber);
            }
        }
        updateCardPileCount();
        updateAddCardsButtons();
        addCardsButton1.setDisable(true);
        //addCardsButton2.setDisable(true);
        //addCardsButton3.setDisable(true);
    }
    
    // Initiates a new turn by dealing cards to all players
    public void onNextTurn() {
        addCardsToPlayerPile(5, player1CardPile, 1);
        addCardsToPlayerPile(5, player2CardPile, 2);
        addCardsToPlayerPile(5, player3CardPile, 3);
        player1CardSelected = false;
        player2CardSelected = false;
        player3CardSelected = false;
        currentPlayerTurn = 1;
    }

    /**
     * Event handler for the place cubes button
     * Triggers cube placement and disables the button
     * @param event The action event
     */
    @FXML
    private void handlePlaceCubesButtonAction(ActionEvent event) {
        placeCubesInAreas();
        placeCubesButton.setDisable(true);
    }

    // Event handler for adding cards to Player 1's pile
    @FXML
    private void handleAddCardsToPlayer1Action() {
        addCardsToPlayerPile(1, player1CardPile, 1);
        player1CardSelected = false;
        updateAddCardsButtons();
        placeCubesButton.setDisable(false);
        //addCardsButton2.setDisable(true);
        //addCardsButton3.setDisable(false);
    }

    @FXML
    private void handleAddCardsToPlayer2Action() {
    }

    @FXML
    private void handleAddCardsToPlayer3Action() {
    }

    /**
     * Handles navigation back to the start screen
     * @param event The button click event
     */
    @FXML
    private void handleBackToStartAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hellofx/resources/begin.fxml"));
            Parent root = loader.load();
            BeginController beginController = loader.getController();
            beginController.setMainApp(mainApp);
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setFullScreen(true);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Places cubes in game areas and manages cube distribution
     * Handles cube value calculation and tracking
     */
    private void placeCubesInAreas() {
        StackPane[] areas = {area1, area2, area3, area4, area5, area6};
        for (StackPane area : areas) {
            VBox cubeVBox = null;
            for (javafx.scene.Node node : area.getChildren()) {
                if (node instanceof VBox && "cubes".equals(node.getId())) {
                    cubeVBox = (VBox) node;
                    break;
                }
            }
            if (cubeVBox == null) {
                cubeVBox = new VBox();
                cubeVBox.setId("cubes");
                cubeVBox.setPrefSize(150, 150);
                cubeVBox.setStyle("-fx-background-color: transparent;");
                cubeVBox.setAlignment(Pos.BOTTOM_CENTER);
                area.getChildren().add(cubeVBox);
            }
            int totalValue = 0;
            for (javafx.scene.Node node : cubeVBox.getChildren()) {
                if (node instanceof StackPane) {
                    StackPane cubeStack = (StackPane) node;
                    if (cubeStack.getChildren().size() > 1) {
                        PhongMaterial material = (PhongMaterial) ((Box) cubeStack.getChildren().get(1)).getMaterial();
                        Color cubeColor = (Color) material.getDiffuseColor();
                        Integer cubeValue = colorToValueMap.get(cubeColor);
                        if (cubeValue != null) totalValue += cubeValue;
                    }
                }
            }
            areaTotalValueMap.put(area, totalValue);
            if (cubeVBox.getChildren().size() < 10 && !cubeFlowPane.getChildren().isEmpty()) {
                StackPane originalCubeStack = (StackPane) cubeFlowPane.getChildren().remove(0);
                VBox.setMargin(originalCubeStack, new Insets(5, 0, 0, 0));
                cubeVBox.getChildren().add(originalCubeStack);
                if (originalCubeStack.getChildren().size() > 1) {
                    PhongMaterial material = (PhongMaterial) ((Box) originalCubeStack.getChildren().get(1)).getMaterial();
                    Color cubeColor = (Color) material.getDiffuseColor();
                    Integer cubeValue = colorToValueMap.get(cubeColor);
                    totalValue += cubeValue;
                    areaTotalValueMap.put(area, totalValue);
                }
            }
            printCubesInArea(area);
        }

    }

    /**
     * Updates the state of add cards buttons based on pile sizes
     * Enables/disables buttons according to game rules
     */
    private void updateAddCardsButtons() {
        addCardsButton1.setDisable(player1CardPile.getChildren().size() != 4);
        //addCardsButton2.setDisable(player2CardPile.getChildren().size() != 4);
        //addCardsButton3.setDisable(player3CardPile.getChildren().size() != 4);
    }

    /**
     * Updates and displays scores for all players
     * Calculates and accumulates points from current round
     */
    private void updatePlayerScores() {
        Map<Integer, Integer> playerScores = calculatePlayerScores();
        int player1Score = playerScores.getOrDefault(1, 0);
        int player2Score = playerScores.getOrDefault(2, 0);
        int player3Score = playerScores.getOrDefault(3, 0);
        scorePlayer1 += player1Score;
        scorePlayer2 += player2Score;
        scorePlayer3 += player3Score;
        player1.setPoints(scorePlayer1);
        player2.setPoints(scorePlayer2);
        player3.setPoints(scorePlayer3);
        player1ScoreLabel.setText("Player 1 Score: " + scorePlayer1);
        player2ScoreLabel.setText("Player 2 Score: " + scorePlayer2);
        player3ScoreLabel.setText("Player 3 Score: " + scorePlayer3);
    }

    /**
     * Manages the order of card selection for all players
     * Coordinates between manual and automatic card selection
     */
    private void chooseCardsInOrder() {
        chooseCardFromPlayer(1);
        chooseCardFromPlayer(2);
        chooseCardFromPlayer(3);
    }

    /**
     * Handles card selection process for a specific player
     * @param playerNumber The player whose turn it is
     */
    private void chooseCardFromPlayer(int playerNumber) {
        if (playerNumber == 1) {
            // Player 1 selects a card manually
            player1CardPile.setVisible(true);
            player2CardPile.setVisible(false);
            player3CardPile.setVisible(false);
        } else {
            // Player 2 and Player 3 select cards automatically
            playAutomaticallyForPlayer(playerNumber);
        }
    }

    @FXML
    private StackPane discardPile;

    /**
     * Calculates scores for all players based on card combinations and area values
     * 
     * This function handles various card combination scenarios:
     * - Single cards (Bird, Fox, Fleeing Bird)
     * - Two card combinations (Bird vs Bird, Fox vs Fox, Bird vs Fleeing Bird, etc.)
     * - Three card combinations (Triple Birds, Triple Fox, etc.)
     * 
     * Score calculation rules:
     * - Bird cards collect cube values from their areas
     * - Fox cards steal points from Bird cards
     * - Fleeing Birds can escape with 1 point if green cube present
     * - Ties between Birds are resolved with dice rolls
     * 
     * @return Map<Integer, Integer> where key is player number and value is their score
     *         for the current round
     */
    private Map<Integer, Integer> calculatePlayerScores() {
        Map<Integer, Integer> playerScores = new HashMap<>();
        Random random = new Random();
        StackPane[] areas = {area1, area2, area3, area4, area5, area6};
        for (StackPane area : areas) {
            VBox cardVBox = null;
            for (javafx.scene.Node node : area.getChildren()) {
                if (node instanceof VBox && "cards".equals(node.getId())) {
                    cardVBox = (VBox) node;
                    break;
                }
            }
            if (cardVBox != null) {
                if (cardVBox.getChildren().size() == 1) {
                    ImageView cardImageView = (ImageView) cardVBox.getChildren().get(0);
                    Card card = (Card) cardImageView.getUserData();
                    int playerNumber = getPlayerNumberByCard(card);
                    if (card != null) {
                        if (card.getType() == Card.Type.BIRD) {
                            int totalScore = playerScores.getOrDefault(playerNumber, 0);
                            totalScore += areaTotalValueMap.getOrDefault(area, 0);
                            playerScores.put(playerNumber, totalScore);
                            discardPile.getChildren().add(cardImageView);
                            clearCubesInVBox(area);
                        } else if (card.getType() == Card.Type.FOX) {
                            discardPile.getChildren().add(cardImageView);
                        } else if (card.getType() == Card.Type.FLEEING_BIRD) {
                            int totalScore = playerScores.getOrDefault(playerNumber, 0);
                            totalScore += areaTotalValueMap.getOrDefault(area, 0);
                            playerScores.put(playerNumber, totalScore);
                            discardPile.getChildren().add(cardImageView);
                            clearCubesInVBox(area);
                        }
                        area.getChildren().remove(cardVBox);
                    }
                }
                if (cardVBox.getChildren().size() == 2) {
                    Card fleeingBirdCard = null;
                    Card foxCard = null;
                    int foxCount = 0;
                    List<Card> birdCards = new ArrayList<>();
                    for (javafx.scene.Node node : cardVBox.getChildren()) {
                        ImageView cardImageView = (ImageView) node;
                        Card card = (Card) cardImageView.getUserData();
                        if (card != null) {
                            if (card.getType() == Card.Type.BIRD) birdCards.add(card);
                            else if (card.getType() == Card.Type.FLEEING_BIRD) fleeingBirdCard = card;
                            else if (card.getType() == Card.Type.FOX) {
                                foxCard = card;
                                foxCount++;
                            }
                        }
                    }
                    if (birdCards.size() == 2) {
                        Map<Card, Integer> cardTotalValues = new HashMap<>();
                        boolean tie;
                        do {
                            tie = false;
                            cardTotalValues.clear();
                            for (Card card : birdCards) {
                                int diceRoll = random.nextInt(6) + 1;
                                int totalValue = card.getValue() + diceRoll;
                                cardTotalValues.put(card, totalValue);
                                int cardPlayerNumber = getPlayerNumberByCard(card);
                                System.out.println("Player " + cardPlayerNumber + " rolled " + diceRoll + " for card with value " + card.getValue() + ", total: " + totalValue);
                            }
                            Card winningCard = null;
                            int maxTotalValue = 0;
                            int maxCount = 0;
                            for (Map.Entry<Card, Integer> entry : cardTotalValues.entrySet()) {
                                if (entry.getValue() > maxTotalValue) {
                                    maxTotalValue = entry.getValue();
                                    winningCard = entry.getKey();
                                    maxCount = 1;
                                } else if (entry.getValue() == maxTotalValue) maxCount++;
                            }
                            if (maxCount > 1) {
                                tie = true;
                                System.out.println("Tie detected, rolling dice again...");
                            } else {
                                if (winningCard != null) {
                                    int winningPlayerNumber = getPlayerNumberByCard(winningCard);
                                    int totalScore = playerScores.getOrDefault(winningPlayerNumber, 0);
                                    totalScore += areaTotalValueMap.getOrDefault(area, 0);
                                    playerScores.put(winningPlayerNumber, totalScore);
                                    clearCubesInVBox(area);
                                    for (Card birdCard : birdCards) {
                                        ImageView birdCardImageView = findCardImageView(birdCard);
                                        if (birdCardImageView != null) discardPile.getChildren().add(birdCardImageView);
                                    }
                                    area.getChildren().remove(cardVBox);
                                    clearCubesInVBox(area);
                                }
                            }
                        } while (tie);
                    }
                    else if (foxCount == 2) {
                        System.out.println("Two fox cards in VBox, no score added.");
                        for (javafx.scene.Node node : cardVBox.getChildren()) {
                            ImageView cardImageView = (ImageView) node;
                            Card card = (Card) cardImageView.getUserData();
                            if (card != null && card.getType() == Card.Type.FOX) discardPile.getChildren().add(cardImageView);
                        }
                        area.getChildren().remove(cardVBox);
                    }
                    else if (birdCards.size() == 1 && fleeingBirdCard != null) {
                        int fleeingBirdPlayerNumber = getPlayerNumberByCard(fleeingBirdCard);
                        int birdPlayerNumber = getPlayerNumberByCard(birdCards.get(0));
                        int totalValue = areaTotalValueMap.getOrDefault(area, 0);
                        boolean hasGreenCube = false;
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
                            if (hasGreenCube) break;
                        }
                        if (hasGreenCube) {
                            int fleeingBirdScore = playerScores.getOrDefault(fleeingBirdPlayerNumber, 0);
                            fleeingBirdScore += 1;
                            playerScores.put(fleeingBirdPlayerNumber, fleeingBirdScore);
                            int birdScore = playerScores.getOrDefault(birdPlayerNumber, 0);
                            birdScore += (totalValue-1);
                            playerScores.put(birdPlayerNumber, birdScore);
                            ImageView birdCardImageView = findCardImageView(birdCards.get(0));
                            if (birdCardImageView != null) discardPile.getChildren().add(birdCardImageView);
                            ImageView fleeingCardImageView = findCardImageView(fleeingBirdCard);
                            if (fleeingCardImageView != null) discardPile.getChildren().add(fleeingCardImageView);
                            area.getChildren().remove(cardVBox);
                            clearCubesInVBox(area);
                        } else {
                            int birdScore = playerScores.getOrDefault(birdPlayerNumber, 0);
                            birdScore += totalValue;
                            playerScores.put(birdPlayerNumber, birdScore);
                            ImageView birdCardImageView = findCardImageView(birdCards.get(0));
                            if (birdCardImageView != null) discardPile.getChildren().add(birdCardImageView);
                            ImageView fleeingCardImageView = findCardImageView(fleeingBirdCard);
                            if (fleeingCardImageView != null) discardPile.getChildren().add(fleeingCardImageView);
                            area.getChildren().remove(cardVBox);
                            clearCubesInVBox(area);
                        }
                    }
                    else if (birdCards.size() == 1 && foxCard != null) {
                        int foxPlayerNumber = getPlayerNumberByCard(foxCard);
                        int birdCardValue = birdCards.get(0).getValue();
                        int totalScore = playerScores.getOrDefault(foxPlayerNumber, 0);
                        totalScore += birdCardValue;
                        playerScores.put(foxPlayerNumber, totalScore);
                        ImageView foxCardImageView = findCardImageView(foxCard);
                        if (foxCardImageView != null) discardPile.getChildren().add(foxCardImageView);
                        area.getChildren().remove(cardVBox);
                    }
                    else if (fleeingBirdCard != null && foxCard != null) {
                        int foxPlayerNumber = getPlayerNumberByCard(foxCard);
                        int fleeingBirdCardValue = fleeingBirdCard.getValue();
                        int totalScore1 = playerScores.getOrDefault(foxPlayerNumber, 0);
                        totalScore1 += fleeingBirdCardValue;
                        playerScores.put(foxPlayerNumber, totalScore1);
                        boolean hasGreenCube = false;
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
                            if (hasGreenCube) break;
                        }
                        if (hasGreenCube) {
                            int fleeingBirdNumber = getPlayerNumberByCard(fleeingBirdCard);
                            int totalScore2 = playerScores.getOrDefault(fleeingBirdNumber, 0);
                            totalScore2 += 1;
                            playerScores.put(fleeingBirdNumber, totalScore2);
                        }
                        ImageView foxCardImageView = findCardImageView(foxCard);
                        if (foxCardImageView != null) discardPile.getChildren().add(foxCardImageView);
                        removeGreenCube(area);
                        area.getChildren().remove(cardVBox);
                    }
                }
                if (cardVBox.getChildren().size() == 3) {
                    List<Card> birdCards = new ArrayList<>();
                    List<Card> foxCards = new ArrayList<>();
                    Card fleeingBirdCard = null;
                    for (javafx.scene.Node node : cardVBox.getChildren()) {
                        ImageView cardImageView = (ImageView) node;
                        Card card = (Card) cardImageView.getUserData();
                        if (card != null) {
                            if (card.getType() == Card.Type.BIRD) birdCards.add(card);
                            else if (card.getType() == Card.Type.FOX) foxCards.add(card);
                            else if (card.getType() == Card.Type.FLEEING_BIRD) fleeingBirdCard = card;
                        }
                    }
                    if (birdCards.size() == 3) {
                        Map<Card, Integer> cardTotalValues = new HashMap<>();
                        boolean tie;
                        do {
                            tie = false;
                            cardTotalValues.clear();
                            for (Card card : birdCards) {
                                int diceRoll = random.nextInt(6) + 1;
                                int totalValue = card.getValue() + diceRoll;
                                cardTotalValues.put(card, totalValue);
                                int cardPlayerNumber = getPlayerNumberByCard(card);
                            }
                            Card winningCard = null;
                            int maxTotalValue = 0;
                            int maxCount = 0;
                            for (Map.Entry<Card, Integer> entry : cardTotalValues.entrySet()) {
                                if (entry.getValue() > maxTotalValue) {
                                    maxTotalValue = entry.getValue();
                                    winningCard = entry.getKey();
                                    maxCount = 1;
                                } else if (entry.getValue() == maxTotalValue) maxCount++;
                            }
                            if (maxCount > 1) {
                                tie = true;
                            } else {
                                if (winningCard != null) {
                                    int winningPlayerNumber = getPlayerNumberByCard(winningCard);
                                    int totalScore = playerScores.getOrDefault(winningPlayerNumber, 0);
                                    totalScore += areaTotalValueMap.getOrDefault(area, 0);
                                    playerScores.put(winningPlayerNumber, totalScore);
                                    clearCubesInVBox(area);
                                    for (Card birdCard : birdCards) {
                                        ImageView birdCardImageView = findCardImageView(birdCard);
                                        if (birdCardImageView != null) discardPile.getChildren().add(birdCardImageView);
                                    }
                                    area.getChildren().remove(cardVBox);
                                    clearCubesInVBox(area);
                                }
                            }
                        } while (tie);
                    }
                    else if (foxCards.size() == 3) {
                        for (Card foxCard : foxCards) {
                            ImageView foxCardImageView = findCardImageView(foxCard);
                            if (foxCardImageView != null) discardPile.getChildren().add(foxCardImageView);
                        }
                        area.getChildren().remove(cardVBox);
                    }
                    else if (foxCards.size() == 2 && fleeingBirdCard != null) {
                        int fleeingBirdPlayerNumber = getPlayerNumberByCard(fleeingBirdCard);
                        int fleeingBirdCardValue = fleeingBirdCard.getValue();
                        Map<Card, Integer> foxCardTotalValues = new HashMap<>();
                        boolean hasGreenCube = false;
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
                            if (hasGreenCube) break;
                        }
                        if (hasGreenCube) {
                            int fleeingBirdScore = playerScores.getOrDefault(fleeingBirdPlayerNumber, 0);
                            fleeingBirdScore += 1;
                            playerScores.put(fleeingBirdPlayerNumber, fleeingBirdScore);
                        }
                        for (Card foxCard : foxCards) {
                            int diceRoll = random.nextInt(6) + 1;
                            int totalValue = foxCard.getValue() + diceRoll;
                            foxCardTotalValues.put(foxCard, totalValue);
                            int foxPlayerNumber = getPlayerNumberByCard(foxCard);
                        }
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
                            for (Card foxCard : foxCards) {
                                ImageView foxCardImageView = findCardImageView(foxCard);
                                if (foxCardImageView != null) discardPile.getChildren().add(foxCardImageView);
                            }
                            removeGreenCube(area);
                            area.getChildren().remove(cardVBox);
                        }
                    }
                    else if (birdCards.size() == 2 && foxCards.size() == 1) {
                        int foxPlayerNumber = getPlayerNumberByCard(foxCards.get(0));
                        int birdCardValueSum = birdCards.stream().mapToInt(Card::getValue).sum();
                        int totalScore = playerScores.getOrDefault(foxPlayerNumber, 0);
                        totalScore += birdCardValueSum;
                        playerScores.put(foxPlayerNumber, totalScore);
                        ImageView foxCardImageView = findCardImageView(foxCards.get(0));
                        if (foxCardImageView != null) discardPile.getChildren().add(foxCardImageView);
                        area.getChildren().remove(cardVBox);
                    }
                    else if (foxCards.size() == 1 && birdCards.size() == 1 && fleeingBirdCard != null) {
                        int fleeingBirdPlayerNumber = getPlayerNumberByCard(fleeingBirdCard);
                        int foxPlayerNumber = getPlayerNumberByCard(foxCards.get(0));
                        int birdCardValue = birdCards.get(0).getValue();
                        int fleeingBirdCardValue = fleeingBirdCard.getValue();
                        boolean hasGreenCube = false;
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
                            if (hasGreenCube) break;
                        }
                        if (hasGreenCube) {
                            int fleeingBirdScore = playerScores.getOrDefault(fleeingBirdPlayerNumber, 0);
                            fleeingBirdScore += 1;
                            playerScores.put(fleeingBirdPlayerNumber, fleeingBirdScore);
                        }
                        int totalScore = playerScores.getOrDefault(foxPlayerNumber, 0);
                        totalScore += birdCardValue + fleeingBirdCardValue;
                        playerScores.put(foxPlayerNumber, totalScore);
                        ImageView foxCardImageView = findCardImageView(foxCards.get(0));
                        if (foxCardImageView != null) discardPile.getChildren().add(foxCardImageView);
                        removeGreenCube(area);
                        area.getChildren().remove(cardVBox);
                    }
                    else if (birdCards.size() == 2 && fleeingBirdCard != null) {
                        int fleeingBirdPlayerNumber = getPlayerNumberByCard(fleeingBirdCard);
                        int totalValue = areaTotalValueMap.getOrDefault(area, 0);
                        Map<Card, Integer> birdCardTotalValues = new HashMap<>();
                        boolean hasGreenCube = false;
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
                            if (hasGreenCube) break;
                        }
                        if (hasGreenCube) {
                            int fleeingBirdScore = playerScores.getOrDefault(fleeingBirdPlayerNumber, 0);
                            fleeingBirdScore += 1;
                            playerScores.put(fleeingBirdPlayerNumber, fleeingBirdScore);
                            for (Card birdCard : birdCards) {
                                int diceRoll = random.nextInt(6) + 1;
                                int totalValueWithDice = birdCard.getValue() + diceRoll;
                                birdCardTotalValues.put(birdCard, totalValueWithDice);
                                int birdPlayerNumber = getPlayerNumberByCard(birdCard);
                            }
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
                                for (Card birdCard : birdCards) {
                                    ImageView birdCardImageView = findCardImageView(birdCard);
                                    if (birdCardImageView != null) discardPile.getChildren().add(birdCardImageView);
                                }
                                ImageView fleeingBirdCardImageView = findCardImageView(fleeingBirdCard);
                                if (fleeingBirdCardImageView != null) discardPile.getChildren().add(fleeingBirdCardImageView);
                                area.getChildren().remove(cardVBox);
                                removeGreenCube(area);
                                clearCubesInVBox(area);
                            }
                        } else {
                            for (Card birdCard : birdCards) {
                                int diceRoll = random.nextInt(6) + 1;
                                int totalValueWithDice = birdCard.getValue() + diceRoll;
                                birdCardTotalValues.put(birdCard, totalValueWithDice);
                                int birdPlayerNumber = getPlayerNumberByCard(birdCard);
                            }
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
                                for (Card birdCard : birdCards) {
                                    ImageView birdCardImageView = findCardImageView(birdCard);
                                    if (birdCardImageView != null) discardPile.getChildren().add(birdCardImageView);
                                }
                                ImageView fleeingBirdCardImageView = findCardImageView(fleeingBirdCard);
                                if (fleeingBirdCardImageView != null) discardPile.getChildren().add(fleeingBirdCardImageView);
                                area.getChildren().remove(cardVBox);
                                removeGreenCube(area);
                                clearCubesInVBox(area);
                            }
                        }
                    }
                }
            }
        }
        return playerScores;
    }

    /**
     * Removes a green cube from the specified game area
     * @param area The StackPane representing the game area to remove the green cube from
     */
    private void removeGreenCube(StackPane area) {
        VBox cubeVBox = null;
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
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Determines which player owns a specific card
     * @param card The Card object to find the owner for
     * @return The player number (1-3) who owns the card, or -1 if not found
     */
    private int getPlayerNumberByCard(Card card) {
        for (Map.Entry<Integer, Card> entry : playerCardMap.entrySet()) if (entry.getValue().equals(card)) return entry.getKey();
        return -1; // Return -1 if the card is not found
    }

    /**
     * Clears all cubes from a game area and resets its cube container
     * @param area The StackPane representing the game area to clear
     */
    private void clearCubesInVBox(StackPane area) {
        VBox cubeVBox = null;
        for (javafx.scene.Node node : area.getChildren()) {
            if (node instanceof VBox && "cubes".equals(node.getId())) {
                cubeVBox = (VBox) node;
                break;
            }
        }

        if (cubeVBox == null) {
            cubeVBox = new VBox();
            cubeVBox.setId("cubes");
            cubeVBox.setPrefSize(150, 150);
            cubeVBox.setStyle("-fx-background-color: transparent;");
            cubeVBox.setAlignment(Pos.BOTTOM_CENTER);
            area.getChildren().add(cubeVBox);
        }
        cubeVBox.getChildren().clear();
    }

    /**
     * Prints debug information about cubes in a specific game area
     * @param area The StackPane representing the game area to inspect
     */
    private void printCubesInArea(StackPane area) {
        VBox cubeVBox = null;
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
        }
    }

    /**
     * Adds a card to the discard pile and updates the UI
     * @param card The Card object to be added to the discard pile
     */
    private void addCardToDiscardPile(Card card) {
        ImageView cardImageView = createCardImageView(card);
        cardImageView.setFitWidth(100);
        cardImageView.setFitHeight(150);
        discardPile.getChildren().clear();
        discardPile.getChildren().add(cardImageView);
        //updateDiscardPileCount();
    }

    // Handles the resolution of a farm action round
    @FXML
    private void handleResolveFarmAction() {
        updatePlayerScores();
        for (Map.Entry<Integer, Card> entry : playerCardMap.entrySet()) {
            Card card = entry.getValue();
            if (card != null) {
                ImageView cardImageView = findCardImageView(card);
                if (cardImageView != null) {
                    ((Pane) cardImageView.getParent()).getChildren().remove(cardImageView);
                    addCardToDiscardPile(card);
                }
            }
        }
        playerCardMap.clear();
        if (cubeFlowPane.getChildren().isEmpty()) {
            endGame();
        }
    }

    /**
     * Locates the ImageView associated with a specific card in the game
     * @param card The Card object to find
     * @return The ImageView of the card if found, null otherwise
     */
    private ImageView findCardImageView(Card card) {
        for (VBox playerPile : new VBox[]{player1CardPile, player2CardPile, player3CardPile}) {
            for (javafx.scene.Node node : playerPile.getChildren()) {
                if (node instanceof ImageView) {
                    ImageView cardImageView = (ImageView) node;
                    if (card.equals(cardImageView.getUserData())) return cardImageView;
                }
            }
        }
        StackPane[] areas = {area1, area2, area3, area4, area5, area6};
        for (StackPane area : areas) {
            for (javafx.scene.Node node : area.getChildren()) {
                if (node instanceof VBox && "cards".equals(node.getId())) {
                    VBox cardVBox = (VBox) node;
                    for (javafx.scene.Node cardNode : cardVBox.getChildren()) {
                        if (cardNode instanceof ImageView) {
                            ImageView cardImageView = (ImageView) cardNode;
                            if (card.equals(cardImageView.getUserData())) return cardImageView;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Converts a Color object to its string representation
     * @param color The Color object to convert
     * @return String representation of the color ("yellow", "green", "blue", or "unknown")
     */
    private String getColorName(Color color) {
        if (Color.YELLOW.equals(color)) return "yellow";
        else if (Color.GREEN.equals(color)) return "green";
        else if (Color.BLUE.equals(color)) return "blue";
        else return "unknown";
    }

    @FXML
    private Button reshuffleButton;
    
    // Handles the reshuffling of cards from discard pile to draw pile
    @FXML
    private void handleReshuffleButtonAction() {
        if (cardPile.getChildren().isEmpty()) {
            List<ImageView> cards = new ArrayList<>();
            for (javafx.scene.Node node : discardPile.getChildren())
                if (node instanceof ImageView) cards.add((ImageView) node);
            Collections.shuffle(cards);
            cardPile.getChildren().addAll(cards);
            discardPile.getChildren().clear();
            updateCardPileCount();
        }
    }

    private Stage winnerStage;
    private WinnerController winnerController;

    // Initiates the winner declaration process
    private void declareWinner() {
        List<Player> players = getPlayersSortedByScore();
        showWinnerScreen(players);
    }

    /**
     * Displays the winner screen with final game results
     * @param players List of players sorted by their final scores
     */
    private void showWinnerScreen(List<Player> players) {
        try {
            if (winnerStage == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/hellofx/resources/winner.fxml"));
                Parent root = loader.load();
                winnerController = loader.getController();
                winnerController.setWinners(players);
                winnerController.setMainApp(mainApp);
                winnerStage = new Stage();
                Scene scene = new Scene(root);
                winnerStage.setTitle("Hick Hack");
                InputStream logoStream = getClass().getResourceAsStream("/hellofx/resources/logo/logo.jpg");
                if (logoStream == null) System.out.println("Logo not found!");
                else winnerStage.getIcons().add(new Image(logoStream));
                winnerStage.setScene(scene);
                winnerStage.setFullScreen(true);
                winnerStage.show();
            } else {
                winnerController.setWinners(players);
                winnerStage.setFullScreen(true);
                winnerStage.show();
            }
            Stage currentStage = (Stage) cubeFlowPane.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Handles game end procedures
    private void endGame() {
        updatePlayerScores();
        declareWinner();
    }

    /**
     * Returns a list of players sorted by their scores in descending order
     * @return List of Player objects sorted by points (highest to lowest)
     */
    private List<Player> getPlayersSortedByScore() {
        List<Player> players = getAllPlayers();
        return players.stream()
                .sorted((p1, p2) -> Integer.compare(p2.getPoints(), p1.getPoints()))
                .collect(Collectors.toList());
    }

    private List<Player> getAllPlayers() {
        return Arrays.asList(player1, player2, player3);
    }
}
