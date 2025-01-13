/**
 * GameState.java
 * 
 * This class represents the state of the game, including the list of cards.
 * It provides methods to get and set the list of cards.
 */

 package hellofx;

 import java.util.List;
 
 public class GameState {
     private List<Card> cards; // List of cards in the game state
 
     /**
      * Gets the list of cards.
      * 
      * @return the list of cards
      */
     public List<Card> getCards() {
         return cards;
     }
 
     /**
      * Sets the list of cards.
      * 
      * @param cards the list of cards to set
      */
     public void setCards(List<Card> cards) {
         this.cards = cards;
     }
 }