package hellofx;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class BeginController implements Initializable {

    @FXML
    private Button btnOkDetect;

    @FXML
    private ImageView backgroundImageView;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private VBox vbox;

    @FXML
    private Label titleLabel;

    @FXML
    private Label welcomeLabel;

    private Main mainApp;
    private Game game;

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

        // Bind the ImageView size to the AnchorPane size
        backgroundImageView.fitWidthProperty().bind(rootPane.widthProperty());
        backgroundImageView.fitHeightProperty().bind(rootPane.heightProperty());

        // Load the CSS file programmatically
        Scene scene = rootPane.getScene();
        if (scene != null) {
            scene.getStylesheets().add(getClass().getResource("/hellofx/styles.css").toExternalForm());
        } else {
            rootPane.sceneProperty().addListener((observable, oldScene, newScene) -> {
                if (newScene != null) {
                    newScene.getStylesheets().add(getClass().getResource("/hellofx/styles.css").toExternalForm());
                }
            });
        }

        // Apply animations programmatically
        applyAnimations();
    }

    private void applyAnimations() {
        // Slide-in animation for labels
        Timeline slideInTimeline = new Timeline(
            new KeyFrame(Duration.ZERO, 
                new KeyValue(titleLabel.translateYProperty(), -100),
                new KeyValue(titleLabel.opacityProperty(), 0),
                new KeyValue(welcomeLabel.translateYProperty(), -100),
                new KeyValue(welcomeLabel.opacityProperty(), 0)
            ),
            new KeyFrame(Duration.seconds(1.5), 
                new KeyValue(titleLabel.translateYProperty(), 0),
                new KeyValue(titleLabel.opacityProperty(), 1),
                new KeyValue(welcomeLabel.translateYProperty(), 0),
                new KeyValue(welcomeLabel.opacityProperty(), 1)
            )
        );
        slideInTimeline.play();

        // Bounce animation for button
        Timeline bounceTimeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(btnOkDetect.translateYProperty(), 0)),
            new KeyFrame(Duration.seconds(0.4), new KeyValue(btnOkDetect.translateYProperty(), -15)),
            new KeyFrame(Duration.seconds(0.6), new KeyValue(btnOkDetect.translateYProperty(), -7)),
            new KeyFrame(Duration.seconds(1.0), new KeyValue(btnOkDetect.translateYProperty(), 0))
        );
        bounceTimeline.setCycleCount(Timeline.INDEFINITE);
        bounceTimeline.play();
    }
}