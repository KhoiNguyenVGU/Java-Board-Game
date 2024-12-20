package hellofx;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class BeginController {

    private Main mainApp;
    private Game game;

    @FXML
    private Button okButton;

    @FXML
    private TextField tfName;

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    public void onOkButtonPressed() {
        String playerName = tfName.getText();
        if (!playerName.isEmpty()) {
            int playerCount = Integer.parseInt(playerName);
            game = new Game(playerCount); // Initialize the game with the number of players
            mainApp.showGamePage(game); // Pass the game instance to the game page
        }
    }
}