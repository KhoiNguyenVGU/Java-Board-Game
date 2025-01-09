package hellofx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showBeginPage(); // Start with the Begin Page
    }

    private void showBeginPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hellofx/begin.fxml"));
            Parent root = loader.load();
            BeginController controller = loader.getController();
            controller.setMainApp(this); // Pass reference to Main for transition
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm()); // Apply CSS
            primaryStage.setTitle("Start Game");
            primaryStage.setScene(scene);
            // primaryStage.setFullScreen(true); // Set the stage to full screen

            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showGamePage(Game game) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hellofx/game.fxml"));
            Parent root = loader.load();
            GameController controller = loader.getController();
            controller.setGame(game);  // Pass the game instance to the controller
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm()); // Apply CSS
            primaryStage.setTitle("Hick Hack");
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