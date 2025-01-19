/**
 * Main.java
 * 
 * Main application class that handles the JavaFX application lifecycle and screen navigation.
 * This class serves as the primary entry point and screen manager for the Hick Hack game.
 */

package hellofx;

// JavaFX libraries import
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.List;

public class Main extends Application {

    // Primary stage reference maintained throughout the application.
    private Stage primaryStage;

    /**
     * Initializes the application and shows the begin page.
     * @param primaryStage The main application window
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showBeginPage();
    }

    /**
     * Loads and displays the initial application screen.
     * Sets up the window.
     */
    public void showBeginPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hellofx/resources/begin.fxml"));
            Parent root = loader.load();
            BeginController controller = loader.getController();
            controller.setMainApp(this);

            InputStream logoStream = getClass().getResourceAsStream("/hellofx/resources/logo/logo.jpg");
            if (logoStream == null) {
                System.out.println("Logo not found!");
            } else {
                primaryStage.getIcons().add(new Image(logoStream));
            }

            Scene scene = new Scene(root);
            primaryStage.setTitle("Hick Hack");
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true); // Maximize the stage instead of setting it to full screen
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads and displays the main game screen for multiplayer mode.
     * @param game The game instance containing game state
     */
    public void showGamePage(Game game) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hellofx/resources/game.fxml"));
            Parent root = loader.load();
            GameController controller = loader.getController();
            controller.setMainApp(this); // Ensure the mainApp reference is set
            controller.setGame(game);

            InputStream logoStream = getClass().getResourceAsStream("/hellofx/resources/logo/logo.jpg");
            if (logoStream == null) {
                System.out.println("Logo not found!");
            } else {
                primaryStage.getIcons().add(new Image(logoStream));
            }

            Scene scene = new Scene(root);
            primaryStage.setTitle("Hick Hack");
            primaryStage.setScene(scene);
            primaryStage.setFullScreen(true); // Set the stage to full screen
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads and displays the single player game screen.
     * @param game The game instance containing game state
     */
    public void showSinglePage(Game game) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hellofx/resources/single_player_game.fxml"));
            Parent root = loader.load();
            SingleGameController controller = loader.getController();
            controller.setMainApp(this);
            controller.setGame(game);

            InputStream logoStream = getClass().getResourceAsStream("/hellofx/resources/logo/logo.jpg");
            if (logoStream == null) {
                System.out.println("Logo not found!");
            } else {
                primaryStage.getIcons().add(new Image(logoStream));
            }

            Scene scene = new Scene(root);
            primaryStage.setTitle("Hick Hack");
            primaryStage.setScene(scene);
            primaryStage.setFullScreen(true); // Set the stage to full screen
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads and displays the rules screen.
     */
    public void showRulesPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hellofx/resources/rules.fxml"));
            Parent root = loader.load();
            RulesController controller = loader.getController();
            controller.setMainApp(this);
            
            // Set up the application logo
            InputStream logoStream = getClass().getResourceAsStream("/hellofx/resources/logo/logo.jpg");
            if (logoStream == null) {
                System.out.println("Logo not found!");
            } else {
                primaryStage.getIcons().add(new Image(logoStream));
            }

            Scene scene = new Scene(root);
            primaryStage.setTitle("Hick Hack - Rules");
            primaryStage.setScene(scene);
            primaryStage.setFullScreen(true); // Set the stage to full screen
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads and displays the winner screen at game end.
     * @param players List of players to display final results
     */
    public void showWinnerPage(List<Player> players) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hellofx/resources/winner.fxml"));
            Parent root = loader.load();
            WinnerController controller = loader.getController();
            controller.setMainApp(this); // Ensure the mainApp reference is set
            controller.setWinners(players);
            
            // Set up the application logo
            InputStream logoStream = getClass().getResourceAsStream("/hellofx/resources/logo/logo.jpg");
            if (logoStream == null) {
                System.out.println("Logo not found!");
            } else {
                primaryStage.getIcons().add(new Image(logoStream));
            }

            Scene scene = new Scene(root);
            primaryStage.setTitle("Hick Hack - Winner");
            primaryStage.setScene(scene);
            primaryStage.setFullScreen(true); // Set the stage to full screen
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
