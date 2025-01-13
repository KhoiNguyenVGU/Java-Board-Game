/**
 * RulesController.java
 * 
 * This class is the controller for the rules screen of the JavaFX application.
 * It handles the initialization of UI components, loading of resources, and 
 * provides event handlers for button actions.
 */

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
 
     /**
      * Sets the main application instance.
      * 
      * @param mainApp the main application instance
      */
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
 
     /**
      * Initializes the controller class. This method is automatically called after the FXML file has been loaded.
      */
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
 
     /**
      * Loads a GIF image into an ImageView.
      * 
      * @param imageView the ImageView to load the GIF into
      * @param path the path to the GIF image
      */
     private void loadGif(ImageView imageView, String path) {
         Image image = new Image(getClass().getResourceAsStream(path));
         imageView.setImage(image);
     }
 
     /**
      * Event handler for the Back to Start button action.
      * Loads the initial screen of the application.
      * 
      * @param event the action event
      */
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
             stage.setFullScreen(true); // Set the stage to full screen
             stage.show();
         } catch (Exception e) {
             e.printStackTrace();
         }
     }
 }