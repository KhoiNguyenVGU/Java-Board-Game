/**
 * WinnerAnimation.java
 * 
 * This class provides animations for the winner screen of the JavaFX application.
 * It includes methods to add a glowing effect to the winner label, play fireworks 
 * animations, and play confetti animations.
 */

 package hellofx;

 import javafx.animation.*;
 import javafx.scene.control.Label;
 import javafx.scene.layout.Pane;
 import javafx.scene.paint.Color;
 import javafx.scene.shape.Circle;
 import javafx.util.Duration;
 
 public class WinnerAnimation {
 
     /**
      * Adds a glowing effect to the winner label.
      * 
      * @param label the label to add the glowing effect to
      */
     public void addGlowEffect(Label label) {
         Timeline glow = new Timeline(
             new KeyFrame(Duration.seconds(0), _ -> label.setStyle("-fx-font-size: 20px; -fx-effect: dropshadow(gaussian, rgba(255, 215, 0, 0.8), 20, 0.5, 0, 0);")),
             new KeyFrame(Duration.seconds(0.5), _ -> label.setStyle("-fx-font-size: 2px; -fx-effect: dropshadow(gaussian, rgba(255, 215, 0, 0.4), 10, 0.5, 0, 0);"))
         );
         glow.setCycleCount(Animation.INDEFINITE);
         glow.setAutoReverse(true);
         glow.play();
     }
 
     /**
      * Plays fireworks animations at random positions.
      * 
      * @param pane the pane to play the fireworks animation on
      */
     public void playFireworksAnimation(Pane pane) {
         if (pane == null) {
             System.err.println("Pane is null, cannot play fireworks animation.");
             return;
         }
 
         Timeline fireworks = new Timeline(new KeyFrame(Duration.seconds(0.5), _ -> {
             Circle firework = new Circle(5, Color.color(Math.random(), Math.random(), Math.random()));
             firework.setCenterX(400 + Math.random() * 400 - 200);
             firework.setCenterY(300 + Math.random() * 200 - 100);
 
             ScaleTransition scale = new ScaleTransition(Duration.seconds(1), firework);
             scale.setFromX(1);
             scale.setFromY(1);
             scale.setToX(5);
             scale.setToY(5);
 
             FadeTransition fade = new FadeTransition(Duration.seconds(1), firework);
             fade.setFromValue(1);
             fade.setToValue(0);
 
             pane.getChildren().add(firework);
             ParallelTransition burst = new ParallelTransition(scale, fade);
             burst.setOnFinished(_ -> pane.getChildren().remove(firework));
             burst.play();
         }));
         fireworks.setCycleCount(Animation.INDEFINITE); // Number of bursts
         fireworks.play();
     }
 
     /**
      * Plays a confetti animation.
      * 
      * @param pane the pane to play the confetti animation on
      */
     public void playConfettiAnimation(Pane pane) {
         if (pane == null) {
             System.err.println("Pane is null, cannot play confetti animation.");
             return;
         }
 
         Timeline confetti = new Timeline(new KeyFrame(Duration.seconds(0.1), _ -> {
             Circle particle = new Circle(3, Color.color(Math.random(), Math.random(), Math.random()));
             particle.setCenterX(Math.random() * pane.getWidth());
             particle.setCenterY(0);
 
             TranslateTransition fall = new TranslateTransition(Duration.seconds(3), particle);
             fall.setToY(pane.getHeight());
 
             RotateTransition rotate = new RotateTransition(Duration.seconds(3), particle);
             rotate.setByAngle(360);
 
             pane.getChildren().add(particle);
             ParallelTransition animation = new ParallelTransition(fall, rotate);
             animation.setOnFinished(_ -> pane.getChildren().remove(particle));
             animation.play();
         }));
         confetti.setCycleCount(Animation.INDEFINITE); // Number of confetti particles
         confetti.play();
     }
 }