package com.cheong.tenten;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    // UI components
    private Button useAbility;
    private ImageButton drawButton;
    private ImageView boxImage0;
    private ImageView boxImage1;
    private ImageView boxImage2;
    private ImageView boxImage3;
    private ImageView boxImage4;
    private ImageView boxImage5;
    private ImageView[] boxImages;
    private boolean[] boxIsEmpty;
    private Card[] boxCards;
    private ArrayList<Card> computerCards;
    private boolean gameEnded;

    private final static Random random = new Random();

    private ArrayList<Integer> drawnCards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        useAbility = (Button) findViewById(R.id.useAbility);
        drawButton = (ImageButton) findViewById(R.id.drawButton);
        boxImage0 = (ImageView) findViewById(R.id.boxImage0);
        boxImage1 = (ImageView) findViewById(R.id.boxImage1);
        boxImage2 = (ImageView) findViewById(R.id.boxImage2);
        boxImage3 = (ImageView) findViewById(R.id.boxImage3);
        boxImage4 = (ImageView) findViewById(R.id.boxImage4);
        boxImage5 = (ImageView) findViewById(R.id.boxImage5);

        boxImages = new ImageView[]{boxImage0, boxImage1, boxImage2, boxImage3, boxImage4, boxImage5};
        boxIsEmpty = new boolean[6];
        boxCards = new Card[6];
        drawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!gameEnded) {
                    drawCard(1);
                }
            }
            });
        useAbility.setOnClickListener(this);
        computerCards = new ArrayList<Card>();
        drawnCards = new ArrayList<Integer>();
        gameEnded = false;

        for (int i = 0; i < 6; i++) {
            boxIsEmpty[i] = true;
            boxCards[i] = null;
        }
        for (int j = 0; j < 4; j++) {
            drawCard(1);
        }
        useAbility.setClickable(false);
        useAbility.setTextColor(Color.parseColor("#60C0C0C0"));
        useAbility.setBackgroundColor(Color.parseColor("#60C0C0C0"));
        for (int k = 0; k < 4; k++) {
            if (!boxIsEmpty[k]) {
                if (boxCards[k].getAbility()!="None") {
                    useAbility.setClickable(true);
                    useAbility.setTextColor(Color.BLACK);
                    useAbility.setBackgroundColor(Color.LTGRAY);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        final ArrayList<Card> temp1 = new ArrayList<Card>();
        final ArrayList<Integer> temp2 = new ArrayList<Integer>();
        for (int i = 0; i<boxCards.length; i++) {
            if (!boxIsEmpty[i]) {
                if (boxCards[i].getAbility()!="None") {
                    temp1.add(boxCards[i]);
                    temp2.add(i);
                }
            }
        }
        CharSequence[] options = new CharSequence[temp1.size()];
        for (int j = 0; j<temp1.size(); j++) {
            options[j] = temp1.get(j).toString() + " - " + temp1.get(j).getAbility();
        }
        AlertDialog.Builder chooseAbility = new AlertDialog.Builder(this);
        chooseAbility.setTitle("Choose an ability to use.");
        chooseAbility.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                TextView showMessage = (TextView)findViewById(R.id.showMessage);
                showMessage.setText(null);
                drawButton.setClickable(false);
                executeAbility(temp2.get(id));
            }
        });
        chooseAbility.show();
        chooseAbility.setCancelable(true);
    }

    private void drawCard(int howMany) {
        TextView showMessage = (TextView)findViewById(R.id.showMessage);
        showMessage.setText(null);
        // Draw a card from the deckPile
        int value = random.nextInt(52);
        while(drawnCards.contains(value)){
            value = random.nextInt(52);
        }
        // Set the image in one of the boxes
        Card card = new Card(value);
        if (boxIsEmpty[0]) {
            boxImage0.setImageResource(card.getImage());
            boxIsEmpty[0] = false;
            boxCards[0] = card;
            TextView tv = (TextView)findViewById(R.id.playerScore);
            updateScore(card.getValue(), tv);
            drawnCards.add(value);
            if (drawnCards.size()==52) {
                endGame();
            }else {
                if (howMany != 1) {
                    howMany--;
                    drawCard(howMany);
                }
                else {
                    computerMoves();
                }
            }
        }
        else if (boxIsEmpty[1]) {
            boxImage1.setImageResource(card.getImage());
            boxIsEmpty[1] = false;
            boxCards[1] = card;
            TextView tv = (TextView)findViewById(R.id.playerScore);
            updateScore(card.getValue(), tv);
            drawnCards.add(value);
            if (drawnCards.size()==52) {
                endGame();
            }else {
                if (howMany != 1) {
                    howMany--;
                    drawCard(howMany);
                }
                else {
                    computerMoves();
                }
            }
        }
        else if (boxIsEmpty[2]) {
            boxImage2.setImageResource(card.getImage());
            boxIsEmpty[2] = false;
            boxCards[2] = card;
            TextView tv = (TextView)findViewById(R.id.playerScore);
            updateScore(card.getValue(), tv);
            drawnCards.add(value);
            if (drawnCards.size()==52) {
                endGame();
            }else {
                if (howMany != 1) {
                    howMany--;
                    drawCard(howMany);
                }
                else {
                    computerMoves();
                }
            }
        }
        else if (boxIsEmpty[3]) {
            boxImage3.setImageResource(card.getImage());
            boxIsEmpty[3] = false;
            boxCards[3] = card;
            TextView tv = (TextView)findViewById(R.id.playerScore);
            updateScore(card.getValue(), tv);
            drawnCards.add(value);
            if (drawnCards.size()==52) {
                endGame();
            }else {
                if (howMany != 1) {
                    howMany--;
                    drawCard(howMany);
                }
                else {
                    computerMoves();
                }
            }
        }
        else if (boxIsEmpty[4]) {
            boxImage4.setImageResource(card.getImage());
            boxIsEmpty[4] = false;
            boxCards[4] = card;
            TextView tv = (TextView)findViewById(R.id.playerScore);
            updateScore(card.getValue(), tv);
            drawnCards.add(value);
            if (drawnCards.size()==52) {
                endGame();
            }else {
                if (howMany != 1) {
                    howMany--;
                    drawCard(howMany);
                }
                else {
                    computerMoves();
                }
            }
        }
        else if (boxIsEmpty[5]){
            boxImage5.setImageResource(card.getImage());
            boxIsEmpty[5] = false;
            boxCards[5] = card;
            TextView tv = (TextView)findViewById(R.id.playerScore);
            updateScore(card.getValue(), tv);
            drawnCards.add(value);
            if (drawnCards.size()==52) {
                endGame();
            }
            else {
                if (howMany != 1) {
                    howMany--;
                    drawCard(howMany);
                }
                else {
                    computerMoves();
                }
            }
        }
        else {
            replaceCard(card, value, howMany);
        }
    }

    private void updateScore(int value, TextView tv) {
        String text = tv.getText().toString();
        int currentScore = Integer.parseInt(text);
        int newScore = currentScore + value;
        tv.setText(String.valueOf(newScore));
    }

    private void replaceCard(Card c, int v, int m) {
        drawButton.setClickable(false);
        useAbility.setClickable(false);
        useAbility.setTextColor(Color.parseColor("#60C0C0C0"));
        useAbility.setBackgroundColor(Color.parseColor("#60C0C0C0"));
        final TextView showMessage = (TextView)findViewById(R.id.showMessage);
        showMessage.setText("You have the maximum number of cards. Choose one to discard.");
        final ImageView drawnCardImage = (ImageView)findViewById(R.id.drawnCardImage);
        drawnCardImage.setImageResource(c.getImage());
        final TextView tv = (TextView)findViewById(R.id.playerScore);
        final Card card = c;
        final int value = v;
        final int howMany = m;

        drawnCardImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMessage.setText(null);
                drawnCards.add(value);
                drawnCardImage.setImageResource(R.drawable.empty);
                drawnCardImage.setClickable(false);
                for (int i=0; i<boxImages.length; i++) {
                    boxImages[i].setClickable(false);
                }
                if (drawnCards.size()==52) {
                    endGame();
                }
                else {
                    if (howMany != 1) {
                        int temp = howMany;
                        temp--;
                        drawCard(temp);
                    }
                    else {
                        computerMoves();
                    }
                }
            }
        });

        for (int i=0; i<boxImages.length; i++) {
            final int id = i;
            boxImages[id].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showMessage.setText(null);
                    updateScore(-boxCards[id].getValue(), tv);
                    boxCards[id] = card;
                    boxImages[id].setImageResource(card.getImage());
                    updateScore(card.getValue(), tv);
                    drawnCards.add(value);
                    drawnCardImage.setImageResource(R.drawable.empty);
                    drawnCardImage.setClickable(false);
                    for (int i=0; i<boxImages.length; i++) {
                        boxImages[i].setClickable(false);
                    }
                    if (drawnCards.size()==52) {
                        endGame();
                    }
                    else {
                        if (howMany != 1) {
                            int temp = howMany;
                            temp--;
                            drawCard(temp);
                        }
                        else {
                            computerMoves();
                        }
                    }
                }
                });
        }
    }

    private void computerMoves() {
        int value = random.nextInt(52);
        while (drawnCards.contains(value)) {
            value = random.nextInt(52);
        }
        Card card = new Card(value);
        if (computerCards.size() == 6) {
            replaceComputerCard(card);
        }else{
            computerCards.add(card);
            TextView tv = (TextView) findViewById(R.id.computerScore);
            updateScore(card.getValue(), tv);
        }
        drawnCards.add(value);
        if (drawnCards.size() == 52) {
            endGame();
        }
        else {
            TextView showMessage = (TextView)findViewById(R.id.showMessage);
            showMessage.setText("It's your turn. Draw a card or use an ability.");
            drawButton.setClickable(true);
            useAbility.setClickable(false);
            useAbility.setTextColor(Color.parseColor("#60C0C0C0"));
            useAbility.setBackgroundColor(Color.parseColor("#60C0C0C0"));
            for (int j=0; j<boxCards.length; j++) {
                if (!boxIsEmpty[j]) {
                    if (boxCards[j].getAbility()!="None") {
                        useAbility.setClickable(true);
                        useAbility.setTextColor(Color.BLACK);
                        useAbility.setBackgroundColor(Color.LTGRAY);
                    }
                }
            }
        }
    }

    private void replaceComputerCard(Card card) {
        int k = card.getValue();
        int min = k;
        int index = -1;
        for (int i = 0; i<computerCards.size(); i++) {
            if (computerCards.get(i).getValue() < min) {
                min = computerCards.get(i).getValue();
                index = i;
            }
        }
        if (index!=-1) {
            computerCards.remove(index);
            TextView tv = (TextView) findViewById(R.id.computerScore);
            updateScore(-min, tv);
            updateScore(k, tv);
            computerCards.add(card);
        }
    }

    private void endGame() {
        gameEnded = true;
        drawButton.setImageResource(R.drawable.empty);
        drawButton.setClickable(false);
        useAbility.setClickable(false);
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

    /* -----------Methods for card abilities ------------------------- */

    private void executeAbility(int boxNum) {
        useAbility.setClickable(false);
        useAbility.setTextColor(Color.parseColor("#60C0C0C0"));
        useAbility.setBackgroundColor(Color.parseColor("#60C0C0C0"));
        final int boxNumber = boxNum;
        if (boxCards[boxNumber].getAbility() == "Opponent discards 3 highest cards") {
            Intent intent = new Intent(this, ExplosiveActivity.class);
            startActivity(intent);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    discard3highest(boxNumber);
                }
            }, 6000);
        }
        else {
            final TextView abilityText = (TextView) findViewById(R.id.abilityText);
            abilityText.setBackgroundColor(Color.BLACK);
            abilityText.setText("You used " + boxCards[boxNumber].toString() + "!");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    abilityText.setText(null);
                    abilityText.setBackgroundColor(Color.TRANSPARENT);
                    if (boxCards[boxNumber].getAbility() == "View opponent's hand") {
                        view(boxNumber);
                    }
                    else if (boxCards[boxNumber].getAbility() == "Opponent discards 2 random cards") {
                        discard2(boxNumber);
                    }
                    else if (boxCards[boxNumber].getAbility() == "Draw 2 cards") {
                        draw2(boxNumber);
                    }
                    else if (boxCards[boxNumber].getAbility() == "Draw 3 cards") {
                        draw3(boxNumber);
                    }
                }
            }, 3000);
        }
    }

    private void view(int boxNumber) {
        if (computerCards.size()>0) {
            final ImageView img = (ImageView) findViewById(R.id.highestCardImage);
            final ImageView glitter = (ImageView) findViewById(R.id.glitterFrame);
            glitter.setBackgroundResource(R.drawable.glitter);
            final AnimationDrawable frameAnimation = (AnimationDrawable) glitter.getBackground();
            frameAnimation.start();

            img.postDelayed(new Runnable() {
                int m = 0;
                public void run() {
                    img.setImageResource(computerCards.get(m).getImage());
                    m++;
                    if (m < computerCards.size()) {
                        img.postDelayed(this,1000);
                    }
                    else {
                        img.postDelayed(new Runnable() {
                            public void run() {
                                img.setImageResource(R.drawable.empty);
                                glitter.setBackgroundResource(R.drawable.empty);
                                computerMoves();
                            }
                        }, 1000);
                    }
                } }, 0);
        }
        else {
            computerMoves();
        }

        TextView tv = (TextView)findViewById(R.id.playerScore);
        updateScore(-1, tv);
        boxImages[boxNumber].setImageResource(R.drawable.empty);
        boxIsEmpty[boxNumber] = true;
        boxCards[boxNumber] = null;
    }

    private void discard2(int boxNumber) {
        final ImageView blastPosition = (ImageView) findViewById(R.id.blastPosition);
        final AnimationDrawable a = (AnimationDrawable) blastPosition.getBackground();
        blastPosition.setVisibility(View.VISIBLE);
        a.start();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                blastPosition.clearAnimation();
                blastPosition.setVisibility(View.INVISIBLE);
            }
        }, 1000);

        if (computerCards.size()>0) {
            int index = random.nextInt(computerCards.size());
            int valueToDeduct = computerCards.get(index).getValue();
            computerCards.remove(index);
            TextView tv = (TextView) findViewById(R.id.computerScore);
            updateScore(-valueToDeduct, tv);
            if (computerCards.size()>0) {
                index = random.nextInt(computerCards.size());
                valueToDeduct = computerCards.get(index).getValue();
                computerCards.remove(index);
                updateScore(-valueToDeduct, tv);
            }
        }

        TextView tv = (TextView)findViewById(R.id.playerScore);
        updateScore(-8, tv);
        boxImages[boxNumber].setImageResource(R.drawable.empty);
        boxIsEmpty[boxNumber] = true;
        boxCards[boxNumber] = null;
        computerMoves();
    }

    private void draw2(int boxNumber) {
        TextView tv = (TextView)findViewById(R.id.playerScore);
        updateScore(-9, tv);
        boxImages[boxNumber].setImageResource(R.drawable.empty);
        boxIsEmpty[boxNumber] = true;
        boxCards[boxNumber] = null;

        drawCard(2);
    }

    private void draw3(int boxNumber) {
        TextView tv = (TextView)findViewById(R.id.playerScore);
        updateScore(-10, tv);
        boxImages[boxNumber].setImageResource(R.drawable.empty);
        boxIsEmpty[boxNumber] = true;
        boxCards[boxNumber] = null;

        drawCard(3);
    }

    private void discard3highest(int boxNumber) {
        final ImageView blastPosition = (ImageView) findViewById(R.id.blastPosition);
        final AnimationDrawable a = (AnimationDrawable) blastPosition.getBackground();
        blastPosition.setVisibility(View.VISIBLE);
        a.start();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                blastPosition.clearAnimation();
                blastPosition.setVisibility(View.INVISIBLE);
            }
        }, 1000);

        Collections.sort(computerCards);
        int valueToDeduct;
        for (int i = 0; i < 3; i++) {
            if (computerCards.size()>0) {
                valueToDeduct = computerCards.get(computerCards.size()-1).getValue();
                computerCards.remove(computerCards.size()-1);
                TextView tv = (TextView) findViewById(R.id.computerScore);
                updateScore(-valueToDeduct, tv);
            }
        }

        TextView tv = (TextView)findViewById(R.id.playerScore);
        updateScore(-13, tv);
        boxImages[boxNumber].setImageResource(R.drawable.empty);
        boxIsEmpty[boxNumber] = true;
        boxCards[boxNumber] = null;
        computerMoves();
    }
}
