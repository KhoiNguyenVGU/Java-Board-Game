import java.util.*;

public class Game {
    private List<Farm> farms = new ArrayList<>();
    private List<Player> players = new ArrayList<>();
    private List<Card> deck = new ArrayList<>();
    private Die die = new Die();

    public Game(int playerCount) {
        initializeFarms();
        initializeDeck();
        initializePlayers(playerCount);
        addInitialCorn();
    }

    private void initializeFarms() {
        for (String color : Arrays.asList("Red", "Blue", "Green", "Yellow", "Purple", "Orange")) {
            farms.add(new Farm(color));
        }
    }

    private void initializeDeck() {
        String[] colors = {"Red", "Blue", "Green", "Yellow", "Purple", "Orange"};
        for (String color : colors) {
            for (int i = 3; i <= 6; i++) deck.add(new Card(Card.Type.BIRD, i, color));
            for (int i = 4; i <= 6; i++) deck.add(new Card(Card.Type.FOX, i, color));
            deck.add(new Card(Card.Type.FLEEING_BIRD, -2, color));
        }
        Collections.shuffle(deck);
    }

    private void initializePlayers(int playerCount) {
        for (int i = 1; i <= playerCount; i++) {
            Player player = new Player("Player " + i);
            for (int j = 0; j < 5; j++) player.addCard(deck.remove(0));
            players.add(player);
        }
    }

    private void addInitialCorn() {
        Random random = new Random();
        CornCube.CornType[] cornTypes = CornCube.CornType.values();
        for (Farm farm : farms) {
            CornCube cube = new CornCube(cornTypes[random.nextInt(cornTypes.length)]);
            farm.addCorn(cube);
        }
    }

    public void startGame() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- New Round ---");
            printGameState();

            // Step 1: Players pick cards
            Map<Player, Card> chosenCards = new HashMap<>();
            for (Player player : players) {
                Card card = pickCardForPlayer(player, scanner);
                chosenCards.put(player, card);
            }

            // Step 2: Resolve the cards
            resolveCards(chosenCards);

            // Display score piles for each player
            for (Player player : players) {
                System.out.println(player.getName() + "'s score pile:");
                player.printScorePile();
            }

            // Step 3: Add new corn and deal new cards
            addCornToFarms();
            dealNewCards();

            // Check if the game should end
            if (deck.isEmpty() || !canAddCornToFarms()) {
                System.out.println("\n--- Game Over ---");
                declareWinner();
                break;
            }
        }
    }

    private void printGameState() {
        System.out.println("Farms:");
        for (Farm farm : farms) System.out.println(farm);
        System.out.println("Players:");
        for (Player player : players) System.out.println(player);
    }

    private Card pickCardForPlayer(Player player, Scanner scanner) {
        System.out.println(player.getName() + ", choose a card to play:");
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
            System.out.println(player.getName() + " (Score: " + player.calculateScore() + ")");
        }
        System.out.println();
    }
    

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
        if (!fleeingBirds.isEmpty()) {
            resolveFleeingBird(fleeingBirds, farm, otherBirdsAndFoxes);
        }
    
        // Resolve foxes and birds
        if (!foxes.isEmpty() && (!birds.isEmpty() || !fleeingBirds.isEmpty())) {
            resolveFoxesAndBirds(foxes, birds, fleeingBirds, farm);
        } else if (!birds.isEmpty()) {
            resolveBirds(birds, farm);
        }
    }

    private void resolveBirds(List<Map.Entry<Player, Card>> birds, Farm farm) {
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
                int roll = die.roll() + bird.getValue().getValue();
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
    }

    private void resolveFoxesAndBirds(List<Map.Entry<Player, Card>> foxes, List<Map.Entry<Player, Card>> birds, List<Map.Entry<Player, Card>> fleeingBirds, Farm farm) {
        Map.Entry<Player, Card> winningFox = foxes.get(0);
        int maxRoll = -1;
    
        for (Map.Entry<Player, Card> fox : foxes) {
            int roll = die.roll() + fox.getValue().getValue();
            if (roll > maxRoll) {
                maxRoll = roll;
                winningFox = fox;
            }
        }
    
        List<Map.Entry<Player, Card>> allBirds = new ArrayList<>(birds);
        allBirds.addAll(fleeingBirds);
    
        for (Map.Entry<Player, Card> bird : allBirds) {
            int birdValue = bird.getValue().getValue();
            if (birdValue > 0) {
                winningFox.getKey().addToScore(bird.getValue());
            } else {
                winningFox.getKey().addToScore(-2);
            }
        }
        // farm.clearCorn();
        System.out.println(winningFox.getKey().getName() + "'s fox eats all the birds!");
    }

    // private void resolveFleeingBird(List<Map.Entry<Player, Card>> fleeingBirds, Farm farm) {
    //     for (Map.Entry<Player, Card> fleeingBird : fleeingBirds) {
    //         if (farmingCornContainsGreen(farm)) {
    //             fleeingBird.getKey().addToScore(new CornCube(CornCube.CornType.GREEN));
    //             System.out.println(fleeingBird.getKey().getName() + "'s bird flees with a green corn.");
    //         } else {
    //             System.out.println(fleeingBird.getKey().getName() + "'s bird flees without any corn.");
    //         }
    //     }
    // }

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

    private void addCornToFarms() {
        Random random = new Random();
        CornCube.CornType[] cornTypes = CornCube.CornType.values();
        for (Farm farm : farms) {
            CornCube cube = new CornCube(cornTypes[random.nextInt(cornTypes.length)]);
            farm.addCorn(cube);
        }
    }

    private void dealNewCards() {
        for (Player player : players) {
            if (!deck.isEmpty()) {
                player.addCard(deck.remove(0));
            }
        }
    }

    private boolean canAddCornToFarms() {
        return deck.size() >= farms.size();
    }

    private void declareWinner() {
        Player winner = players.stream().max(Comparator.comparingInt(Player::calculateScore)).orElse(null);
        System.out.println("The winner is " + winner.getName() + " with " + winner.calculateScore() + " points!");
    }

    public static void main(String[] args) {
        Game game = new Game(3); // Example with 3 players
        game.startGame();
    }
}