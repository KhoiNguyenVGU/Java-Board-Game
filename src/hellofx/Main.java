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
        showBeginPage();
    }

    public void showBeginPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("begin.fxml"));
            Parent root = loader.load();
            BeginController controller = loader.getController();
            controller.setMainApp(this);

            Scene scene = new Scene(root);
            primaryStage.setTitle("Hick Hack");
            primaryStage.setScene(scene);
            primaryStage.setFullScreen(true); // Set the stage to full screen
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showGamePage(Game game) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
            Parent root = loader.load();
            GameController controller = loader.getController();
            controller.setGame(game);

            Scene scene = new Scene(root);
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