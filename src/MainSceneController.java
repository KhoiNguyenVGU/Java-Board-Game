import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class MainSceneController {

    @FXML
    private TextField textTitle;

    @FXML
    void OnClick(ActionEvent event) {
        Stage mainWindow = (Stage) textTitle.getScene().getWindow();
        String title = textTitle.getText();
        mainWindow.setTitle(title);
    }

}
