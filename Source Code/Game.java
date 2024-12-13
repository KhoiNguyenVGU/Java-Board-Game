import java.util.*;

/**
 * The Game class represents the main game logic for the board game.
 * It manages the farms, players, deck, and game rounds.
 */
public class Game {
    private List<Farm> farms = new ArrayList<>();
    private List<Player> players = new ArrayList<>();
    private List<Card> deck = new ArrayList<>();
    private List<Card> discardPile = new ArrayList<>();
    private Die die = new Die();

    int greenCornCount = 26;
    int blueCornCount = 26;
    int yellowCornCount = 26;

    /**
     * Constructs a new Game with the specified number of players.
     *
     * @param playerCount the number of players
     */
    public Game(int playerCount) {
        initializeFarms();
        initializeDeck();
        initializePlayers(playerCount);
        addInitialCorn();
    }

    /**
     * Initializes the farms with predefined colors.
     */
    private void initializeFarms() {
        for (String color : Arrays.asList("Red", "Blue", "Green", "Yellow", "Purple", "Orange")) {
            farms.add(new Farm(color));
        }
    }

    /**
     * Initializes the deck with cards of different types and colors.
     */
    private void initializeDeck() {
        String[] colors = {"Red", "Blue", "Green", "Yellow", "Purple", "Orange"};
        for (String color : colors) {
            // Add 2 birds of type 3 and 4
            for (int i = 0; i < 2; i++) {
                deck.add(new Card(Card.Type.BIRD, 3, color));
                deck.add(new Card(Card.Type.BIRD, 4, color));
            }
            // Add birds of type 5 and 6
            for (int i = 5; i <= 6; i++) {
                deck.add(new Card(Card.Type.BIRD, i, color));
            }
            // Add foxes of type 4 to 6
            for (int i = 4; i <= 6; i++) {
                deck.add(new Card(Card.Type.FOX, i, color));
            }
            // Add fleeing bird
            deck.add(new Card(Card.Type.FLEEING_BIRD, -2, color));
        }
        Collections.shuffle(deck);
    }

    /**
     * Initializes the players with the specified number of players.
     *
     * @param playerCount the number of players
     */
    private void initializePlayers(int playerCount) {
        for (int i = 1; i <= playerCount; i++) {
            Player player = new Player("Player " + i);
            for (int j = 0; j < 5; j++) player.addCard(deck.remove(0));
            players.add(player);
        }
    }

    /**
     * Adds initial corn cubes to the farms.
     */
    private void addInitialCorn() {
        Random random = new Random();
        CornCube.CornType[] cornTypes = CornCube.CornType.values();
        for (Farm farm : farms) {
            CornCube.CornType chosenType = cornTypes[random.nextInt(cornTypes.length)];
            CornCube cube = new CornCube(chosenType);
            farm.addCorn(cube);
            
            switch (chosenType) {
                case GREEN:
                    greenCornCount--;
                    break;
                case BLUE:
                    blueCornCount--;
                    break;
                case YELLOW:
                    yellowCornCount--;
                    break;
            }
        }
    }

    /**
     * Starts the game and manages the game rounds.
     */
    public void startGame() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n*** New Round ***\n");
            System.out.println("--- Farms ---");
            printFarms();
            System.out.print("\n");

            // Step 1: Players pick cards
            System.out.println("--- Players Pick Cards ---");
            Map<Player, Card> chosenCards = new HashMap<>();
            for (Player player : players) {
                Card card = pickCardForPlayer(player, scanner);
                chosenCards.put(player, card);
            }

            // Step 2: Resolve the cards
            System.out.println("\n--- Resolving Cards ---");
            resolveCards(chosenCards);

            // Display score piles for each player
            System.out.println("--- Player Score Pile ---");
            for (Player player : players) {
                System.out.println(player.getName() + "'s score pile:");
                player.printScorePile();
            }

            // Display the number of corn of each type
            System.out.println("--- Corn Count ---");
            System.out.println("Green Corn Count: " + greenCornCount);
            System.out.println("Blue Corn Count: " + blueCornCount);
            System.out.println("Yellow Corn Count: " + yellowCornCount);

            // Display the number of cards left in the deck
            System.out.println("\n--- Deck Count ---");
            System.out.println("Deck Count: " + deck.size());
            System.out.println("Discard Pile: " + discardPile.size());
            // for (Card card : discardPile) {
            //     System.out.println(card);
            // }

            // Step 3: Check if the game should end
            if (greenCornCount + blueCornCount + yellowCornCount >= 6) {
                addCornToFarms();
            } else {
                System.out.println("\n--- Game Over ---");
                declareWinner();
                break;
            }

