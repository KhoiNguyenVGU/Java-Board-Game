package hellofx;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class BeginController implements Initializable {

    @FXML
    private Button btnOkDetect;

    @FXML
    private Button showRulesButton;

    @FXML
    private Button exitButton;

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
    public void onOkButtonPressed(ActionEvent event) {
        int playerCount = 3; // Fixed number of players
        game = new Game(playerCount); // Initialize the game with the number of players
        if (mainApp != null) {
            mainApp.showGamePage(game); // Pass the game instance to the game page
        } else {
            System.err.println("mainApp is null, cannot proceed.");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load the image and set it to the ImageView
        String imagePath = "/hellofx/resources/begin/begin.jpg";
        URL imageUrl = getClass().getResource(imagePath);
        if (imageUrl == null) {
            System.out.println("Image not found: " + imagePath);
        } else {
            Image image = new Image(imageUrl.toExternalForm());
            backgroundImageView.setImage(image);
        }

        // Bind the ImageView size to the AnchorPane size
        backgroundImageView.fitWidthProperty().bind(rootPane.widthProperty());
        backgroundImageView.fitHeightProperty().bind(rootPane.heightProperty());

        // Load the CSS file programmatically
        Scene scene = rootPane.getScene();
        String cssPath = "/hellofx/resources/styles.css";
        URL cssUrl = getClass().getResource(cssPath);
        if (cssUrl == null) {
            System.out.println("CSS not found: " + cssPath);
        } else {
            if (scene != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                rootPane.sceneProperty().addListener((_, _, newScene) -> {
                    if (newScene != null) {
                        newScene.getStylesheets().add(cssUrl.toExternalForm());
                    }
                });
            }
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

        // Bounce animation for buttons
        Timeline bounceTimeline = new Timeline(
            new KeyFrame(Duration.ZERO, 
                new KeyValue(btnOkDetect.translateYProperty(), 0), 
                new KeyValue(showRulesButton.translateYProperty(), 0),
                new KeyValue(exitButton.translateYProperty(), 0)
            ),
            new KeyFrame(Duration.seconds(0.4), 
                new KeyValue(btnOkDetect.translateYProperty(), -15), 
                new KeyValue(showRulesButton.translateYProperty(), -15),
                new KeyValue(exitButton.translateYProperty(), -15)
            ),
            new KeyFrame(Duration.seconds(0.6), 
                new KeyValue(btnOkDetect.translateYProperty(), -7), 
                new KeyValue(showRulesButton.translateYProperty(), -7),
                new KeyValue(exitButton.translateYProperty(), -7)
            ),
            new KeyFrame(Duration.seconds(1.0), 
                new KeyValue(btnOkDetect.translateYProperty(), 0), 
                new KeyValue(showRulesButton.translateYProperty(), 0),
                new KeyValue(exitButton.translateYProperty(), 0)
            )
        );
        bounceTimeline.setCycleCount(Timeline.INDEFINITE);
        bounceTimeline.play();

        // Pulsating animation for titleLabel
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(1), titleLabel);
        scaleTransition.setFromX(1.0);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToX(1.1);
        scaleTransition.setToY(1.1);
        scaleTransition.setAutoReverse(true);
        scaleTransition.setCycleCount(Timeline.INDEFINITE);

        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), titleLabel);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.8);
        fadeTransition.setAutoReverse(true);
        fadeTransition.setCycleCount(Timeline.INDEFINITE);

        scaleTransition.play();
        fadeTransition.play();
    }

    @FXML
    private void handleShowRulesAction(ActionEvent event) {
        if (mainApp != null) {
            mainApp.showRulesPage();
        } else {
            System.err.println("mainApp is null, cannot proceed.");
        }
    }
    
    @FXML
    public void handleExitAction(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }
}