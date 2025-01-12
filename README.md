# Getting Started

Welcome to our project! 🎉
We’re thrilled to have you here and hope this guide helps you get everything up and running smoothly. Whether you're a contributor or a user, this project is designed to make your experience enjoyable and productive.


# Download Instructions: 

- Step 1: Create a folder and navigate to clone the game

- Step 2: Make sure to have `Git` downloaded to run Git Commands.

- Step 3: Open a terminal (or Git Bash) and run the following command to clone the repository
`git clone https://github.com/KhoiNguyenVGU/Java-Board-Game.git`

- Step 4: Click the `.exe` file to run the game.

# Run Requirements for IDE.

Before implementation of the game, there are multiple requirements to run the game:

- `Install latest Java Version`: Install Java SDK 23.0.1 or later to run the game
- `Add configuration to project`: To run the JavaFX application in VS Code, add the following line to your launch.json file under the vmArgs field ` "vmArgs": "--enable-preview --module-path \"path-to-your-jdk-lib" --add-modules javafx.controls,javafx.fxml" `
- `Install Java Extension`: Install only this Java Extension to run and compile Java files
![Alt Text](src/hellofx/resources/readme/extension.png)

# Code Components

The `resources` folder contains images of cards, farms, gifs, and scenes in fxml file, and CSS file for animation of the game.

There are class files in the codes, which represent the class of objects of the game.

`Game.java` can be used to play the game in `Command Prompt`. 

`BeginController.java`, `GameController.java`, and `WinnerController.java` is used for managing and controlling the scenes of the game. 

To run the game in Programming IDE(Visual Studio Code), run the `main.java` to run the game.


# Game Rules:

- Objective: The goal of the game is to [find the player with the highest score].

- Players: The game is played between [3] players.

- Game Components:

    - There are 6 areas(farms), each representing with a color.

    - There are 10 cards of each colour; 7 of which show various sized birds of the same breed and 3 of which show foxes who will each only eat one breed of poultry - obviously raised to be picky eaters!.  Each poultry card has a number either between 3 and 6 or -2 (the “fleet fowl”), whilst the foxes have a number between 4 and 6. The numbers reflect the relative size / strength of the bird or fox. These numbers become very relevant when more than one bird or fox turns up to feed at the same tile.

    - There are 78 mall wooden cubes representing the corn that the birds will eat throughout the game, different colours have different nutritional values – Green - 1, Blue - 2 Yellow - 3.

    - 1 Die: Players will roll dices to find the winner of a round.

- Game Round:

    - Players who place one unique card on a farm eats all the corns on the farm, except for the fox, which will only eat the bird card.

    - Where more than one player has played a card of the same colour depicting a bird, the players can negotiate the division of feed between them, or in our experience more typically fight over who gets the feed. The poultry fight by rolling a die and adding the die number to the number on each bird’s card - the highest combination wins. However if your bird had a -2 then you get a piece of Green feed before the other birds fight over the remaining feed.

    - Once the birds have eaten, their cards are put on the discard pile.

    - Players who placed cards with foxes eat any poultry of the same colour as the fox card (i.e. the collect those poultry cards), if any turned up to feed or rue the fact that they went after the wrong type of poultry.

    - If more than one fox turns up then their fight (they never negotiate) is resolved in the same way as described for the poultry with the pultry card going to the winner.

    - Any player who sent a ‘fleet fowl’ i.e. a poultry card with a -2 on the card gets one Green feed before they are then gobbled up by the fox. The fox gets the poultry card with the -2 towards their overall score at the end of the game.

    - When there are no cards in the Card Deck, reshuffle the Discard Deck to keep playing. 

    - The game will be finalized when there are no corns left in the Corn Box. The player with highest point wins the game.

For more detailed game rules and strategies, check out the full [Game Rule Documentation](https://kevinandgames.blogspot.com/2011/12/review-hick-hack-in-gackelwack.html).


# GUI Description

The Graphical User Interface (GUI) of the game provides a user-friendly environment for players to interact with the game. Below is an overview of the main sections and components of the GUI:

## Begin Window:

- `Title bar`: Displays the title of the game

- `Menu Interface`:Provides access to various options like starting a new game, viewing the rules, or exiting the game.

    - `Start Game Button`: Starts a new session of the game.

    - `Show Rules Button`: Opens a new page show game rules.

    - `Exit Button`: Closes the application.

## Game Window:

Here is the User Interface(UI) of the Game:

- `Back Button`: Return to the Begin Window

- `6 Farms`: 6 Areas with 6 colors: Yellow, Green, Red, Blue, Purple, Black

- `3 Player Card Decks`: Shows the player cards to play. The card deck will be available one after another to avoid the players' cards being shown.

- `1 Card Deck`: Contains the cards of the game.

- `1 Discard Deck`: Contains the cards played that will be reshuffled again.

- `1 Corn Box`: Contains the corns to be added to the farms.

- `Resolve Farm Button`: To calculate the player score after each round. After there are no corns left, press the button to finalize the winner

- `Reshuffle Button`: To reshuffle the Discard Deck after no cards are left in Card Deck.

- `Place Cubes Button`: To add one corn to each farm before a round starts.

## Winner Window:

Here will show the winner of the game and 2 players with their scores and animation for the winner.


