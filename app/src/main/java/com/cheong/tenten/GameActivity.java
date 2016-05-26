package com.cheong.tenten;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    // UI components
    private ImageButton drawButton;
    private ImageView boxImage0;
    private ImageView boxImage1;
    private ImageView boxImage2;
    private ImageView boxImage3;
    private ImageView boxImage4;
    private ImageView boxImage5;
    private ImageView[] boxImages;
    private boolean[] boxIsEmpty;
    private int[] boxValue;
    private ArrayList<Integer> computerCardValues;
    private boolean gameEnded;

    private final static Random random = new Random();

    private ArrayList<Integer> drawnCards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        drawButton = (ImageButton) findViewById(R.id.drawButton);
        boxImage0 = (ImageView) findViewById(R.id.boxImage0);
        boxImage1 = (ImageView) findViewById(R.id.boxImage1);
        boxImage2 = (ImageView) findViewById(R.id.boxImage2);
        boxImage3 = (ImageView) findViewById(R.id.boxImage3);
        boxImage4 = (ImageView) findViewById(R.id.boxImage4);
        boxImage5 = (ImageView) findViewById(R.id.boxImage5);

        boxImages = new ImageView[]{boxImage0, boxImage1, boxImage2, boxImage3, boxImage4, boxImage5};
        boxIsEmpty = new boolean[6];
        boxValue = new int[6];
        for (int i = 0; i < 6; i++) {
            boxIsEmpty[i] = true;
            boxValue[i] = 0;
        }

        drawButton.setOnClickListener(this);
        computerCardValues = new ArrayList<Integer>();
        drawnCards = new ArrayList<Integer>();
        gameEnded = false;
    }

    @Override
    public void onClick(View v) {
        if (!gameEnded) {
            drawCard();
        }
    }

    private void drawCard() {
        // Draw a card from the deckPile
        int value = random.nextInt(26);
        while(drawnCards.contains(value)){
            value = random.nextInt(26);
        }
        // Set the image in one of the boxes
        Card card = new Card(value);
        if (boxIsEmpty[0]) {
            boxImage0.setImageResource(card.getImage());
            boxIsEmpty[0] = false;
            boxValue[0] = card.getValue();
            TextView tv = (TextView)findViewById(R.id.playerScore);
            updateScore(card.getValue(), tv);
            drawnCards.add(value);
            if (drawnCards.size()==26) {
                endGame();
            }else {
                computerMoves();
            }
        }
        else if (boxIsEmpty[1]) {
            boxImage1.setImageResource(card.getImage());
            boxIsEmpty[1] = false;
            boxValue[1] = card.getValue();
            TextView tv = (TextView)findViewById(R.id.playerScore);
            updateScore(card.getValue(), tv);
            drawnCards.add(value);
            if (drawnCards.size()==26) {
                endGame();
            }else {
                computerMoves();
            }
        }
        else if (boxIsEmpty[2]) {
            boxImage2.setImageResource(card.getImage());
            boxIsEmpty[2] = false;
            boxValue[2] = card.getValue();
            TextView tv = (TextView)findViewById(R.id.playerScore);
            updateScore(card.getValue(), tv);
            drawnCards.add(value);
            if (drawnCards.size()==26) {
                endGame();
            }else {
                computerMoves();
            }
        }
        else if (boxIsEmpty[3]) {
            boxImage3.setImageResource(card.getImage());
            boxIsEmpty[3] = false;
            boxValue[3] = card.getValue();
            TextView tv = (TextView)findViewById(R.id.playerScore);
            updateScore(card.getValue(), tv);
            drawnCards.add(value);
            if (drawnCards.size()==26) {
                endGame();
            }else {
                computerMoves();
            }
        }
        else if (boxIsEmpty[4]) {
            boxImage4.setImageResource(card.getImage());
            boxIsEmpty[4] = false;
            boxValue[4] = card.getValue();
            TextView tv = (TextView)findViewById(R.id.playerScore);
            updateScore(card.getValue(), tv);
            drawnCards.add(value);
            if (drawnCards.size()==26) {
                endGame();
            }else {
                computerMoves();
            }
        }
        else if (boxIsEmpty[5]){
            boxImage5.setImageResource(card.getImage());
            boxIsEmpty[5] = false;
            boxValue[5] = card.getValue();
            TextView tv = (TextView)findViewById(R.id.playerScore);
            updateScore(card.getValue(), tv);
            drawnCards.add(value);
            if (drawnCards.size()==26) {
                endGame();
            }
            else {
                computerMoves();
            }
        }
        else {
            replaceCard(card, value);
        }
    }

    private void updateScore(int value, TextView tv) {
        String text = tv.getText().toString();
        int currentScore = Integer.parseInt(text);
        int newScore = currentScore + value;
        tv.setText(String.valueOf(newScore));
    }

    private void replaceCard(Card card, int value) {
        TextView textView = (TextView)findViewById(R.id.chooseDiscardStaticText);
        textView.setVisibility(TextView.VISIBLE);

        final int val = card.getValue();
        final int v = value;
        final int image = card.getImage();
        for (int i=0; i<boxImages.length; i++) {
            final int id = i;
            boxImages[id].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView textView = (TextView)findViewById(R.id.chooseDiscardStaticText);
                    textView.setVisibility(TextView.INVISIBLE);
                    TextView tv = (TextView)findViewById(R.id.playerScore);
                    updateScore(-boxValue[id], tv);
                    boxValue[id] = val;
                    boxImages[id].setImageResource(image);
                    updateScore(val, tv);
                    drawnCards.add(v);
                    if (drawnCards.size()==26) {
                        endGame();
                    }
                    else {
                        computerMoves();
                    }
                }
                });
        }
    }

    private void computerMoves() {
        int value = random.nextInt(26);
        while (drawnCards.contains(value)) {
            value = random.nextInt(26);
        }
        Card card = new Card(value);
        if (computerCardValues.size() == 6) {
            replaceComputerCard();
        }
        TextView tv = (TextView) findViewById(R.id.computerScore);
        updateScore(card.getValue(), tv);
        computerCardValues.add(card.getValue());
        drawnCards.add(value);
        if (drawnCards.size() == 26) {
            endGame();
        }
    }

    private void replaceComputerCard() {
        int min = computerCardValues.get(0);
        int index = 0;
        for (int i = 1; i<computerCardValues.size(); i++) {
            if (computerCardValues.get(i) < min) {
                min = computerCardValues.get(i);
                index = i;
            }
        }
        computerCardValues.remove(index);
        TextView tv = (TextView) findViewById(R.id.computerScore);
        updateScore(-min, tv);
    }

    private void endGame() {
        gameEnded = true;
        drawButton.setImageResource(R.drawable.empty);
        TextView playertv = (TextView)findViewById(R.id.playerScore);
        int playerScore = Integer.parseInt(playertv.getText().toString());
        TextView comptv = (TextView)findViewById(R.id.computerScore);
        int compScore = Integer.parseInt(comptv.getText().toString());
        AlertDialog.Builder outcome = new AlertDialog.Builder(this);
        if (playerScore > compScore) {
            outcome.setMessage("You win!");
        }
        else if (playerScore == compScore) {
            outcome.setMessage("It's a tie!");
        }
        else {
            outcome.setMessage("You lose!");
        }
        outcome.setCancelable(true);
        outcome.show();
    }
}