            // Step 4: Deal new cards to players
            if (players.get(0).getHand().isEmpty()) {
                dealNewCards();
            }
        }
    }

    /**
     * Prints the current state of the farms.
     */
    private void printFarms() {
        for (Farm farm : farms) System.out.println(farm);
    }

    /**
     * Allows a player to pick a card from their hand.
     *
     * @param player the player picking a card
     * @param scanner the scanner for input
     * @return the chosen card
     */
    private Card pickCardForPlayer(Player player, Scanner scanner) {
        System.out.println("\n" + player.getName() + ", choose a card to play:");
        List<Card> hand = player.getHand();

        for (int i = 0; i < hand.size(); i++) {
            System.out.println(i + 1 + ": " + hand.get(i));
        }

        int choice = scanner.nextInt() - 1;
        while (choice < 0 || choice >= hand.size()) {
            System.out.println("Invalid choice. Try again.");
            choice = scanner.nextInt() - 1;
        }

        return hand.remove(choice);
    }

    /**
     * Resolves the cards chosen by the players.
     *
     * @param chosenCards the map of players and their chosen cards
     */
    private void resolveCards(Map<Player, Card> chosenCards) {
        // Group the cards by their color
        Map<String, List<Map.Entry<Player, Card>>> groupedCards = new HashMap<>();
    
        for (Map.Entry<Player, Card> entry : chosenCards.entrySet()) {
            String color = entry.getValue().getColor();
            groupedCards.putIfAbsent(color, new ArrayList<>());
            groupedCards.get(color).add(entry);
        }
    
        // Resolve each farm based on the grouped cards
        for (String color : groupedCards.keySet()) {
            Farm farm = farms.stream()
                    .filter(f -> f.getColor().equals(color))
                    .findFirst()
                    .orElse(null);
    
            if (farm != null) {
                resolveFarm(groupedCards.get(color), farm);
            }
        }
    
        // Display player scores after resolving all actions
        System.out.println("\n--- Player Scores After This Turn ---");
        for (Player player : players) {
            System.out.println(player.getName() + " (Score: " + player.calculateScore() + ")\n");
        }
    }

    /**
     * Resolves the actions for a specific farm based on the cards played.
     *
     * @param cards the list of player-card entries
     * @param farm the farm to resolve
     */
    private void resolveFarm(List<Map.Entry<Player, Card>> cards, Farm farm) {
        List<Map.Entry<Player, Card>> birds = new ArrayList<>();
        List<Map.Entry<Player, Card>> foxes = new ArrayList<>();
        List<Map.Entry<Player, Card>> fleeingBirds = new ArrayList<>();
        List<Map.Entry<Player, Card>> otherBirdsAndFoxes = new ArrayList<>();
    
        for (Map.Entry<Player, Card> entry : cards) {
            switch (entry.getValue().getType()) {
                case BIRD -> birds.add(entry);
                case FOX -> foxes.add(entry);
                case FLEEING_BIRD -> fleeingBirds.add(entry);
            }
        }
    
        // Combine birds and foxes into otherBirdsAndFoxes
        otherBirdsAndFoxes.addAll(birds);
        otherBirdsAndFoxes.addAll(foxes);
    
        // Resolve fleeing birds
        if (!fleeingBirds.isEmpty() && foxes.isEmpty()) {
            resolveFleeingBirdDiscard(fleeingBirds, farm, otherBirdsAndFoxes);
        }
    
        // Resolve foxes and birds
        if (!foxes.isEmpty() && (!birds.isEmpty() || !fleeingBirds.isEmpty())) {
            resolveFleeingBird(fleeingBirds, farm, otherBirdsAndFoxes);
            resolveFoxesAndBirds(foxes, birds, fleeingBirds, farm);
        } else if (!birds.isEmpty()) {
            resolveBirds(birds, farm);
        } else if (!foxes.isEmpty()) {
            resolveFoxes(foxes);
        }
    }

    /**
     * Resolves the actions for birds on a farm.
     *
     * @param birds the list of player-card entries for birds
     * @param farm the farm to resolve
     */
    private void resolveBirds(List<Map.Entry<Player, Card>> birds, Farm farm) {
        // Sort birds list to ensure Player 1 rolls first
        birds.sort(Comparator.comparing(entry -> entry.getKey().getName()));

        if (birds.size() == 1) {
            Map.Entry<Player, Card> bird = birds.get(0);
            Player player = bird.getKey();
            for (CornCube corn : farm.getCornCubes()) {
                player.addToScore(corn);
            }
            farm.clearCorn();
            System.out.println(player.getName() + "'s bird eats all the corn!");
        } else {
            // Birds fight for the corn
            Map.Entry<Player, Card> winner = birds.get(0);
            int maxRoll = -1;
            for (Map.Entry<Player, Card> bird : birds) {
                int diceRoll = die.roll();
                int roll = diceRoll + bird.getValue().getValue();
                System.out.println(bird.getKey().getName() + " rolled a dice value of " + diceRoll);
                System.out.println(bird.getKey().getName() + " has a total value of " + roll);
                if (roll > maxRoll) {
                    maxRoll = roll;
                    winner = bird;
                }
            }
            for (CornCube corn : farm.getCornCubes()) {
                winner.getKey().addToScore(corn);
            }
            farm.clearCorn();
            System.out.println(winner.getKey().getName() + " wins the fight and takes the corn!");
        }

        // Add played bird cards to discard pile
        for (Map.Entry<Player, Card> bird : birds) {
            discardPile.add(bird.getValue());
        }
    }
    
    /**
     * Resolves the actions for foxes and birds on a farm.
     *
     * @param foxes the list of player-card entries for foxes
     * @param birds the list of player-card entries for birds
     * @param fleeingBirds the list of player-card entries for fleeing birds
     * @param farm the farm to resolve
     */
    private void resolveFoxesAndBirds(List<Map.Entry<Player, Card>> foxes, List<Map.Entry<Player, Card>> birds, List<Map.Entry<Player, Card>> fleeingBirds, Farm farm) {
        // Sort foxes list to ensure Player 1 rolls first
        foxes.sort(Comparator.comparing(entry -> entry.getKey().getName()));

        Map.Entry<Player, Card> winningFox = foxes.get(0);
        int maxRoll = -1;

        if (foxes.size() > 1) {
            for (Map.Entry<Player, Card> fox : foxes) {
                int diceRoll = die.roll();
                int roll = diceRoll + fox.getValue().getValue();
                System.out.println(fox.getKey().getName() + " rolled a dice value of " + diceRoll);
                System.out.println(fox.getKey().getName() + " has a total value of " + roll);
                if (roll > maxRoll) {
                    maxRoll = roll;
                    winningFox = fox;
                }
            }
        }

        List<Map.Entry<Player, Card>> allBirds = new ArrayList<>(birds);
        allBirds.addAll(fleeingBirds);

        // List to keep track of eaten birds
        List<Map.Entry<Player, Card>> eatenBirds = new ArrayList<>();

        for (Map.Entry<Player, Card> bird : allBirds) {
            winningFox.getKey().addToScore(bird.getValue());
            eatenBirds.add(bird); // Add bird to eaten birds list
        }

        System.out.println(winningFox.getKey().getName() + "'s fox eats all the birds!");

        // Add played fox cards to discard pile
        for (Map.Entry<Player, Card> fox : foxes) {
            discardPile.add(fox.getValue());
        }
        // Add played bird cards to discard pile (excluding eaten birds)
        for (Map.Entry<Player, Card> bird : allBirds) {
            if (!eatenBirds.contains(bird)) {
                discardPile.add(bird.getValue());
            }
        }
    }

    /**
     * Resolves the actions for foxes on a farm.
     *
     * @param foxes the list of player-card entries for foxes
     */
    private void resolveFoxes(List<Map.Entry<Player, Card>> foxes) {
        foxes.sort(Comparator.comparing(entry -> entry.getKey().getName()));
        
        // Add played fox cards to discard pile
        for (Map.Entry<Player, Card> fox : foxes) {
            discardPile.add(fox.getValue());
            System.out.println(fox.getKey().getName() + "'s fox eats nothing!");
        }
    }
    
    /**
     * Resolves the actions for fleeing birds on a farm.
     *
     * @param fleeingBirds the list of player-card entries for fleeing birds
     * @param farm the farm to resolve
     * @param otherBirdsAndFoxes the list of other birds and foxes on the farm
     */
    private void resolveFleeingBird(List<Map.Entry<Player, Card>> fleeingBirds, Farm farm, List<Map.Entry<Player, Card>> otherBirdsAndFoxes) {
        if (fleeingBirds.isEmpty()) {
            return;
        }
    
        Map.Entry<Player, Card> fleeingBird = fleeingBirds.get(0);
        Player player = fleeingBird.getKey();
    
        if (otherBirdsAndFoxes.isEmpty()) {
            // Fleeing bird is alone, it eats all the corn
            for (CornCube corn : farm.getCornCubes()) {
                player.addToScore(corn);
            }
            farm.clearCorn();
            System.out.println(player.getName() + "'s bird eats all the corn!");
        } else {
            // Fleeing bird takes one green corn (if any) before everything else resolves
            boolean tookGreenCorn = false;
            Iterator<CornCube> iterator = farm.getCornCubes().iterator();
            while (iterator.hasNext()) {
                CornCube corn = iterator.next();
                if (corn.getPoints() == 1) {
                    player.addToScore(corn);
                    iterator.remove();
                    tookGreenCorn = true;
                    break;
                }
            }
            if (tookGreenCorn) {
                System.out.println(player.getName() + "'s bird flees with a green corn.");
            } else {
                System.out.println(player.getName() + "'s bird flees without any green corn.");
            }
        }
    }

    /**
     * Resolves the actions for fleeing birds on a farm and discards them.
     *
     * @param fleeingBirds the list of player-card entries for fleeing birds
     * @param farm the farm to resolve
     * @param otherBirdsAndFoxes the list of other birds and foxes on the farm
     */
    private void resolveFleeingBirdDiscard(List<Map.Entry<Player, Card>> fleeingBirds, Farm farm, List<Map.Entry<Player, Card>> otherBirdsAndFoxes) {
        if (fleeingBirds.isEmpty()) {
            return;
        }
    
        Map.Entry<Player, Card> fleeingBird = fleeingBirds.get(0);
        Player player = fleeingBird.getKey();
    
        if (otherBirdsAndFoxes.isEmpty()) {
            // Fleeing bird is alone, it eats all the corn
            for (CornCube corn : farm.getCornCubes()) {
                player.addToScore(corn);
            }
            farm.clearCorn();
            System.out.println(player.getName() + "'s bird eats all the corn!");
        } else {
            // Fleeing bird takes one green corn (if any) before everything else resolves
            boolean tookGreenCorn = false;
            Iterator<CornCube> iterator = farm.getCornCubes().iterator();
            while (iterator.hasNext()) {
                CornCube corn = iterator.next();
                if (corn.getPoints() == 1) {
                    player.addToScore(corn);
                    iterator.remove();
                    tookGreenCorn = true;
                    break;
                }
            }
            if (tookGreenCorn) {
                System.out.println(player.getName() + "'s bird flees with a green corn.");
            } else {
                System.out.println(player.getName() + "'s bird flees without any green corn.");
            }
        }
    
        // Add played fleeing bird cards to discard pile
        for (Map.Entry<Player, Card> fleeingBirdEntry : fleeingBirds) {
            discardPile.add(fleeingBirdEntry.getValue());
        }
    }

    /**
     * Adds corn cubes to the farms.
     */
    private void addCornToFarms() {
        Random random = new Random();
        List<CornCube.CornType> availableCornTypes = new ArrayList<>();
    
        for (Farm farm : farms) {
            availableCornTypes.clear();
            if (greenCornCount > 0) {
                availableCornTypes.add(CornCube.CornType.GREEN);
            }
            if (blueCornCount > 0) {
                availableCornTypes.add(CornCube.CornType.BLUE);
            }
            if (yellowCornCount > 0) {
                availableCornTypes.add(CornCube.CornType.YELLOW);
            }
    
            if (availableCornTypes.isEmpty()) {
                break; // No corn left to add
            }
    
            CornCube.CornType chosenType = availableCornTypes.get(random.nextInt(availableCornTypes.size()));
            CornCube cube = new CornCube(chosenType);
            farm.addCorn(cube);
    
            switch (chosenType) {
                case GREEN:
                    greenCornCount--;
                    break;
                case BLUE:
                    blueCornCount--;
                    break;
                case YELLOW:
                    yellowCornCount--;
                    break;
            }
        }
    }

    /**
     * Deals new cards to the players.
     */
    private void dealNewCards() {
        if (deck.size() < 5 * players.size()) {
            Collections.shuffle(discardPile);
            deck.addAll(discardPile);
            discardPile.clear();
        }
    
        for (Player player : players) {
            for (int i = 0; i < 5; i++) {
                    player.addCard(deck.remove(0));
            }
        }
    }

    /**
     * Declares the winner of the game.
     */
    private void declareWinner() {
        Player winner = players.stream().max(Comparator.comparingInt(Player::calculateScore)).orElse(null);
        System.out.println("The winner is " + winner.getName() + " with " + winner.calculateScore() + " points!");
    }

    /**
     * The main method to start the game.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n*** Welcome to Hick Hack! ***");
        System.out.print("Please enter the number of players: ");
        int playerCount = scanner.nextInt();

        Game game = new Game(playerCount);
        game.startGame();
        scanner.close();
    }
}