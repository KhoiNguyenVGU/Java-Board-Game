package hellofx;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class BeginController implements Initializable {

    private Main mainApp;
    private Game game;

    @FXML
    private Button btnOkDetect;

    @FXML
    private ImageView backgroundImageView;

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    public void onOkButtonPressed() {
        int playerCount = 3; // Fixed number of players
        game = new Game(playerCount); // Initialize the game with the number of players
        mainApp.showGamePage(game); // Pass the game instance to the game page
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load the image and set it to the ImageView
        String imagePath = "/hellofx/begin/begin.jpg";
        Image image = new Image(getClass().getResource(imagePath).toExternalForm());
        backgroundImageView.setImage(image);
    }
}