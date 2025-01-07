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

    private Random random = new Random();

    private Game game;

    private Map<Color, StackPane> colorToAreaMap;
    private Map<Color, Integer> colorToValueMap;

    private boolean player1CardSelected = false;
    private boolean player2CardSelected = false;
    private boolean player3CardSelected = false;

    private Map<Integer, Card> playerCardMap = new HashMap<>();

    private int player1Score = 0;
    private int player2Score = 0;
    private int player3Score = 0;

    private int currentPlayerTurn = 1;

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

        resolveFarmButton.setOnAction(event -> {
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
        updatePlayerScores(); // Update the player's score after adding a card
    }

    @FXML
    private void handleAddCardsToPlayer2Action() {
        addCardsToPlayerPile(1, player2CardPile, 2);
        player2CardSelected = false; // Reset the flag after adding a card
        updateAddCardsButtons(); // Update the buttons after adding a card
        updatePlayerScores(); // Update the player's score after adding a card
    }

    @FXML
    private void handleAddCardsToPlayer3Action() {
        addCardsToPlayerPile(1, player3CardPile, 3);
        player3CardSelected = false; // Reset the flag after adding a card
        updateAddCardsButtons(); // Update the buttons after adding a card
        updatePlayerScores(); // Update the player's score after adding a card
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
            if (cubeVBox.getChildren().size() < 6 && !cubeFlowPane.getChildren().isEmpty()) {
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
    
        // Debug: Print the calculated scores
        System.out.println("Player 1 Score: " + player1Score);
        System.out.println("Player 2 Score: " + player2Score);
        System.out.println("Player 3 Score: " + player3Score);
    
        // Update the UI or internal state with the new scores
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
    
                // If there is only one card in the VBox, add the total value of the area to the player's score
                if (cardVBox.getChildren().size() == 1) {
                    StackPane cardPane = (StackPane) cardVBox.getChildren().get(0);
                    Card card = (Card) cardPane.getUserData();
                    int playerNumber = getPlayerNumberByCard(card);
    
                    // Check if the card belongs to the player and is a bird
                    if (card != null && card.getType() == Card.Type.BIRD) {
                        int totalScore = playerScores.getOrDefault(playerNumber, 0);
                        totalScore += areaTotalValueMap.getOrDefault(area, 0);
                        playerScores.put(playerNumber, totalScore);
                        System.out.println("Single bird card in VBox for player " + playerNumber + ", total score: " + totalScore);
                    }
                }
    
                // If there are two or three cards in the VBox and all are birds, roll a dice and add the value of the player's card
                if (cardVBox.getChildren().size() == 2 || cardVBox.getChildren().size() == 3) {
                    List<Card> birdCards = new ArrayList<>();
                    for (javafx.scene.Node node : cardVBox.getChildren()) {
                        StackPane cardPane = (StackPane) node;
                        Card card = (Card) cardPane.getUserData();
                        if (card != null && card.getType() == Card.Type.BIRD) {
                            birdCards.add(card);
                        }
                    }
    
                    if (birdCards.size() == cardVBox.getChildren().size()) {
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
                                }
                            }
                        } while (tie);
                    }
                }
            }
        }
    
        return playerScores;
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

    // private int calculatePlayerScore(VBox playerPile) {
    //     int totalScore = 0;
    //     List<Card> cards = new ArrayList<>();
    //     for (javafx.scene.Node node : playerPile.getChildren()) {
    //         if (node instanceof StackPane) {
    //             StackPane cardPane = (StackPane) node;
    //             Card card = (Card) cardPane.getUserData();
    //             if (card != null) {
    //                 cards.add(card);
    //             }
    //         }
    //     }

    //     // Debug: Print the size of the cards list
    //     System.out.println("Number of cards in player pile: " + cards.size());
    
    //     if (cards.isEmpty()) {
    //         return totalScore; // No cards in the player pile, return 0 score
    //     }
    
    //     StackPane area = colorToAreaMap.get(Color.valueOf(cards.get(0).getColor().toUpperCase()));

    //     // Debug: Print the area and its total value
    //     System.out.println("Area: " + area + ", Total value: " + areaTotalValueMap.getOrDefault(area, 0));


    //     if (cards.size() == 1 && cards.get(0).isBird()) {
    //         // If the card is a bird and it is alone, the player's score will be the total value of that area
    //         totalScore += areaTotalValueMap.getOrDefault(area, 0);
    //     } else if (cards.size() == 2) {
    //         // If there are 2 cards, add the value of each card with the value of the dice
    //         int diceValue = rollDice();
    //         int card1ValueWithDice = cards.get(0).getValue() + diceValue;
    //         int card2ValueWithDice = cards.get(1).getValue() + diceValue;
    //         if (card1ValueWithDice > card2ValueWithDice) {
    //             totalScore += areaTotalValueMap.getOrDefault(area, 0);
    //         } else if (card2ValueWithDice > card1ValueWithDice) {
    //             totalScore += areaTotalValueMap.getOrDefault(area, 0);
    //         }
    //     }
    //     System.out.println("Calculated score for player pile: " + totalScore);
    //     return totalScore;
    // }

    private int rollDice() {
        return random.nextInt(6) + 1; // Roll a dice (1-6)
    }
    
    @FXML
    private void handleResolveFarmAction() {
        System.out.println("Resolving farm action...");
    
        // You can add additional logic here as needed
        // For example, you might want to call updatePlayerScores() or other methods
        updatePlayerScores();
    }
    
    
}