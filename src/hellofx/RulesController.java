package hellofx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class RulesController {

    private Main mainApp;

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private VBox rootVBox;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Text rule1, rule2, rule3, rule4;
    @FXML
    private Text scoreRule1, scoreRule2, scoreRule3, scoreRule4;
    @FXML
    private Text instruction1, instruction2, instruction3, instruction4, instruction5, instruction6;

    @FXML
    private Label gameRulesLabel, scoreRuleLabel, gameInstructionLabel;

    @FXML
    private ImageView gameRulesGif, scoreRuleGif, gameInstructionGif;

    @FXML
    private void initialize() {
        // Bind the wrappingWidth property of the Text nodes to the width of the ScrollPane
        rule1.wrappingWidthProperty().bind(scrollPane.widthProperty().subtract(20));
        rule2.wrappingWidthProperty().bind(scrollPane.widthProperty().subtract(20));
        rule3.wrappingWidthProperty().bind(scrollPane.widthProperty().subtract(20));
        rule4.wrappingWidthProperty().bind(scrollPane.widthProperty().subtract(20));
        scoreRule1.wrappingWidthProperty().bind(scrollPane.widthProperty().subtract(20));
        scoreRule2.wrappingWidthProperty().bind(scrollPane.widthProperty().subtract(20));
        scoreRule3.wrappingWidthProperty().bind(scrollPane.widthProperty().subtract(20));
        scoreRule4.wrappingWidthProperty().bind(scrollPane.widthProperty().subtract(20));
        instruction1.wrappingWidthProperty().bind(scrollPane.widthProperty().subtract(20));
        instruction2.wrappingWidthProperty().bind(scrollPane.widthProperty().subtract(20));
        instruction3.wrappingWidthProperty().bind(scrollPane.widthProperty().subtract(20));
        instruction4.wrappingWidthProperty().bind(scrollPane.widthProperty().subtract(20));
        instruction5.wrappingWidthProperty().bind(scrollPane.widthProperty().subtract(20));
        instruction6.wrappingWidthProperty().bind(scrollPane.widthProperty().subtract(20));

        // Load GIF images
        loadGif(gameRulesGif, "/hellofx/resources/gif/fire.gif");
        loadGif(scoreRuleGif, "/hellofx/resources/gif/fire.gif");
        loadGif(gameInstructionGif, "/hellofx/resources/gif/fire.gif");
    }

    private void loadGif(ImageView imageView, String path) {
        Image image = new Image(getClass().getResourceAsStream(path));
        imageView.setImage(image);
    }

    @FXML
    private void handleBackToStartAction(ActionEvent event) {
        try {
            // Load the begin.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hellofx/resources/begin.fxml"));
            Parent root = loader.load();

            // Get the controller and set the mainApp reference
            BeginController beginController = loader.getController();
            beginController.setMainApp(mainApp);

            // Get the current stage
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();

            // Set the new scene
            stage.setScene(new Scene(root));
            stage.setFullScreen(true); // Set the stage to full screens
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}