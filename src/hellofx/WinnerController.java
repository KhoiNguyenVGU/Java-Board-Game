package hellofx;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class WinnerController implements Initializable {

    @FXML
    private Label firstPlaceLabel;

    @FXML
    private Label secondPlaceLabel;

    @FXML
    private Label thirdPlaceLabel;

    @FXML
    private ImageView congratulationsImageView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load the GIF and set it to the ImageView
        String gifPath = "/hellofx/resources/congratulations.gif";
        URL gifUrl = getClass().getResource(gifPath);
        if (gifUrl != null) {
            Image gifImage = new Image(gifUrl.toExternalForm());
            congratulationsImageView.setImage(gifImage);
        } else {
            System.err.println("GIF not found: " + gifPath);
        }
    }

    public void setWinners(List<Player> players) {
        // Sort players by points in descending order
        players.sort((p1, p2) -> Integer.compare(p2.getPoints(), p1.getPoints()));

        // Set the text for the labels
        if (players.size() > 0) {
            firstPlaceLabel.setText("1st Place: " + players.get(0).getName() + " - " + players.get(0).getPoints() + " points");
        }
        if (players.size() > 1) {
            secondPlaceLabel.setText("2nd Place: " + players.get(1).getName() + " - " + players.get(1).getPoints() + " points");
        }
        if (players.size() > 2) {
            thirdPlaceLabel.setText("3rd Place: " + players.get(2).getName() + " - " + players.get(2).getPoints() + " points");
        }
    }

    @FXML
    private void handlePlayAgainAction() {
        // Logic to restart the game
        Stage stage = (Stage) firstPlaceLabel.getScene().getWindow();
        stage.close();
        // Open the main game stage again (implement this logic as needed)
    }

    @FXML
    private void handleExitAction() {
        // Exit the application
        Stage stage = (Stage) firstPlaceLabel.getScene().getWindow();
        stage.close();
    }
}