package com.cheong.tenten;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class HowToPlay extends AppCompatActivity {

    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_play);

        String text = "The objective of Tenten is to put together a hand with the highest score. " +
                "A player's hand can consist of no more than 6 cards at any point in time. " +
                "Each card in hand contributes its value to the player's current score. " +
                "As players take turns to move, their current scores are updated accordingly. " +
                "The game ends when the deck has been emptied. " +
                "(Note that the use of special abilities may change certain rules in the game.) \n" +
                "\n" +
                "Players are assigned a starting hand of 4 cards each. " +
                "A deck of cards is displayed at the centre of the screen, " +
                "which players can tap on to draw a card if it is their turn. " +
                "During their turn, players can either:\n" +
                "\n" +
                " - Use a card's special ability, if possible (the card is automatically discarded), or\n" +
                " - Draw a card from the deck. If the player already has 6 cards, he must choose 1 to discard.\n" +
                "\n" +
                "Aces are of value 1. Jacks, Queens, Kings are of values 11, 12, 13 respectively. All other cards retain their numeric values.\n" +
                "\n" +
                "The following cards are allocated special abilities:\n" +
                "\n" +
                " - Ace of clubs, diamonds and hearts: Abilities of opponent’s cards are deactivated.\n" +
                " - Ace of spades: Highest hand wins.\n" +
                " - Eight of hearts and spades: Opponent discards 2 random cards.\n" +
                " - Nine of hearts and spades: Draw 2 cards.\n" +
                " - Ten of hearts and spades: Draw 3 cards.\n" +
                " - Jack of hearts and spades: Opponent discards 2 highest cards.\n" +
                " - King of hearts: Game ends next round.\n" +
                " - King of spades: Lowest hand wins."
                ;

        tv = (TextView) findViewById(R.id.tutorialText);
        tv.setText(text);
    }
}
