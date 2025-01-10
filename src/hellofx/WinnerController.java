package hellofx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class WinnerController implements Initializable {

    @FXML
    private Label firstPlaceLabel;

    @FXML
    private Label firstPlaceName;

    @FXML
    private Label secondPlaceLabel;

    @FXML
    private Label secondPlaceName;

    @FXML
    private Label thirdPlaceLabel;

    @FXML
    private Label thirdPlaceName;

    @FXML
    private VBox firstPlaceVBox;

    @FXML
    private VBox secondPlaceVBox;

    @FXML
    private VBox thirdPlaceVBox;

    @FXML
    private AnchorPane rootPane;

    private WinnerAnimation winnerAnimation = new WinnerAnimation();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Apply the CSS file programmatically
        String stylesheetPath = getClass().getResource("/hellofx/styles.css").toExternalForm();
        // Apply the stylesheet to the scene when it becomes available
        Scene scene = firstPlaceName.getScene();
        if (scene != null) {
            scene.getStylesheets().add(stylesheetPath);
        } else {
            firstPlaceName.sceneProperty().addListener((observable, oldScene, newScene) -> {
                if (newScene != null) {
                    newScene.getStylesheets().add(stylesheetPath);
                }
            });
        }
    }

    public void setWinners(List<Player> players) {
        // Sort players by points in descending order
        players.sort((p1, p2) -> Integer.compare(p2.getPoints(), p1.getPoints()));

        if (players.size() > 0) {
            firstPlaceName.setText(players.get(0).getName() + " - " + players.get(0).getPoints() + " points");
            firstPlaceVBox.setPrefHeight(400); // Set height for 1st place
            winnerAnimation.addGlowEffect(firstPlaceName);
            winnerAnimation.playFireworksAnimation(rootPane);
            winnerAnimation.playConfettiAnimation(rootPane);
        }

        if (players.size() > 1) {
            secondPlaceName.setText(players.get(1).getName() + " - " + players.get(1).getPoints() + " points");
            secondPlaceVBox.setPrefHeight(300); // Set height for 2nd place
        }

        if (players.size() > 2) {
            thirdPlaceName.setText(players.get(2).getName() + " - " + players.get(2).getPoints() + " points");
            thirdPlaceVBox.setPrefHeight(200); // Set height for 3rd place
        }
    }

    @FXML
    private void handlePlayAgainAction() {
        try {
            // Load the begin.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("begin.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage) firstPlaceName.getScene().getWindow();

            // Set the new scene
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleExitAction() {
        // Exit the application
        Stage stage = (Stage) firstPlaceName.getScene().getWindow();
        stage.close();
    }
}