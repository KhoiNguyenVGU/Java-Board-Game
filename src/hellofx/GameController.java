package hellofx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
// import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import javafx.stage.Stage;
import javafx.scene.paint.PhongMaterial;
import javafx.event.ActionEvent;

import java.util.Iterator;
import java.io.IOException;
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
    private Button placeCubesButton;

    private Map<StackPane, Integer> areaTotalValueMap = new HashMap<>();

    @FXML
    private Button resolveFarmButton;

    @FXML
    private Button addCardsButton;

    @FXML
    private VBox player1CardPile, player2CardPile, player3CardPile;

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

    private Map<Integer, Integer> playerScores = new HashMap<>();

    private Map<Integer, Card> playerCardMap = new HashMap<>();

    private int currentPlayerTurn = 1;

    int scorePlayer1 = 0;
    int scorePlayer2 = 0;
    int scorePlayer3 = 0;

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
        setAreaColors();
        updateAddCardsButtons();
        initializeColorToAreaMap();
        initializeColorToValueMap();
        initializeAreaTotalValueMap();
        updatePlayerScores();
        chooseCardsInOrder();

        resolveFarmButton.setOnAction(_ -> {
            updatePlayerScores(); // Only update the scores here
        });
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

    private void addCardToPlayerPile(Card card, VBox playerPile, int playerNumber) {
        StackPane cardPane = new StackPane();
        cardPane.setStyle("-fx-border-color: black; -fx-pref-width: 100; -fx-pref-height: 150;");
        cardPane.setStyle("-fx-background-color: " + card.getColor() + ";");
        Label label = new Label(card.toString());
        cardPane.getChildren().add(label);
        cardPane.setUserData(card); // Store the card as user data in the cardPane
    
        // Add click event handler to the card
        cardPane.setOnMouseClicked(_ -> {
            if (playerPile == player1CardPile && currentPlayerTurn == 1 && !player1CardSelected) {
                handleCardSelection(card, playerPile, cardPane, playerNumber);
                player1CardSelected = true;
                currentPlayerTurn = 2; // Move to the next player
            } else if (playerPile == player2CardPile && currentPlayerTurn == 2 && !player2CardSelected) {
                handleCardSelection(card, playerPile, cardPane, playerNumber);
                player2CardSelected = true;
                currentPlayerTurn = 3; // Move to the next player
            } else if (playerPile == player3CardPile && currentPlayerTurn == 3 && !player3CardSelected) {
                handleCardSelection(card, playerPile, cardPane, playerNumber);
                player3CardSelected = true;
                currentPlayerTurn = 1; // Reset to the first player
            }
    
            // Check if all players have selected their cards
            if (player1CardSelected && player2CardSelected && player3CardSelected) {
                // Enable the resolve farm button
                resolveFarmButton.setDisable(false);
            }
        });
    
        playerPile.getChildren().add(cardPane);
    }

    private void handleCardSelection(Card card, VBox playerPile, StackPane cardPane, int playerNumber) {
        // Debug: Print the player number and the card they chose
        System.out.println("Player " + playerNumber + " selected card: " + card);
        // Perform desired action with the selected card
        playerPile.getChildren().remove(cardPane);
        playerCardMap.put(playerNumber, card);
        moveCardToCorrectArea(card, cardPane);
        // Enable the add cards buttons
        addCardsButton1.setDisable(false);
        addCardsButton2.setDisable(false);
        addCardsButton3.setDisable(false);

    }

    private void moveCardToCorrectArea(Card card, StackPane cardPane) {
        Color cardColor = Color.valueOf(card.getColor().toUpperCase());
        StackPane targetArea = colorToAreaMap.get(cardColor);
    
        if (targetArea != null) {
            VBox cardVBox = null;
    
            // Check if there is already a VBox for cards in the StackPane
            for (javafx.scene.Node node : targetArea.getChildren()) {
                if (node instanceof VBox && "cards".equals(node.getId())) {
                    cardVBox = (VBox) node;
                    break;
                }
            }
    
            // If no VBox for cards exists in this area, create one
            if (cardVBox == null) {
                cardVBox = new VBox();
                cardVBox.setId("cards");
                cardVBox.setPrefSize(150, 150); // Set fixed size for VBox to match StackPane
                cardVBox.setStyle("-fx-background-color: transparent;"); // Transparent background
                cardVBox.setAlignment(Pos.TOP_CENTER); // Set alignment for the VBox to center cubes
                targetArea.getChildren().add(cardVBox);
            }
    
            // Add the card to the VBox
            cardVBox.getChildren().add(cardPane);
    
            // Debug: Print the number of cards in the VBox
            System.out.println("Number of cards in VBox for area " + targetArea.getId() + ": " + cardVBox.getChildren().size());
    
            // Print the color of the cubes in the VBox
            for (javafx.scene.Node cube : cardVBox.getChildren()) {
                if (cube instanceof StackPane) {
                    StackPane cubeStack = (StackPane) cube;
                    if (cubeStack.getChildren().size() > 1) {
                        PhongMaterial material = (PhongMaterial) ((Box) cubeStack.getChildren().get(1)).getMaterial();
                        Color cubeColor = (Color) material.getDiffuseColor();
                        System.out.println("Cube color: " + cubeColor);
                    }
                }
            }
        }
    }

    private void addCardsToPlayerPile(int numberOfCards, VBox playerPile, int playerNumber) {
        for (int i = 0; i < numberOfCards; i++) {
            if (!game.getDeck().isEmpty() && !cardPile.getChildren().isEmpty()) {
                Card card = game.getDeck().remove(0); // Remove the card from the deck
                addCardToPlayerPile(card, playerPile, playerNumber); // Add the card to the player pile

                // Remove the corresponding card from the card pile
                cardPile.getChildren().remove(0);
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

        if (cubeFlowPane.getChildren().isEmpty()) {
            System.out.println("Cube flow pane is empty.");
            // Determine the winner based on player scores
            String winner = determineWinner();
            System.out.println("Determined winner: " + winner);
            declareWinner(winner);
            endGame(); // End the game
        } else {
            System.out.println("Cube flow pane is not empty, continue the game.");
        }
    }

    @FXML
    private void handlePlaceCubesButtonAction(ActionEvent event) {
        placeCubesInAreas();
    }

    @FXML
    private void handleAddCardsToPlayer1Action() {
        addCardsToPlayerPile(1, player1CardPile, 1);
        player1CardSelected = false; // Reset the flag after adding a card
        updateAddCardsButtons(); // Update the buttons after adding a card
    }

    @FXML
    private void handleAddCardsToPlayer2Action() {
        addCardsToPlayerPile(1, player2CardPile, 2);
        player2CardSelected = false; // Reset the flag after adding a card
        updateAddCardsButtons(); // Update the buttons after adding a card
    }

    @FXML
    private void handleAddCardsToPlayer3Action() {
        addCardsToPlayerPile(1, player3CardPile, 3);
        player3CardSelected = false; // Reset the flag after adding a card
        updateAddCardsButtons(); // Update the buttons after adding a card
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
        Map<Integer, Integer> playerScores = calculatePlayerScores();
    
        int player1Score = playerScores.getOrDefault(1, 0);
        int player2Score = playerScores.getOrDefault(2, 0);
        int player3Score = playerScores.getOrDefault(3, 0);
    
        scorePlayer1 += player1Score;
        scorePlayer2 += player2Score;
        scorePlayer3 += player3Score;

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
    private VBox discardPile;
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
                    StackPane cardPane = (StackPane) cardVBox.getChildren().get(0);
                    Card card = (Card) cardPane.getUserData();
                    int playerNumber = getPlayerNumberByCard(card);
                
                    // Check if the card belongs to the player and is a bird, fox, or fleeing bird
                    if (card != null) {
                        if (card.getType() == Card.Type.BIRD) {
                            int totalScore = playerScores.getOrDefault(playerNumber, 0);
                            totalScore += areaTotalValueMap.getOrDefault(area, 0);
                            playerScores.put(playerNumber, totalScore);
                            System.out.println("Single bird card in VBox for player " + playerNumber + ", total score: " + totalScore);
                            // Add bird card to the discard pile
                            StackPane birdCardPane = findCardPane(card);
                            if (birdCardPane != null) {
                                discardPile.getChildren().add(birdCardPane);
                            }
                            // Clear the cubes in the VBox
                            clearCubesInVBox(area);
                        } else if (card.getType() == Card.Type.FOX) {
                            System.out.println("Single fox card in VBox for player " + playerNumber + ", no score added.");
                            // Add fox card to the discard pile
                            StackPane foxCardPane = findCardPane(card);
                            if (foxCardPane != null) {
                                discardPile.getChildren().add(foxCardPane);
                            }
                        } else if (card.getType() == Card.Type.FLEEING_BIRD) {
                            int totalScore = playerScores.getOrDefault(playerNumber, 0);
                            totalScore += areaTotalValueMap.getOrDefault(area, 0);
                            playerScores.put(playerNumber, totalScore);
                            System.out.println("Single fleeing bird card in VBox for player " + playerNumber + ", total score: " + totalScore);
                            // Add fleeing bird card to the discard pile
                            StackPane fleeingBirdCardPane = findCardPane(card);
                            if (fleeingBirdCardPane != null) {
                                discardPile.getChildren().add(fleeingBirdCardPane);
                            }
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
                        StackPane cardPane = (StackPane) node;
                        Card card = (Card) cardPane.getUserData();
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
                                        StackPane birdCardPane = findCardPane(birdCard);
                                        if (birdCardPane != null) {
                                            discardPile.getChildren().add(birdCardPane);
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
                            StackPane cardPane = (StackPane) node;
                            Card card = (Card) cardPane.getUserData();
                            if (card != null && card.getType() == Card.Type.FOX) {
                                StackPane foxCardPane = findCardPane(card);
                                if (foxCardPane != null) {
                                    discardPile.getChildren().add(foxCardPane);
                                }
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
                            StackPane birdCardPane = findCardPane(birdCards.get(0));
                            if (birdCardPane != null) {
                                discardPile.getChildren().add(birdCardPane);
                            }
                            StackPane fleeingCardPane = findCardPane(fleeingBirdCard);
                            if (fleeingCardPane != null) {
                                discardPile.getChildren().add(fleeingCardPane);
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
                            StackPane birdCardPane = findCardPane(birdCards.get(0));
                            if (birdCardPane != null) {
                                discardPile.getChildren().add(birdCardPane);
                            }
                            StackPane fleeingCardPane = findCardPane(fleeingBirdCard);
                            if (fleeingCardPane != null) {
                                discardPile.getChildren().add(fleeingCardPane);
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
                        StackPane foxCardPane = findCardPane(foxCard);
                        if (foxCardPane != null) {
                            discardPile.getChildren().add(foxCardPane);
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
                        StackPane foxCardPane = findCardPane(foxCard);
                        if (foxCardPane != null) {
                            discardPile.getChildren().add(foxCardPane);
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
                        StackPane cardPane = (StackPane) node;
                        Card card = (Card) cardPane.getUserData();
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
                                        StackPane birdCardPane = findCardPane(birdCard);
                                        if (birdCardPane != null) {
                                            discardPile.getChildren().add(birdCardPane);
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
                            StackPane foxCardPane = findCardPane(foxCard);
                            if (foxCardPane != null) {
                                discardPile.getChildren().add(foxCardPane);
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
                                StackPane foxCardPane = findCardPane(foxCard);
                                if (foxCardPane != null) {
                                    discardPile.getChildren().add(foxCardPane);
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
                        StackPane foxCardPane = findCardPane(foxCards.get(0));
                        if (foxCardPane != null) {
                            discardPile.getChildren().add(foxCardPane);
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
                        StackPane foxCardPane = findCardPane(foxCards.get(0));
                        if (foxCardPane != null) {
                            discardPile.getChildren().add(foxCardPane);
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
                                    StackPane birdCardPane = findCardPane(birdCard);
                                    if (birdCardPane != null) {
                                        discardPile.getChildren().add(birdCardPane);
                                    }
                                }
                        
                                // Add fleeing bird card to the discard pile
                                StackPane fleeingBirdCardPane = findCardPane(fleeingBirdCard);
                                if (fleeingBirdCardPane != null) {
                                    discardPile.getChildren().add(fleeingBirdCardPane);
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
                                    StackPane birdCardPane = findCardPane(birdCard);
                                    if (birdCardPane != null) {
                                        discardPile.getChildren().add(birdCardPane);
                                    }
                                }
                        
                                // Add fleeing bird card to the discard pile
                                StackPane fleeingBirdCardPane = findCardPane(fleeingBirdCard);
                                if (fleeingBirdCardPane != null) {
                                    discardPile.getChildren().add(fleeingBirdCardPane);
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

    @FXML
    private void handleResolveFarmAction() {
        System.out.println("Resolving farm action...");

        // Update player scores
        updatePlayerScores();

        // Add chosen cards to the discard pile
        for (Map.Entry<Integer, Card> entry : playerCardMap.entrySet()) {
            Card card = entry.getValue();
            if (card != null) {
                StackPane cardPane = findCardPane(card);
                if (cardPane != null) {
                    // Remove the cardPane from its current parent
                    ((VBox) cardPane.getParent()).getChildren().remove(cardPane);
                    // Add the cardPane to the discard pile
                    discardPile.getChildren().add(cardPane);
                    System.out.println("Added card to discard pile: " + card);
                }
            }
        }

        // Clear the playerCardMap after resolving the farm
        playerCardMap.clear();
    }

    
    // Helper method to find the StackPane containing the card
    private StackPane findCardPane(Card card) {
        // Search in player piles
        for (VBox playerPile : new VBox[]{player1CardPile, player2CardPile, player3CardPile}) {
            for (javafx.scene.Node node : playerPile.getChildren()) {
                if (node instanceof StackPane) {
                    StackPane cardPane = (StackPane) node;
                    if (card.equals(cardPane.getUserData())) {
                        return cardPane;
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
                        if (cardNode instanceof StackPane) {
                            StackPane cardPane = (StackPane) cardNode;
                            if (card.equals(cardPane.getUserData())) {
                                return cardPane;
                            }
                        }
                    }
                }
            }
        }
    
        return null;
    }

    // private void addCardsToDiscardPile(VBox cardVBox, List<Card> cardsToDiscard) {
    //     for (Card card : cardsToDiscard) {
    //         for (javafx.scene.Node node : cardVBox.getChildren()) {
    //             StackPane cardPane = (StackPane) node;
    //             if (card.equals(cardPane.getUserData())) {
    //                 cardVBox.getChildren().remove(cardPane); // Remove the cardPane from the cardVBox
    //                 discardPile.getChildren().add(cardPane); // Add the cardPane to the discard pile
    //                 break;
    //             }
    //         }
    //     }
    // }
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
            List<StackPane> cards = new ArrayList<>();
            for (javafx.scene.Node node : discardPile.getChildren()) {
                if (node instanceof StackPane) {
                    cards.add((StackPane) node);
                }
            }
            Collections.shuffle(cards);
            cardPile.getChildren().addAll(cards);
            discardPile.getChildren().clear();
            System.out.println("Discard pile shuffled and added back to the card pile.");
        } else {
            System.out.println("Card pile is not empty.");
        }
    }

    private String determineWinner() {
        // Implement the logic to determine the winner based on player scores
        int player1Score = getPlayerScore(1);
        int player2Score = getPlayerScore(2);
        int player3Score = getPlayerScore(3);
    
        if (player1Score > player2Score && player1Score > player3Score) {
            return "Player 1";
        } else if (player2Score > player1Score && player2Score > player3Score) {
            return "Player 2";
        } else if (player3Score > player1Score && player3Score > player2Score) {
            return "Player 3";
        } else {
            return "It's a tie!";
        }
    }

    private int getPlayerScore(int playerNumber) {
        // Implement this method to get the score of the specified player
        switch (playerNumber) {
            case 1:
                return playerScores.getOrDefault(1, 0);
            case 2:
                return playerScores.getOrDefault(2, 0);
            case 3:
                return playerScores.getOrDefault(3, 0);
            default:
                return 0;
        }
    }

    private void declareWinner(String winner) {
        // Debug: Print the winner to the console
        System.out.println("Winner: " + winner);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("winner.fxml"));
            Parent root = loader.load();
    
            WinnerController winnerController = loader.getController();
            winnerController.setWinner(winner);
    
            Stage stage = new Stage();
            stage.setTitle("Game Over");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void endGame() {
        System.out.println("Game has ended.");
        // Additional logic to end the game can be added here
    }
}