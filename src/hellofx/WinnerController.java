/**
 * WinnerController.java
 * 
 * This class is the controller for the winner screen of the JavaFX application.
 * It handles the initialization of UI components, loading of resources, and 
 * provides event handlers for button actions.
 */

 package hellofx;

 import javafx.animation.ScaleTransition;
 import javafx.animation.SequentialTransition;
 import javafx.animation.TranslateTransition;
 import javafx.fxml.FXML;
 import javafx.fxml.FXMLLoader;
 import javafx.fxml.Initializable;
 import javafx.scene.Parent;
 import javafx.scene.Scene;
 import javafx.scene.control.Button;
 import javafx.scene.control.Label;
 import javafx.scene.layout.AnchorPane;
 import javafx.scene.layout.VBox;
 import javafx.stage.Stage;
 import javafx.util.Duration;
 
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
     private Main mainApp; // Declare the mainApp field
 
     /**
      * Sets the main application instance.
      * 
      * @param mainApp the main application instance
      */
     public void setMainApp(Main mainApp) {
         this.mainApp = mainApp;
     }
 
     @Override
     public void initialize(URL location, ResourceBundle resources) {
         // Apply the CSS file programmatically
         String stylesheetPath = getClass().getResource("/hellofx/resources/styles.css").toExternalForm();
         // Apply the stylesheet to the scene when it becomes available
         Scene scene = firstPlaceName.getScene();
         if (scene != null) {
             scene.getStylesheets().add(stylesheetPath);
         } else {
             firstPlaceName.sceneProperty().addListener((_, _, newScene) -> {
                 if (newScene != null) {
                     newScene.getStylesheets().add(stylesheetPath);
                 }
             });
         }
 
         // Initial positions further off-screen (bottom)
         thirdPlaceVBox.setTranslateY(1000); // Move further off-screen downwards
         secondPlaceVBox.setTranslateY(1000); // Move further off-screen downwards
         firstPlaceVBox.setTranslateY(1000); // Move further off-screen downwards
 
         // Transition for 3rd place
         TranslateTransition thirdPlaceUp = new TranslateTransition(Duration.seconds(2), thirdPlaceVBox); // Slower transition
         thirdPlaceUp.setToY(50); // Move a little lower
         TranslateTransition thirdPlaceRight = new TranslateTransition(Duration.seconds(2), thirdPlaceVBox); // Slower transition
         thirdPlaceRight.setToX(20); // Move really close to the center
 
         // Transition for 2nd place
         TranslateTransition secondPlaceUp = new TranslateTransition(Duration.seconds(2), secondPlaceVBox); // Slower transition
         secondPlaceUp.setToY(0);
         TranslateTransition secondPlaceLeft = new TranslateTransition(Duration.seconds(2), secondPlaceVBox); // Slower transition
         secondPlaceLeft.setToX(-20); // Move really close to the center
 
         // Transition for 1st place
         TranslateTransition firstPlaceUp = new TranslateTransition(Duration.seconds(2), firstPlaceVBox); // Slower transition
         firstPlaceUp.setToY(-50); // Move a little higher
 
         // Zoom in and out animation for 1st place
         ScaleTransition firstPlaceZoomIn = new ScaleTransition(Duration.seconds(2), firstPlaceVBox); // Slower transition
         firstPlaceZoomIn.setFromX(1.0);
         firstPlaceZoomIn.setFromY(1.0);
         firstPlaceZoomIn.setToX(1.3); // Reduced zoom
         firstPlaceZoomIn.setToY(1.3); // Reduced zoom
 
         ScaleTransition firstPlaceZoomOut = new ScaleTransition(Duration.seconds(2), firstPlaceVBox); // Slower transition
         firstPlaceZoomOut.setFromX(1.3); // Reduced zoom
         firstPlaceZoomOut.setFromY(1.3); // Reduced zoom
         firstPlaceZoomOut.setToX(1.0);
         firstPlaceZoomOut.setToY(1.0);
 
         // Loop the zoom animation indefinitely
         SequentialTransition firstPlaceZoom = new SequentialTransition(firstPlaceZoomIn, firstPlaceZoomOut);
         firstPlaceZoom.setCycleCount(SequentialTransition.INDEFINITE);
 
         // Sequential transitions
         SequentialTransition thirdPlaceTransition = new SequentialTransition(thirdPlaceUp, thirdPlaceRight);
         SequentialTransition secondPlaceTransition = new SequentialTransition(secondPlaceUp, secondPlaceLeft);
         SequentialTransition firstPlaceTransition = new SequentialTransition(firstPlaceUp, firstPlaceZoom);
 
         // Play transitions in sequence
         thirdPlaceTransition.setOnFinished(event -> secondPlaceTransition.play());
         secondPlaceTransition.setOnFinished(event -> firstPlaceTransition.play());
 
         thirdPlaceTransition.play();
     }
 
     /**
      * Sets the winners and updates the UI accordingly.
      * 
      * @param players the list of players
      */
     public void setWinners(List<Player> players) {
         // Sort players by points in descending order
         players.sort((p1, p2) -> Integer.compare(p2.getPoints(), p1.getPoints()));
 
         if (players.size() > 0) {
             firstPlaceName.setText(players.get(0).getName() + " - " + players.get(0).getPoints() + " points");
             firstPlaceVBox.setPrefHeight(400); // Set height for 1st place
             winnerAnimation.addGlowEffect(firstPlaceName);
             if (rootPane != null) {
                 winnerAnimation.playFireworksAnimation(rootPane);
                 winnerAnimation.playConfettiAnimation(rootPane);
             } else {
                 System.err.println("rootPane is null, cannot play animations.");
             }
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
 
     /**
      * Event handler for the Play Again button action.
      * Loads the initial screen of the application.
      */
     @FXML
     private void handlePlayAgainAction() {
         try {
             // Load the begin.fxml file
             FXMLLoader loader = new FXMLLoader(getClass().getResource("/hellofx/resources/begin.fxml"));
             Parent root = loader.load();
 
             // Get the controller and set the mainApp reference
             BeginController beginController = loader.getController();
             beginController.setMainApp(mainApp);
 
             // Get the current stage
             Stage stage = (Stage) firstPlaceName.getScene().getWindow();
 
             // Set the new scene
             stage.setScene(new Scene(root));
             stage.setFullScreen(true); // Maximize the stage instead of setting it to full screen
             stage.show();
         } catch (Exception e) {
             e.printStackTrace();
         }
     }
 
     /**
      * Event handler for the Exit button action.
      * Exits the application.
      */
     @FXML
     private void handleExitAction() {
         // Exit the application
         Stage stage = (Stage) firstPlaceName.getScene().getWindow();
         stage.close();
     }
 }