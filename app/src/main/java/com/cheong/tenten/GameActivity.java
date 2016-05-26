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
    private CharSequence[] boxName;
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
        boxName = new CharSequence[6];
        boxIsEmpty = new boolean[6];
        boxValue = new int[6];
        for (int i = 0; i < 6; i++) {
            boxIsEmpty[i] = true;
            boxValue[i] = 0;
            boxName[i] = "EMPTY";
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
        int value = random.nextInt(13);
        while(drawnCards.contains(value)){
            value = random.nextInt(13);
        }
        // Set the image in one of the boxes
        Card card = new Card(value);
        if (boxIsEmpty[0]) {
            boxImage0.setImageResource(card.getImage());
            boxIsEmpty[0] = false;
            boxName[0] = card.toString();
            boxValue[0] = card.getValue();
            TextView tv = (TextView)findViewById(R.id.playerScore);
            updateScore(card.getValue(), tv);
            drawnCards.add(value);
            if (drawnCards.size()==13) {
                endGame();
            }else {
                computerMoves();
            }
        }
        else if (boxIsEmpty[1]) {
            boxImage1.setImageResource(card.getImage());
            boxIsEmpty[1] = false;
            boxName[1] = card.toString();
            boxValue[1] = card.getValue();
            TextView tv = (TextView)findViewById(R.id.playerScore);
            updateScore(card.getValue(), tv);
            drawnCards.add(value);
            if (drawnCards.size()==13) {
                endGame();
            }else {
                computerMoves();
            }
        }
        else if (boxIsEmpty[2]) {
            boxImage2.setImageResource(card.getImage());
            boxIsEmpty[2] = false;
            boxName[2] = card.toString();
            boxValue[2] = card.getValue();
            TextView tv = (TextView)findViewById(R.id.playerScore);
            updateScore(card.getValue(), tv);
            drawnCards.add(value);
            if (drawnCards.size()==13) {
                endGame();
            }else {
                computerMoves();
            }
        }
        else if (boxIsEmpty[3]) {
            boxImage3.setImageResource(card.getImage());
            boxIsEmpty[3] = false;
            boxName[3] = card.toString();
            boxValue[3] = card.getValue();
            TextView tv = (TextView)findViewById(R.id.playerScore);
            updateScore(card.getValue(), tv);
            drawnCards.add(value);
            if (drawnCards.size()==13) {
                endGame();
            }else {
                computerMoves();
            }
        }
        else if (boxIsEmpty[4]) {
            boxImage4.setImageResource(card.getImage());
            boxIsEmpty[4] = false;
            boxName[4] = card.toString();
            boxValue[4] = card.getValue();
            TextView tv = (TextView)findViewById(R.id.playerScore);
            updateScore(card.getValue(), tv);
            drawnCards.add(value);
            if (drawnCards.size()==13) {
                endGame();
            }else {
                computerMoves();
            }
        }
        else if (boxIsEmpty[5]){
            boxImage5.setImageResource(card.getImage());
            boxIsEmpty[5] = false;
            boxName[5] = card.toString();
            boxValue[5] = card.getValue();
            TextView tv = (TextView)findViewById(R.id.playerScore);
            updateScore(card.getValue(), tv);
            drawnCards.add(value);
            if (drawnCards.size()==13) {
                endGame();
            }
            else {
                computerMoves();
            }
        }
        else {
            replaceCard(card, value, boxName);
        }
    }

    private void updateScore(int value, TextView tv) {
        String text = tv.getText().toString();
        int currentScore = Integer.parseInt(text);
        int newScore = currentScore + value;
        tv.setText(String.valueOf(newScore));
    }

    private void replaceCard(Card card, int value, CharSequence[] names) {
        CharSequence[] options = new CharSequence[6];
        for (int i = 0; i < names.length; i++) {
            if (names[i] != "EMPTY") {
                options[i] = names[i];
            }
        }
        final CharSequence name = card.toString();
        final int val = card.getValue();
        final int v = value;
        final int image = card.getImage();
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("You have the maximum number of cards. Choose one to discard.");
        adb.setItems(options, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                TextView tv = (TextView)findViewById(R.id.playerScore);
                updateScore(-boxValue[id], tv);
                boxValue[id] = val;
                boxImages[id].setImageResource(image);
                boxName[id] = name;
                updateScore(val, tv);
                drawnCards.add(v);
                dialog.cancel();
                if (drawnCards.size()==13) {
                    endGame();
                }
                else {
                    computerMoves();
                }
            }
        });
        adb.show();
    }

    private void computerMoves() {
        int value = random.nextInt(13);
        while (drawnCards.contains(value)) {
            value = random.nextInt(13);
        }
        Card card = new Card(value);
        if (computerCardValues.size() == 6) {
            replaceComputerCard();
        }
        TextView tv = (TextView) findViewById(R.id.computerScore);
        updateScore(card.getValue(), tv);
        computerCardValues.add(card.getValue());
        drawnCards.add(value);
        if (drawnCards.size() == 13) {
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
