package hellofx;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class WinnerController {

    @FXML
    private Label winnerLabel;

    public void setWinner(String winner) {
        winnerLabel.setText("Winner: " + winner);
    }

    @FXML
    private void handlePlayAgainAction() {
        // Logic to restart the game
        // For example, you can close the current stage and open the main game stage again
        Stage stage = (Stage) winnerLabel.getScene().getWindow();
        stage.close();
        // Open the main game stage again (implement this logic as needed)
    }

    @FXML
    private void handleExitAction() {
        // Exit the application
        Stage stage = (Stage) winnerLabel.getScene().getWindow();
        stage.close();
    }
}