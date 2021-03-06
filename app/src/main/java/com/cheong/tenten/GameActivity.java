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
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    // UI components
    private TextView round;
    private Button useAbility;
    private Button continueButton;
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
    private boolean suddendeathMode;
    private int suddendeathCount;
    private int winCondition;
    private int currentRound;
    private int playerPoints;
    private int compPoints;

    private final static Random random = new Random();

    private ArrayList<Integer> drawnCards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        currentRound = 1;
        playerPoints = 0;
        compPoints = 0;
        startRound();
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

    private void startRound() {

        round = (TextView) findViewById(R.id.round);
        useAbility = (Button) findViewById(R.id.useAbility);
        drawButton = (ImageButton) findViewById(R.id.drawButton);
        boxImage0 = (ImageView) findViewById(R.id.boxImage0);
        boxImage1 = (ImageView) findViewById(R.id.boxImage1);
        boxImage2 = (ImageView) findViewById(R.id.boxImage2);
        boxImage3 = (ImageView) findViewById(R.id.boxImage3);
        boxImage4 = (ImageView) findViewById(R.id.boxImage4);
        boxImage5 = (ImageView) findViewById(R.id.boxImage5);
        continueButton = (Button) findViewById(R.id.continue_button);

        boxImages = new ImageView[]{boxImage0, boxImage1, boxImage2, boxImage3, boxImage4, boxImage5};
        boxIsEmpty = new boolean[6];
        boxCards = new Card[6];
        computerCards = new ArrayList<>();
        drawnCards = new ArrayList<>();
        drawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawCard(1);
            }
        });
        useAbility.setOnClickListener(this);

        if (currentRound == 1) {
            round.setText("Round 1");
        }
        else if (currentRound == 2) {
            round.setText("Round 2");
        }
        else {
            round.setText("Round 3");
        }

        suddendeathMode = false;
        suddendeathCount = 2;
        winCondition = 0;
        drawButton.setImageResource(R.drawable.back);
        drawButton.setVisibility(View.VISIBLE);
        continueButton.setClickable(false);
        continueButton.setVisibility(View.INVISIBLE);

        for (int i = 4; i < 6; i++) {
            boxIsEmpty[i] = true;
            boxCards[i] = null;
        }
        TextView tv = (TextView)findViewById(R.id.playerScore);
        tv.setText("0");
        TextView comptv = (TextView)findViewById(R.id.computerScore);
        comptv.setText("0");
        for (int j = 0; j < 4; j++) {
            int value = random.nextInt(52);
            while(drawnCards.contains(value)){
                value = random.nextInt(52);
            }
            Card card = new Card(value);
            boxImages[j].setImageResource(card.getImage());
            boxImages[j].setVisibility(View.VISIBLE);
            boxIsEmpty[j] = false;
            boxCards[j] = card;
            updateScore(card.getValue(), tv);
            drawnCards.add(value);
        }
        computerDrawCards(4);
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
            boxImage0.setVisibility(View.VISIBLE);
            boxIsEmpty[0] = false;
            boxCards[0] = card;
            TextView tv = (TextView)findViewById(R.id.playerScore);
            updateScore(card.getValue(), tv);
            drawnCards.add(value);
            if (drawnCards.size()==52) {
                endRound();
            }else {
                if (howMany != 1) {
                    howMany--;
                    drawCard(howMany);
                }
                else {
                    if (suddendeathCount == 0) {
                        endRound();
                    }
                    else if (suddendeathMode) {
                        suddendeathCount--;
                        computerMoves();
                    }
                    else {
                        computerMoves();
                    }
                }
            }
        }
        else if (boxIsEmpty[1]) {
            boxImage1.setImageResource(card.getImage());
            boxImage1.setVisibility(View.VISIBLE);
            boxIsEmpty[1] = false;
            boxCards[1] = card;
            TextView tv = (TextView)findViewById(R.id.playerScore);
            updateScore(card.getValue(), tv);
            drawnCards.add(value);
            if (drawnCards.size()==52) {
                endRound();
            }else {
                if (howMany != 1) {
                    howMany--;
                    drawCard(howMany);
                }
                else {
                    if (suddendeathCount == 0) {
                        endRound();
                    }
                    else if (suddendeathMode) {
                        suddendeathCount--;
                        computerMoves();
                    }
                    else {
                        computerMoves();
                    }
                }
            }
        }
        else if (boxIsEmpty[2]) {
            boxImage2.setImageResource(card.getImage());
            boxImage2.setVisibility(View.VISIBLE);
            boxIsEmpty[2] = false;
            boxCards[2] = card;
            TextView tv = (TextView)findViewById(R.id.playerScore);
            updateScore(card.getValue(), tv);
            drawnCards.add(value);
            if (drawnCards.size()==52) {
                endRound();
            }else {
                if (howMany != 1) {
                    howMany--;
                    drawCard(howMany);
                }
                else {
                    if (suddendeathCount == 0) {
                        endRound();
                    }
                    else if (suddendeathMode) {
                        suddendeathCount--;
                        computerMoves();
                    }
                    else {
                        computerMoves();
                    }
                }
            }
        }
        else if (boxIsEmpty[3]) {
            boxImage3.setImageResource(card.getImage());
            boxImage3.setVisibility(View.VISIBLE);
            boxIsEmpty[3] = false;
            boxCards[3] = card;
            TextView tv = (TextView)findViewById(R.id.playerScore);
            updateScore(card.getValue(), tv);
            drawnCards.add(value);
            if (drawnCards.size()==52) {
                endRound();
            }else {
                if (howMany != 1) {
                    howMany--;
                    drawCard(howMany);
                }
                else {
                    if (suddendeathCount == 0) {
                        endRound();
                    }
                    else if (suddendeathMode) {
                        suddendeathCount--;
                        computerMoves();
                    }
                    else {
                        computerMoves();
                    }
                }
            }
        }
        else if (boxIsEmpty[4]) {
            boxImage4.setImageResource(card.getImage());
            boxImage4.setVisibility(View.VISIBLE);
            boxIsEmpty[4] = false;
            boxCards[4] = card;
            TextView tv = (TextView)findViewById(R.id.playerScore);
            updateScore(card.getValue(), tv);
            drawnCards.add(value);
            if (drawnCards.size()==52) {
                endRound();
            }else {
                if (howMany != 1) {
                    howMany--;
                    drawCard(howMany);
                }
                else {
                    if (suddendeathCount == 0) {
                        endRound();
                    }
                    else if (suddendeathMode) {
                        suddendeathCount--;
                        computerMoves();
                    }
                    else {
                        computerMoves();
                    }
                }
            }
        }
        else if (boxIsEmpty[5]){
            boxImage5.setImageResource(card.getImage());
            boxImage5.setVisibility(View.VISIBLE);
            boxIsEmpty[5] = false;
            boxCards[5] = card;
            TextView tv = (TextView)findViewById(R.id.playerScore);
            updateScore(card.getValue(), tv);
            drawnCards.add(value);
            if (drawnCards.size()==52) {
                endRound();
            }
            else {
                if (howMany != 1) {
                    howMany--;
                    drawCard(howMany);
                }
                else {
                    if (suddendeathCount == 0) {
                        endRound();
                    }
                    else if (suddendeathMode) {
                        suddendeathCount--;
                        computerMoves();
                    }
                    else {
                        computerMoves();
                    }
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
        drawnCardImage.setVisibility(View.VISIBLE);
        final TextView tv = (TextView)findViewById(R.id.playerScore);
        final Card card = c;
        final int value = v;
        final int howMany = m;

        drawnCardImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMessage.setText(null);
                drawnCards.add(value);
                drawnCardImage.setVisibility(View.INVISIBLE);
                drawnCardImage.setClickable(false);
                for (int i=0; i<boxImages.length; i++) {
                    boxImages[i].setClickable(false);
                }
                if (drawnCards.size()==52) {
                    endRound();
                }
                else {
                    if (howMany != 1) {
                        int temp = howMany;
                        temp--;
                        drawCard(temp);
                    }
                    else {
                        if (suddendeathCount == 0) {
                            endRound();
                        }
                        else if (suddendeathMode) {
                            suddendeathCount--;
                            computerMoves();
                        }
                        else {
                            computerMoves();
                        }
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
                    drawnCardImage.setVisibility(View.INVISIBLE);
                    drawnCardImage.setClickable(false);
                    for (int i=0; i<boxImages.length; i++) {
                        boxImages[i].setClickable(false);
                    }
                    if (drawnCards.size()==52) {
                        endRound();
                    }
                    else {
                        if (howMany != 1) {
                            int temp = howMany;
                            temp--;
                            drawCard(temp);
                        }
                        else {
                            if (suddendeathCount == 0) {
                                endRound();
                            }
                            else if (suddendeathMode) {
                                suddendeathCount--;
                                computerMoves();
                            }
                            else {
                                computerMoves();
                            }
                        }
                    }
                }
                });
        }
    }

    private void computerMoves() {
        drawButton.setClickable(false);
        useAbility.setClickable(false);
        useAbility.setTextColor(Color.parseColor("#60C0C0C0"));
        useAbility.setBackgroundColor(Color.parseColor("#60C0C0C0"));

        boolean hasAbilityTemp = false;
        boolean hasAbilityForSabotageTemp = false;
        int indexTemp = -1;
        int indexForSabotageTemp = -1;
        if (computerCards.size() > 0) {
            for (int k = 0; k < computerCards.size(); k++) {
                CharSequence ability = computerCards.get(k).getAbility();
                if (ability!="None") {
                    hasAbilityTemp = true;
                    indexTemp = k;
                }
                if (ability=="Deactivate opponent's cards"||ability=="Restoration: Highest hand wins"||ability=="Sudden death: Game ends next round") {
                    hasAbilityForSabotageTemp = true;
                    indexForSabotageTemp = k;
                }
            }
        }

        final boolean hasAbility = hasAbilityTemp;
        final boolean hasAbilityForSabotage = hasAbilityForSabotageTemp;
        final int index = indexTemp;
        final int indexForSabotage = indexForSabotageTemp;
        final TextView showMessage = (TextView) findViewById(R.id.showMessage);
        showMessage.setText("Opponent's turn");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                showMessage.setText(null);
                if (winCondition == 0 && hasAbility) {
                    computerExecuteAbility(index);
                }
                else if (winCondition == 1 && hasAbilityForSabotage) {
                    computerExecuteAbility(indexForSabotage);
                }
                else {
                    computerDrawCards(1);
                }
            }
        }, 2000);
    }

    private void computerDrawCards(int howMany) {
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
            endRound();
        }
        else {
            if (howMany != 1) {
                howMany--;
                computerDrawCards(howMany);
            }
            else {
                if (suddendeathCount == 0) {
                    endRound();
                }
                else if (suddendeathMode) {
                    suddendeathCount--;
                    playerTurn();
                }
                else {
                    playerTurn();
                }
            }
        }
    }

    private void playerTurn() {
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

    private void replaceComputerCard(Card card) {
        int k = card.getValue();
        int index = -1;
        if (winCondition == 0) {
            int min = k;
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
        else {
            int max = k;
            for (int i = 0; i<computerCards.size(); i++) {
                if (computerCards.get(i).getValue() > max) {
                    max = computerCards.get(i).getValue();
                    index = i;
                }
            }
            if (index!=-1) {
                computerCards.remove(index);
                TextView tv = (TextView) findViewById(R.id.computerScore);
                updateScore(-max, tv);
                updateScore(k, tv);
                computerCards.add(card);
            }
        }
    }

    private void endRound() {
        drawButton.setVisibility(View.INVISIBLE);
        drawButton.setClickable(false);
        useAbility.setClickable(false);
        TextView playertv = (TextView)findViewById(R.id.playerScore);
        int playerScore = Integer.parseInt(playertv.getText().toString());
        TextView comptv = (TextView)findViewById(R.id.computerScore);
        int compScore = Integer.parseInt(comptv.getText().toString());
        if (winCondition == 0) {
            if (playerScore > compScore) {
                playerPoints+=2;
            }
            else if (playerScore == compScore) {
                playerPoints++;
                compPoints++;
            }
            else {
                compPoints+=2;
            }
        }
        else if (winCondition == 1) {
            if (playerScore < compScore) {
                playerPoints+=2;
            }
            else if (playerScore == compScore) {
                playerPoints++;
                compPoints++;
            }
            else {
                compPoints+=2;
            }
        }
        AlertDialog.Builder outcome = new AlertDialog.Builder(this);
        outcome.setTitle("Points after this round");
        String message = "You: " +String.valueOf(playerPoints)+"\n"+"\n"+"Computer: "+String.valueOf(compPoints);
        outcome.setMessage(message);
        outcome.setCancelable(true);
        outcome.show();
        continueButton.setClickable(true);
        continueButton.setVisibility(View.VISIBLE);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentRound==3) {
                    endGame();
                }
                else {
                    currentRound++;
                    setContentView(R.layout.activity_game);
                    startRound();
                }
            }
        });
    }

    private void endGame() {
        AlertDialog.Builder outcome = new AlertDialog.Builder(this);
        if (playerPoints > compPoints) {
            outcome.setMessage("You win!");
        }
        else if (playerPoints == compPoints) {
            outcome.setMessage("It's a tie!");
        }
        else {
            outcome.setMessage("You lose!");
        }
        outcome.setCancelable(true);
        outcome.show();
    }

    /* -----------Methods for card abilities (User) ------------------------- */

    private void executeAbility(int boxNum) {
        useAbility.setClickable(false);
        useAbility.setTextColor(Color.parseColor("#60C0C0C0"));
        useAbility.setBackgroundColor(Color.parseColor("#60C0C0C0"));
        final int boxNumber = boxNum;
        if (boxCards[boxNumber].getAbility() == "Restoration: Highest hand wins") {
            Intent intent = new Intent(this, ExplosiveActivity.class);
            intent.putExtra("Message", "Restoration" + "\n" + "Highest hand wins!");
            startActivity(intent);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    restoration(boxNumber);
                }
            }, 6000);
        }
        else if (boxCards[boxNumber].getAbility() == "Sudden death: Game ends next turn") {
            Intent intent = new Intent(this, ExplosiveActivity.class);
            intent.putExtra("Message", "Sudden death" + "\n" + "Game ends next turn");
            startActivity(intent);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    suddendeath(boxNumber);
                }
            }, 6000);
        }
        else if (boxCards[boxNumber].getAbility() == "Sabotage: Lowest hand wins") {
            Intent intent = new Intent(this, ExplosiveActivity.class);
            intent.putExtra("Message", "Sabotage" + "\n" + "Lowest hand wins!");
            startActivity(intent);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    sabotage(boxNumber);
                }
            }, 6000);
        }
        else {
            final TextView showMessage = (TextView) findViewById(R.id.showMessage);
            showMessage.setText("You used " + boxCards[boxNumber].toString() + "!");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    showMessage.setText(null);
                    if (boxCards[boxNumber].getAbility() == "Deactivate opponent's cards") {
                        deactivate(boxNumber);
                    }
                    else if (boxCards[boxNumber].getAbility() == "Opponent discards 2 random cards") {
                        discard2(boxNumber);
                    }
                    else if (boxCards[boxNumber].getAbility() == "Draw 2 cards") {
                        draw(boxNumber,2);
                    }
                    else if (boxCards[boxNumber].getAbility() == "Draw 3 cards") {
                        draw(boxNumber,3);
                    }
                    else if (boxCards[boxNumber].getAbility() == "Opponent discards 2 highest cards") {
                        discardhighest(boxNumber,2);
                    }
                    else if (boxCards[boxNumber].getAbility() == "Opponent discards 3 highest cards") {
                        discardhighest(boxNumber,3);
                    }
                }
            }, 3000);
        }
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
        boxImages[boxNumber].setVisibility(View.INVISIBLE);
        boxIsEmpty[boxNumber] = true;
        boxCards[boxNumber] = null;
        if (suddendeathCount == 0) {
            endRound();
        }
        else if (suddendeathMode) {
            suddendeathCount--;
            computerMoves();
        }
        else {
            computerMoves();
        }
    }

    private void draw(int boxNumber, int howMany) {
        TextView tv = (TextView)findViewById(R.id.playerScore);
        if (howMany == 2) {
            updateScore(-9, tv);
        }
        else if (howMany == 3) {
            updateScore(-10, tv);
        }
        boxImages[boxNumber].setVisibility(View.INVISIBLE);
        boxIsEmpty[boxNumber] = true;
        boxCards[boxNumber] = null;

        drawCard(howMany);
    }

    private void deactivate(int boxNumber) {
        if (computerCards.size()>0) {
            for (int i = 0; i < computerCards.size(); i++) {
                computerCards.get(i).deactivate();
            }
        }

        TextView tv = (TextView)findViewById(R.id.playerScore);
        updateScore(-1, tv);
        boxImages[boxNumber].setVisibility(View.INVISIBLE);
        boxIsEmpty[boxNumber] = true;
        boxCards[boxNumber] = null;
        if (suddendeathCount == 0) {
            endRound();
        }
        else if (suddendeathMode) {
            suddendeathCount--;
            computerMoves();
        }
        else {
            computerMoves();
        }
    }

    private void discardhighest(int boxNumber, int howMany) {
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
        for (int i = 0; i < howMany; i++) {
            if (computerCards.size()>0) {
                valueToDeduct = computerCards.get(computerCards.size()-1).getValue();
                computerCards.remove(computerCards.size()-1);
                TextView tv = (TextView) findViewById(R.id.computerScore);
                updateScore(-valueToDeduct, tv);
            }
        }

        TextView tv = (TextView)findViewById(R.id.playerScore);
        if (howMany == 2) {
            updateScore(-11, tv);
        }
        else if (howMany == 3) {
            updateScore(-12, tv);
        }
        boxImages[boxNumber].setVisibility(View.INVISIBLE);
        boxIsEmpty[boxNumber] = true;
        boxCards[boxNumber] = null;
        if (suddendeathCount == 0) {
            endRound();
        }
        else if (suddendeathMode) {
            suddendeathCount--;
            computerMoves();
        }
        else {
            computerMoves();
        }
    }

    private void restoration(int boxNumber) {
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.gamelayout);
        layout.setBackgroundColor(Color.RED);
        round.setTextColor(Color.parseColor("#00e5ee"));
        TextView playertext = (TextView)findViewById(R.id.playerScoreStaticText);
        playertext.setTextColor(Color.BLACK);
        TextView playertv = (TextView)findViewById(R.id.playerScore);
        playertv.setTextColor(Color.BLACK);
        TextView comptext = (TextView)findViewById(R.id.computerScoreStaticText);
        comptext.setTextColor(Color.BLACK);
        TextView comptv = (TextView)findViewById(R.id.computerScore);
        comptv.setTextColor(Color.BLACK);
        winCondition = 0;

        updateScore(-1, playertv);
        boxImages[boxNumber].setVisibility(View.INVISIBLE);
        boxIsEmpty[boxNumber] = true;
        boxCards[boxNumber] = null;
        if (suddendeathCount == 0) {
            endRound();
        }
        else if (suddendeathMode) {
            suddendeathCount--;
            computerMoves();
        }
        else {
            computerMoves();
        }
    }

    private void suddendeath(int boxNumber) {
        suddendeathMode = true;
        suddendeathCount--;

        TextView tv = (TextView)findViewById(R.id.playerScore);
        updateScore(-13, tv);
        boxImages[boxNumber].setVisibility(View.INVISIBLE);
        boxIsEmpty[boxNumber] = true;
        boxCards[boxNumber] = null;
        computerMoves();
    }

    private void sabotage(int boxNumber) {
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.gamelayout);
        layout.setBackgroundColor(Color.BLACK);
        round.setTextColor(Color.parseColor("#00e5ee"));
        TextView playertext = (TextView)findViewById(R.id.playerScoreStaticText);
        playertext.setTextColor(Color.YELLOW);
        TextView playertv = (TextView)findViewById(R.id.playerScore);
        playertv.setTextColor(Color.YELLOW);
        TextView comptext = (TextView)findViewById(R.id.computerScoreStaticText);
        comptext.setTextColor(Color.YELLOW);
        TextView comptv = (TextView)findViewById(R.id.computerScore);
        comptv.setTextColor(Color.YELLOW);
        winCondition = 1;

        updateScore(-13, playertv);
        boxImages[boxNumber].setVisibility(View.INVISIBLE);
        boxIsEmpty[boxNumber] = true;
        boxCards[boxNumber] = null;
        if (suddendeathCount == 0) {
            endRound();
        }
        else if (suddendeathMode) {
            suddendeathCount--;
            computerMoves();
        }
        else {
            computerMoves();
        }
    }

    /* -----------Methods for card abilities (Computer) ------------------------- */

    private void computerExecuteAbility(int ind) {
        final int index = ind;
        final Card card = computerCards.get(index);
        if (card.getAbility() == "Restoration: Highest hand wins") {
            Intent intent = new Intent(this, ExplosiveActivity.class);
            intent.putExtra("Message", "Restoration" + "\n" + "Highest hand wins!");
            startActivity(intent);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    computerrestoration(index);
                }
            }, 6000);
        }
        else if (card.getAbility() == "Sudden death: Game ends next turn") {
            Intent intent = new Intent(this, ExplosiveActivity.class);
            intent.putExtra("Message", "Sudden death" + "\n" + "Game ends next turn");
            startActivity(intent);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    computersuddendeath(index);
                }
            }, 6000);
        }
        else if (card.getAbility() == "Sabotage: Lowest hand wins") {
            Intent intent = new Intent(this, ExplosiveActivity.class);
            intent.putExtra("Message", "Sabotage" + "\n" + "Lowest hand wins!");
            startActivity(intent);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    computersabotage(index);
                }
            }, 6000);
        }
        else {
            final TextView showMessage = (TextView) findViewById(R.id.showMessage);
            showMessage.setText("COM used " + card.toString() + "!");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    showMessage.setText(null);
                    if (card.getAbility() == "Opponent discards 2 random cards") {
                        computerdiscard2(index);
                    }
                    else if (card.getAbility() == "Draw 2 cards") {
                        computerdraw(index,2);
                    }
                    else if (card.getAbility() == "Draw 3 cards") {
                        computerdraw(index,3);
                    }
                    else if (card.getAbility() == "Deactivate opponent's cards") {
                        computerdeactivate(index);
                    }
                    else if (card.getAbility() == "Opponent discards 2 highest cards") {
                        computerdiscardhighest(index,2);
                    }
                    else if (card.getAbility() == "Opponent discards 3 highest cards") {
                        computerdiscardhighest(index,3);
                    }
                }
            }, 3000);
        }
    }

    private void computerdiscard2(int index) {
        computerCards.remove(index);
        TextView tv = (TextView)findViewById(R.id.computerScore);
        updateScore(-8, tv);
        ArrayList<Integer> canBeDiscarded = new ArrayList<Integer>();
        for (int i = 0; i < boxCards.length; i++) {
            if (!boxIsEmpty[i]) {
                canBeDiscarded.add(i);
            }
        }

        if (canBeDiscarded.size() == 0) {
            if (suddendeathCount == 0) {
                endRound();
            }
            else if (suddendeathMode) {
                suddendeathCount--;
                playerTurn();
            }
            else {
                playerTurn();
            }
        }
        else {
            final ArrayList<Integer> toDiscardList = new ArrayList<Integer>();
            int temp = random.nextInt(canBeDiscarded.size());
            int toDiscard = canBeDiscarded.get(temp);
            boxImages[toDiscard].setImageResource(R.drawable.blast);
            final AnimationDrawable a = (AnimationDrawable) boxImages[toDiscard].getDrawable();
            a.start();
            int valueToDeduct = boxCards[toDiscard].getValue();
            TextView mytv = (TextView) findViewById(R.id.playerScore);
            updateScore(-valueToDeduct, mytv);
            toDiscardList.add(toDiscard);
            canBeDiscarded.remove(temp);
            if (canBeDiscarded.size() > 0) {
                temp = random.nextInt(canBeDiscarded.size());
                toDiscard = canBeDiscarded.get(temp);
                boxImages[toDiscard].setImageResource(R.drawable.blast);
                AnimationDrawable b = (AnimationDrawable) boxImages[toDiscard].getDrawable();
                b.start();
                valueToDeduct = boxCards[toDiscard].getValue();
                updateScore(-valueToDeduct, mytv);
                toDiscardList.add(toDiscard);
            }
            Handler h2 = new Handler();
            h2.postDelayed(new Runnable() {
                public void run() {
                    for (int m = 0; m < toDiscardList.size(); m++) {
                        boxImages[toDiscardList.get(m)].clearAnimation();
                        boxImages[toDiscardList.get(m)].setImageResource(R.drawable.back);
                        boxImages[toDiscardList.get(m)].setVisibility(View.INVISIBLE);
                        boxIsEmpty[toDiscardList.get(m)] = true;
                        boxCards[toDiscardList.get(m)] = null;
                    }
                    if (suddendeathCount == 0) {
                        endRound();
                    }
                    else if (suddendeathMode) {
                        suddendeathCount--;
                        playerTurn();
                    }
                    else {
                        playerTurn();
                    }
                }
            }, 1000);
        }
    }

    private void computerdraw(int index, int howMany) {
        computerCards.remove(index);
        TextView tv = (TextView)findViewById(R.id.computerScore);
        if (howMany == 2) {
            updateScore(-9, tv);
        }
        else if (howMany == 3) {
            updateScore(-10, tv);
        }

        computerDrawCards(howMany);
    }

    private void computerdeactivate(int index) {
        computerCards.remove(index);
        TextView tv = (TextView)findViewById(R.id.computerScore);
        updateScore(-1, tv);

        for (int i = 0; i < boxCards.length; i++) {
            if (!boxIsEmpty[i]) {
                boxCards[i].deactivate();
            }
        }
        if (suddendeathCount == 0) {
            endRound();
        }
        else if (suddendeathMode) {
            suddendeathCount--;
            playerTurn();
        }
        else {
            playerTurn();
        }
    }

    private void computerdiscardhighest(int index, int howMany) {
        computerCards.remove(index);
        TextView tv = (TextView)findViewById(R.id.computerScore);
        if (howMany == 2) {
            updateScore(-11, tv);
        }
        else if (howMany == 3) {
            updateScore(-12, tv);
        }
        ArrayList<Card> allCards = new ArrayList<Card>();
        for (int i = 0; i < boxCards.length; i++) {
            if (!boxIsEmpty[i]) {
                allCards.add(boxCards[i]);
            }
        }
        Collections.sort(allCards);

        if (allCards.size() == 0) {
            if (suddendeathCount == 0) {
                endRound();
            }
            else if (suddendeathMode) {
                suddendeathCount--;
                playerTurn();
            }
            else {
                playerTurn();
            }
        }
        else{
            final ArrayList<Integer> toDiscardList = new ArrayList<Integer>();
            for (int j = 0; j < howMany; j++) {
                if (allCards.size() > 0) {
                    int temp = -1;
                    for (int k = 0; k < boxCards.length; k++) {
                        if (!boxIsEmpty[k]) {
                            if (boxCards[k].getRank()==allCards.get(allCards.size()-1).getRank()) {
                                temp = k;
                            }
                        }
                    }
                    int toDiscard = temp;
                    boxImages[toDiscard].setImageResource(R.drawable.blast);
                    AnimationDrawable a = (AnimationDrawable) boxImages[toDiscard].getDrawable();
                    a.start();
                    int valueToDeduct = boxCards[toDiscard].getValue();
                    TextView mytv = (TextView) findViewById(R.id.playerScore);
                    updateScore(-valueToDeduct, mytv);
                    toDiscardList.add(toDiscard);
                    allCards.remove(allCards.size()-1);
                }
            }
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    for (int m = 0; m < toDiscardList.size(); m++) {
                        boxImages[toDiscardList.get(m)].clearAnimation();
                        boxImages[toDiscardList.get(m)].setImageResource(R.drawable.back);
                        boxImages[toDiscardList.get(m)].setVisibility(View.INVISIBLE);
                        boxIsEmpty[toDiscardList.get(m)] = true;
                        boxCards[toDiscardList.get(m)] = null;
                    }
                    if (suddendeathCount == 0) {
                        endRound();
                    }
                    else if (suddendeathMode) {
                        suddendeathCount--;
                        playerTurn();
                    }
                    else {
                        playerTurn();
                    }
                }
            }, 1000);
        }

    }

    private void computerrestoration(int index) {
        computerCards.remove(index);
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.gamelayout);
        layout.setBackgroundColor(Color.RED);
        round.setTextColor(Color.parseColor("#00e5ee"));
        TextView playertext = (TextView)findViewById(R.id.playerScoreStaticText);
        playertext.setTextColor(Color.BLACK);
        TextView playertv = (TextView)findViewById(R.id.playerScore);
        playertv.setTextColor(Color.BLACK);
        TextView comptext = (TextView)findViewById(R.id.computerScoreStaticText);
        comptext.setTextColor(Color.BLACK);
        TextView comptv = (TextView)findViewById(R.id.computerScore);
        comptv.setTextColor(Color.BLACK);

        updateScore(-1, comptv);
        winCondition = 0;
        if (suddendeathCount == 0) {
            endRound();
        }
        else if (suddendeathMode) {
            suddendeathCount--;
            playerTurn();
        }
        else {
            playerTurn();
        }
    }

    private void computersuddendeath(int index) {
        computerCards.remove(index);
        TextView tv = (TextView)findViewById(R.id.computerScore);
        updateScore(-13, tv);

        suddendeathMode = true;
        suddendeathCount--;
        playerTurn();
    }

    private void computersabotage(int index) {
        computerCards.remove(index);
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.gamelayout);
        layout.setBackgroundColor(Color.BLACK);
        round.setTextColor(Color.parseColor("#00e5ee"));
        TextView playertext = (TextView)findViewById(R.id.playerScoreStaticText);
        playertext.setTextColor(Color.YELLOW);
        TextView playertv = (TextView)findViewById(R.id.playerScore);
        playertv.setTextColor(Color.YELLOW);
        TextView comptext = (TextView)findViewById(R.id.computerScoreStaticText);
        comptext.setTextColor(Color.YELLOW);
        TextView comptv = (TextView)findViewById(R.id.computerScore);
        comptv.setTextColor(Color.YELLOW);

        updateScore(-13, comptv);
        winCondition = 1;
        if (suddendeathCount == 0) {
            endRound();
        }
        else if (suddendeathMode) {
            suddendeathCount--;
            playerTurn();
        }
        else {
            playerTurn();
        }
    }
}
