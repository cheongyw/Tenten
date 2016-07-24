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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;

public class GameActivity4P extends AppCompatActivity implements View.OnClickListener {

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
    private ArrayList<Card> com1_Cards;
    private ArrayList<Card> com2_Cards;
    private ArrayList<Card> com3_Cards;
    private ArrayList<Card>[] comCards;
    private boolean suddendeathMode;
    private int suddendeathCount;
    private int winCondition;
    private int currentRound;
    private int playerPoints;
    private int com1Points;
    private int com2Points;
    private int com3Points;

    private final static Random random = new Random();

    private ArrayList<Integer> drawnCards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_4p);

        currentRound = 1;
        playerPoints = 0;
        com1Points = 0;
        com2Points = 0;
        com3Points = 0;
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
                TextView showMessage = (TextView)findViewById(R.id.showMessage4P);
                showMessage.setText(null);
                drawButton.setClickable(false);
                executeAbility(temp2.get(id));
            }
        });
        chooseAbility.show();
        chooseAbility.setCancelable(true);
    }

    private void startRound() {

        round = (TextView) findViewById(R.id.round_4P);
        useAbility = (Button) findViewById(R.id.useAbility4P);
        drawButton = (ImageButton) findViewById(R.id.drawButton4P);
        boxImage0 = (ImageView) findViewById(R.id.boxImage0_4P);
        boxImage1 = (ImageView) findViewById(R.id.boxImage1_4P);
        boxImage2 = (ImageView) findViewById(R.id.boxImage2_4P);
        boxImage3 = (ImageView) findViewById(R.id.boxImage3_4P);
        boxImage4 = (ImageView) findViewById(R.id.boxImage4_4P);
        boxImage5 = (ImageView) findViewById(R.id.boxImage5_4P);
        continueButton = (Button) findViewById(R.id.continue_button_4P);

        drawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawCard(1);
            }
        });
        useAbility.setOnClickListener(this);

        boxImages = new ImageView[]{boxImage0, boxImage1, boxImage2, boxImage3, boxImage4, boxImage5};
        boxIsEmpty = new boolean[6];
        boxCards = new Card[6];
        com1_Cards = new ArrayList<>();
        com2_Cards = new ArrayList<>();
        com3_Cards = new ArrayList<>();
        comCards = new ArrayList[3];
        comCards[0] = com1_Cards;
        comCards[1] = com2_Cards;
        comCards[2] = com3_Cards;
        drawnCards = new ArrayList<>();

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
        suddendeathCount = 4;
        winCondition = 0;
        drawButton.setImageResource(R.drawable.back);
        drawButton.setVisibility(View.VISIBLE);
        continueButton.setClickable(false);
        continueButton.setVisibility(View.INVISIBLE);

        for (int i = 4; i < 6; i++) {
            boxIsEmpty[i] = true;
            boxCards[i] = null;
        }
        TextView tv = (TextView)findViewById(R.id.playerScore4P);
        tv.setText("0");
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
        TextView comptv1 = (TextView)findViewById(R.id.com1Score);
        comptv1.setText("0");
        TextView comptv2 = (TextView)findViewById(R.id.com2Score);
        comptv2.setText("0");
        TextView comptv3 = (TextView)findViewById(R.id.com3Score);
        comptv3.setText("0");
        for (int which = 0; which < 3; which++) {
            for (int k = 0; k < 4; k++) {
                int value = random.nextInt(52);
                while (drawnCards.contains(value)) {
                    value = random.nextInt(52);
                }
                Card card = new Card(value);
                ArrayList<Card> computerCards = comCards[which];
                computerCards.add(card);
                TextView comptv;
                if (which == 0) {
                    comptv = (TextView) findViewById(R.id.com1Score);
                }
                else if (which == 1) {
                    comptv = (TextView) findViewById(R.id.com2Score);
                }
                else {
                    comptv = (TextView) findViewById(R.id.com3Score);
                }
                updateScore(card.getValue(), comptv);
                drawnCards.add(value);
            }
        }
        playerTurn();
    }

    private void drawCard(int howMany) {
        TextView showMessage = (TextView)findViewById(R.id.showMessage4P);
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
            TextView tv = (TextView)findViewById(R.id.playerScore4P);
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
                        computerMoves(0);
                    }
                    else {
                        computerMoves(0);
                    }
                }
            }
        }
        else if (boxIsEmpty[1]) {
            boxImage1.setImageResource(card.getImage());
            boxImage1.setVisibility(View.VISIBLE);
            boxIsEmpty[1] = false;
            boxCards[1] = card;
            TextView tv = (TextView)findViewById(R.id.playerScore4P);
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
                        computerMoves(0);
                    }
                    else {
                        computerMoves(0);
                    }
                }
            }
        }
        else if (boxIsEmpty[2]) {
            boxImage2.setImageResource(card.getImage());
            boxImage2.setVisibility(View.VISIBLE);
            boxIsEmpty[2] = false;
            boxCards[2] = card;
            TextView tv = (TextView)findViewById(R.id.playerScore4P);
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
                        computerMoves(0);
                    }
                    else {
                        computerMoves(0);
                    }
                }
            }
        }
        else if (boxIsEmpty[3]) {
            boxImage3.setImageResource(card.getImage());
            boxImage3.setVisibility(View.VISIBLE);
            boxIsEmpty[3] = false;
            boxCards[3] = card;
            TextView tv = (TextView)findViewById(R.id.playerScore4P);
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
                        computerMoves(0);
                    }
                    else {
                        computerMoves(0);
                    }
                }
            }
        }
        else if (boxIsEmpty[4]) {
            boxImage4.setImageResource(card.getImage());
            boxImage4.setVisibility(View.VISIBLE);
            boxIsEmpty[4] = false;
            boxCards[4] = card;
            TextView tv = (TextView)findViewById(R.id.playerScore4P);
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
                        computerMoves(0);
                    }
                    else {
                        computerMoves(0);
                    }
                }
            }
        }
        else if (boxIsEmpty[5]){
            boxImage5.setImageResource(card.getImage());
            boxImage5.setVisibility(View.VISIBLE);
            boxIsEmpty[5] = false;
            boxCards[5] = card;
            TextView tv = (TextView)findViewById(R.id.playerScore4P);
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
                        computerMoves(0);
                    }
                    else {
                        computerMoves(0);
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
        final TextView showMessage = (TextView)findViewById(R.id.showMessage4P);
        showMessage.setText("You have the maximum number of cards. Choose one to discard.");
        final ImageView drawnCardImage = (ImageView)findViewById(R.id.drawnCardImage4P);
        drawnCardImage.setImageResource(c.getImage());
        drawnCardImage.setVisibility(View.VISIBLE);
        final TextView tv = (TextView)findViewById(R.id.playerScore4P);
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
                            computerMoves(0);
                        }
                        else {
                            computerMoves(0);
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
                    boxImages[id].setVisibility(View.VISIBLE);
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
                                computerMoves(0);
                            }
                            else {
                                computerMoves(0);
                            }
                        }
                    }
                }
            });
        }
    }

    private void computerMoves(int w) {
        drawButton.setClickable(false);
        useAbility.setClickable(false);
        useAbility.setTextColor(Color.parseColor("#60C0C0C0"));
        useAbility.setBackgroundColor(Color.parseColor("#60C0C0C0"));
        final int which = w;

        boolean hasAbilityTemp = false;
        boolean hasAbilityForSabotageTemp = false;
        int indexTemp = -1;
        int indexForSabotageTemp = -1;
        ArrayList<Card> computerCards = comCards[which];

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
        final TextView showMessage = (TextView) findViewById(R.id.showMessage4P);
        if (which == 0) {
            showMessage.setText("COM 1's turn");
        }
        else if (which == 1) {
            showMessage.setText("COM 2's turn");
        }
        else {
            showMessage.setText("COM 3's turn");
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                showMessage.setText(null);
                if (winCondition == 0 && hasAbility) {
                    computerExecuteAbility(index, which);
                }
                else if (winCondition == 1 && hasAbilityForSabotage) {
                    computerExecuteAbility(indexForSabotage, which);
                }
                else {
                    computerDrawCards(1, which);
                }
            }
        }, 2000);
    }

    private void computerDrawCards(int howMany, int which) {
        int value = random.nextInt(52);
        while (drawnCards.contains(value)) {
            value = random.nextInt(52);
        }
        Card card = new Card(value);
        ArrayList<Card> computerCards = comCards[which];
        if (computerCards.size() == 6) {
            replaceComputerCard(card, which);
        }else{
            computerCards.add(card);
            TextView tv;
            if (which == 0) {
                 tv = (TextView) findViewById(R.id.com1Score);
            }
            else if (which == 1) {
                tv = (TextView) findViewById(R.id.com2Score);
            }
            else {
                tv = (TextView) findViewById(R.id.com3Score);
            }
            updateScore(card.getValue(), tv);
        }
        drawnCards.add(value);
        if (drawnCards.size() == 52) {
            endRound();
        }
        else {
            if (howMany != 1) {
                howMany--;
                computerDrawCards(howMany, which);
            }
            else {
                if (suddendeathCount == 0) {
                    endRound();
                }
                else {
                    if (suddendeathMode) {
                        suddendeathCount--;
                    }
                    if (which == 2) {
                        playerTurn();
                    }
                    else {
                        which++;
                        computerMoves(which);
                    }
                }
            }
        }
    }

    private void playerTurn() {
        TextView showMessage = (TextView)findViewById(R.id.showMessage4P);
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

    private void replaceComputerCard(Card card, int which) {
        int k = card.getValue();
        int index = -1;
        ArrayList<Card> computerCards = comCards[which];
        TextView tv;
        if (which == 0) {
            tv = (TextView) findViewById(R.id.com1Score);
        }
        else if (which == 1) {
            tv = (TextView) findViewById(R.id.com2Score);
        }
        else {
            tv = (TextView) findViewById(R.id.com3Score);
        }
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
        TextView playertv = (TextView)findViewById(R.id.playerScore4P);
        int playerScore = Integer.parseInt(playertv.getText().toString());
        TextView com1_tv = (TextView)findViewById(R.id.com1Score);
        int com1_Score = Integer.parseInt(com1_tv.getText().toString());
        TextView com2_tv = (TextView)findViewById(R.id.com2Score);
        int com2_Score = Integer.parseInt(com2_tv.getText().toString());
        TextView com3_tv = (TextView)findViewById(R.id.com3Score);
        int com3_Score = Integer.parseInt(com3_tv.getText().toString());
        ArrayList<Integer> scores = new ArrayList<>();
        scores.add(playerScore);scores.add(com1_Score);scores.add(com2_Score);scores.add(com3_Score);
        Collections.sort(scores);
        playerPoints+=scores.indexOf(playerScore);
        com1Points+=scores.indexOf(com1_Score);
        com2Points+=scores.indexOf(com2_Score);
        com3Points+=scores.indexOf(com3_Score);

        AlertDialog.Builder outcome = new AlertDialog.Builder(this);
        outcome.setTitle("Points after this round");
        String message = "You: " +String.valueOf(playerPoints)+"\n"+"\n"+"Com 1: "+String.valueOf(com1Points)+"\n"+"\n"+"Com 2: "+String.valueOf(com2Points)+"\n"+"\n"+"Com 3: "+String.valueOf(com3Points);
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
                    setContentView(R.layout.activity_game_4p);
                    startRound();
                }
            }
        });

    }

    private void endGame() {
        Hashtable<String, Integer> table = new Hashtable<>();
        table.put("COM 1",com1Points);table.put("COM 2",com2Points);table.put("COM 3",com3Points);table.put("You",playerPoints);
        ArrayList<String> sortedKeys = sortValue(table);
        AlertDialog.Builder outcome = new AlertDialog.Builder(this);
        if (winCondition == 0) {
            if (sortedKeys.get(3)=="You") {
                outcome.setTitle("You win!");
            }
            else {
                outcome.setTitle("You lose!");
            }
            String message = sortedKeys.get(3)+"\n"+"\n"+sortedKeys.get(2)+"\n"+"\n"+sortedKeys.get(1)+"\n"+"\n"+sortedKeys.get(0);
            outcome.setMessage(message);
        }
        else if (winCondition == 1) {
            if (sortedKeys.get(0)=="You") {
                outcome.setTitle("You win!");
            }
            else {
                outcome.setTitle("You lose!");
            }
            String message = sortedKeys.get(0)+"\n"+"\n"+sortedKeys.get(1)+"\n"+"\n"+sortedKeys.get(2)+"\n"+"\n"+sortedKeys.get(3);
            outcome.setMessage(message);
        }
        outcome.setCancelable(true);
        outcome.show();
    }

    public static ArrayList<String> sortValue(Hashtable<String,Integer> t) {
        ArrayList<Map.Entry<String, Integer>> l = new ArrayList(t.entrySet());
        Collections.sort(l, new Comparator<Map.Entry<String, Integer>>(){

            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }});

        ArrayList<String> sortedKeys = new ArrayList<>();
        for (int i = 0; i < l.size(); i++) {
            sortedKeys.add(l.get(i).getKey());
        }
        return sortedKeys;
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
            final TextView showMessage = (TextView) findViewById(R.id.showMessage4P);
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
        final ImageView blast1 = (ImageView) findViewById(R.id.blastCom1);
        final ImageView blast2 = (ImageView) findViewById(R.id.blastCom2);
        final ImageView blast3 = (ImageView) findViewById(R.id.blastCom3);
        final AnimationDrawable a = (AnimationDrawable) blast1.getBackground();
        final AnimationDrawable b = (AnimationDrawable) blast2.getBackground();
        final AnimationDrawable c = (AnimationDrawable) blast3.getBackground();
        blast1.setVisibility(View.VISIBLE);
        blast2.setVisibility(View.VISIBLE);
        blast3.setVisibility(View.VISIBLE);
        a.start();
        b.start();
        c.start();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                blast1.clearAnimation();
                blast2.clearAnimation();
                blast3.clearAnimation();
                blast1.setVisibility(View.INVISIBLE);
                blast2.setVisibility(View.INVISIBLE);
                blast3.setVisibility(View.INVISIBLE);
            }
        }, 1000);

        for (int which = 0; which < 3; which++) {
            ArrayList<Card> computerCards = comCards[which];
            TextView tv;
            if (which == 0) {
                tv = (TextView) findViewById(R.id.com1Score);
            }
            else if (which == 1) {
                tv = (TextView) findViewById(R.id.com2Score);
            }
            else {
                tv = (TextView) findViewById(R.id.com3Score);
            }
            if (computerCards.size()>0) {
                int index = random.nextInt(computerCards.size());
                int valueToDeduct = computerCards.get(index).getValue();
                computerCards.remove(index);
                updateScore(-valueToDeduct, tv);
                if (computerCards.size()>0) {
                    index = random.nextInt(computerCards.size());
                    valueToDeduct = computerCards.get(index).getValue();
                    computerCards.remove(index);
                    updateScore(-valueToDeduct, tv);
                }
            }
        }

        TextView tv = (TextView)findViewById(R.id.playerScore4P);
        updateScore(-8, tv);
        boxImages[boxNumber].setVisibility(View.INVISIBLE);
        boxIsEmpty[boxNumber] = true;
        boxCards[boxNumber] = null;
        if (suddendeathCount == 0) {
            endRound();
        }
        else if (suddendeathMode) {
            suddendeathCount--;
            computerMoves(0);
        }
        else {
            computerMoves(0);
        }
    }

    private void draw(int boxNumber, int howMany) {
        TextView tv = (TextView)findViewById(R.id.playerScore4P);
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
        for (int which = 0; which < 3; which++) {
            ArrayList<Card> computerCards = comCards[which];
            if (computerCards.size()>0) {
                for (int i = 0; i < computerCards.size(); i++) {
                    computerCards.get(i).deactivate();
                }
            }
        }

        TextView tv = (TextView)findViewById(R.id.playerScore4P);
        updateScore(-1, tv);
        boxImages[boxNumber].setVisibility(View.INVISIBLE);
        boxIsEmpty[boxNumber] = true;
        boxCards[boxNumber] = null;
        if (suddendeathCount == 0) {
            endRound();
        }
        else if (suddendeathMode) {
            suddendeathCount--;
            computerMoves(0);
        }
        else {
            computerMoves(0);
        }
    }

    private void discardhighest(int boxNumber, int howMany) {
        final ImageView blast1 = (ImageView) findViewById(R.id.blastCom1);
        final ImageView blast2 = (ImageView) findViewById(R.id.blastCom2);
        final ImageView blast3 = (ImageView) findViewById(R.id.blastCom3);
        final AnimationDrawable a = (AnimationDrawable) blast1.getBackground();
        final AnimationDrawable b = (AnimationDrawable) blast2.getBackground();
        final AnimationDrawable c = (AnimationDrawable) blast3.getBackground();
        blast1.setVisibility(View.VISIBLE);
        blast2.setVisibility(View.VISIBLE);
        blast3.setVisibility(View.VISIBLE);
        a.start();
        b.start();
        c.start();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                blast1.clearAnimation();
                blast2.clearAnimation();
                blast3.clearAnimation();
                blast1.setVisibility(View.INVISIBLE);
                blast2.setVisibility(View.INVISIBLE);
                blast3.setVisibility(View.INVISIBLE);
            }
        }, 1000);

        for (int which = 0; which < 3; which++) {
            ArrayList<Card> computerCards = comCards[which];
            TextView tv;
            if (which == 0) {
                tv = (TextView) findViewById(R.id.com1Score);
            } else if (which == 1) {
                tv = (TextView) findViewById(R.id.com2Score);
            } else {
                tv = (TextView) findViewById(R.id.com3Score);
            }
            Collections.sort(computerCards);
            int valueToDeduct;
            for (int i = 0; i < howMany; i++) {
                if (computerCards.size()>0) {
                    valueToDeduct = computerCards.get(computerCards.size()-1).getValue();
                    computerCards.remove(computerCards.size()-1);
                    updateScore(-valueToDeduct, tv);
                }
            }
        }

        TextView mytv = (TextView)findViewById(R.id.playerScore4P);
        if (howMany == 2) {
            updateScore(-11, mytv);
        }
        else if (howMany == 3) {
            updateScore(-12, mytv);
        }
        boxImages[boxNumber].setVisibility(View.INVISIBLE);
        boxIsEmpty[boxNumber] = true;
        boxCards[boxNumber] = null;
        if (suddendeathCount == 0) {
            endRound();
        }
        else if (suddendeathMode) {
            suddendeathCount--;
            computerMoves(0);
        }
        else {
            computerMoves(0);
        }
    }

    private void restoration(int boxNumber) {
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.gamelayout4P);
        layout.setBackgroundColor(Color.RED);
        round.setTextColor(Color.parseColor("#00e5ee"));
        TextView playertext = (TextView)findViewById(R.id.playerText4P);
        playertext.setTextColor(Color.BLACK);
        TextView playertv = (TextView)findViewById(R.id.playerScore4P);
        playertv.setTextColor(Color.BLACK);
        TextView com1text = (TextView)findViewById(R.id.com1Text);
        com1text.setTextColor(Color.BLACK);
        TextView com1tv = (TextView)findViewById(R.id.com1Score);
        com1tv.setTextColor(Color.BLACK);
        TextView com2text = (TextView)findViewById(R.id.com2Text);
        com2text.setTextColor(Color.BLACK);
        TextView com2tv = (TextView)findViewById(R.id.com2Score);
        com2tv.setTextColor(Color.BLACK);
        TextView com3text = (TextView)findViewById(R.id.com3Text);
        com3text.setTextColor(Color.BLACK);
        TextView com3tv = (TextView)findViewById(R.id.com3Score);
        com3tv.setTextColor(Color.BLACK);

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
            computerMoves(0);
        }
        else {
            computerMoves(0);
        }
    }

    private void suddendeath(int boxNumber) {
        suddendeathMode = true;
        suddendeathCount--;

        TextView tv = (TextView)findViewById(R.id.playerScore4P);
        updateScore(-13, tv);
        boxImages[boxNumber].setVisibility(View.INVISIBLE);
        boxIsEmpty[boxNumber] = true;
        boxCards[boxNumber] = null;
        computerMoves(0);
    }

    private void sabotage(int boxNumber) {
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.gamelayout4P);
        layout.setBackgroundColor(Color.BLACK);
        round.setTextColor(Color.parseColor("#00e5ee"));
        TextView playertext = (TextView)findViewById(R.id.playerText4P);
        playertext.setTextColor(Color.YELLOW);
        TextView playertv = (TextView)findViewById(R.id.playerScore4P);
        playertv.setTextColor(Color.YELLOW);
        TextView com1text = (TextView)findViewById(R.id.com1Text);
        com1text.setTextColor(Color.YELLOW);
        TextView com1tv = (TextView)findViewById(R.id.com1Score);
        com1tv.setTextColor(Color.YELLOW);
        TextView com2text = (TextView)findViewById(R.id.com2Text);
        com2text.setTextColor(Color.YELLOW);
        TextView com2tv = (TextView)findViewById(R.id.com2Score);
        com2tv.setTextColor(Color.YELLOW);
        TextView com3text = (TextView)findViewById(R.id.com3Text);
        com3text.setTextColor(Color.YELLOW);
        TextView com3tv = (TextView)findViewById(R.id.com3Score);
        com3tv.setTextColor(Color.YELLOW);

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
            computerMoves(0);
        }
        else {
            computerMoves(0);
        }
    }

    /* -----------Methods for card abilities (Computer) ------------------------- */

    private void computerExecuteAbility(int ind, int w) {
        final int index = ind;
        final int which = w;
        ArrayList<Card> computerCards = comCards[which];
        final Card card = computerCards.get(index);
        if (card.getAbility() == "Restoration: Highest hand wins") {
            Intent intent = new Intent(this, ExplosiveActivity.class);
            intent.putExtra("Message", "Restoration" + "\n" + "Highest hand wins!");
            startActivity(intent);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    computerrestoration(index,which);
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
                    computersuddendeath(index,which);
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
                    computersabotage(index,which);
                }
            }, 6000);
        }
        else {
            final TextView showMessage = (TextView) findViewById(R.id.showMessage4P);
            if (which == 0) {
                showMessage.setText("COM 1 used " + card.toString() + "!");
            }
            else if (which == 1) {
                showMessage.setText("COM 2 used " + card.toString() + "!");
            }
            else {
                showMessage.setText("COM 3 used " + card.toString() + "!");
            }
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    showMessage.setText(null);
                    if (card.getAbility() == "Opponent discards 2 random cards") {
                        computerdiscard2(index, which);
                    }
                    else if (card.getAbility() == "Draw 2 cards") {
                        computerdraw(index,2,which);
                    }
                    else if (card.getAbility() == "Draw 3 cards") {
                        computerdraw(index,3,which);
                    }
                    else if (card.getAbility() == "Deactivate opponent's cards") {
                        computerdeactivate(index,which);
                    }
                    else if (card.getAbility() == "Opponent discards 2 highest cards") {
                        computerdiscardhighest(index,2,which);
                    }
                    else if (card.getAbility() == "Opponent discards 3 highest cards") {
                        computerdiscardhighest(index,3,which);
                    }
                }
            }, 3000);
        }
    }

    private void computerdiscard2(int index, int w) {
        final int which = w;
        ArrayList<Card> computerCards = comCards[which];
        computerCards.remove(index);
        TextView tv;
        final ImageView blast1;
        final ImageView blast2;
        if (which == 0) {
            tv = (TextView)findViewById(R.id.com1Score);
            blast1 = (ImageView) findViewById(R.id.blastCom2);
            blast2 = (ImageView) findViewById(R.id.blastCom3);
        }
        else if (which == 1) {
            tv = (TextView)findViewById(R.id.com2Score);
            blast1 = (ImageView) findViewById(R.id.blastCom1);
            blast2 = (ImageView) findViewById(R.id.blastCom3);
        }
        else {
            tv = (TextView)findViewById(R.id.com3Score);
            blast1 = (ImageView) findViewById(R.id.blastCom1);
            blast2 = (ImageView) findViewById(R.id.blastCom2);
        }
        updateScore(-8, tv);
        final AnimationDrawable a = (AnimationDrawable) blast1.getBackground();
        final AnimationDrawable b = (AnimationDrawable) blast2.getBackground();
        blast1.setVisibility(View.VISIBLE);
        blast2.setVisibility(View.VISIBLE);
        a.start();
        b.start();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                blast1.clearAnimation();
                blast2.clearAnimation();
                blast1.setVisibility(View.INVISIBLE);
                blast2.setVisibility(View.INVISIBLE);
            }
        }, 1000);

        for (int other = 0; other < 3; other++) {
            if (other!=which) {
                ArrayList<Card> tempComputerCards = comCards[other];
                TextView temptv;
                if (other == 0) {
                    temptv = (TextView) findViewById(R.id.com1Score);
                }
                else if (other == 1) {
                    temptv = (TextView) findViewById(R.id.com2Score);
                }
                else {
                    temptv = (TextView) findViewById(R.id.com3Score);
                }
                if (tempComputerCards.size()>0) {
                    int tempIndex = random.nextInt(tempComputerCards.size());
                    int valueToDeduct = tempComputerCards.get(tempIndex).getValue();
                    tempComputerCards.remove(tempIndex);
                    updateScore(-valueToDeduct, temptv);
                    if (tempComputerCards.size()>0) {
                        tempIndex = random.nextInt(tempComputerCards.size());
                        valueToDeduct = tempComputerCards.get(tempIndex).getValue();
                        tempComputerCards.remove(tempIndex);
                        updateScore(-valueToDeduct, temptv);
                    }
                }
            }
        }

        ArrayList<Integer> canBeDiscarded = new ArrayList<Integer>();
        for (int i = 0; i < boxCards.length; i++) {
            if (!boxIsEmpty[i]) {
                canBeDiscarded.add(i);
            }
        }

        if (canBeDiscarded.size() == 0) {
            Handler h = new Handler();
            h.postDelayed(new Runnable() {
                public void run() {
                    if (suddendeathCount == 0) {
                        endRound();
                    }
                    else {
                        if (suddendeathMode) {
                            suddendeathCount--;
                        }
                        if (which == 2) {
                            playerTurn();
                        }
                        else {
                            int temp = which;
                            temp++;
                            computerMoves(temp);
                        }
                    }
                }
            }, 1000);
        }
        else {
            final ArrayList<Integer> toDiscardList = new ArrayList<Integer>();
            int temp = random.nextInt(canBeDiscarded.size());
            int toDiscard = canBeDiscarded.get(temp);
            boxImages[toDiscard].setImageResource(R.drawable.blast);
            final AnimationDrawable c = (AnimationDrawable) boxImages[toDiscard].getDrawable();
            c.start();
            int valueToDeduct = boxCards[toDiscard].getValue();
            TextView mytv = (TextView) findViewById(R.id.playerScore4P);
            updateScore(-valueToDeduct, mytv);
            toDiscardList.add(toDiscard);
            canBeDiscarded.remove(temp);
            if (canBeDiscarded.size() > 0) {
                temp = random.nextInt(canBeDiscarded.size());
                toDiscard = canBeDiscarded.get(temp);
                boxImages[toDiscard].setImageResource(R.drawable.blast);
                AnimationDrawable d = (AnimationDrawable) boxImages[toDiscard].getDrawable();
                d.start();
                valueToDeduct = boxCards[toDiscard].getValue();
                updateScore(-valueToDeduct, mytv);
                toDiscardList.add(toDiscard);
            }
            Handler h2 = new Handler();
            h2.postDelayed(new Runnable() {
                public void run() {
                    for (int m = 0; m < toDiscardList.size(); m++) {
                        boxImages[toDiscardList.get(m)].clearAnimation();
                        boxImages[toDiscardList.get(m)].setImageResource(R.drawable.empty);
                        boxImages[toDiscardList.get(m)].setVisibility(View.INVISIBLE);
                        boxIsEmpty[toDiscardList.get(m)] = true;
                        boxCards[toDiscardList.get(m)] = null;
                    }
                    if (suddendeathCount == 0) {
                        endRound();
                    }
                    else {
                        if (suddendeathMode) {
                            suddendeathCount--;
                        }
                        if (which == 2) {
                            playerTurn();
                        }
                        else {
                            int temp = which;
                            temp++;
                            computerMoves(temp);
                        }
                    }
                }
            }, 1000);
        }
    }

    private void computerdraw(int index, int howMany, int which) {
        ArrayList<Card> computerCards = comCards[which];
        computerCards.remove(index);
        TextView tv;
        if (which == 0) {
            tv = (TextView) findViewById(R.id.com1Score);
        } else if (which == 1) {
            tv = (TextView) findViewById(R.id.com2Score);
        } else {
            tv = (TextView) findViewById(R.id.com3Score);
        }
        if (howMany == 2) {
            updateScore(-9, tv);
        } else if (howMany == 3) {
            updateScore(-10, tv);
        }
        computerDrawCards(howMany, which);
    }

    private void computerdeactivate(int index, int which) {
        ArrayList<Card> computerCards = comCards[which];
        computerCards.remove(index);
        TextView tv;
        if (which == 0) {
            tv = (TextView) findViewById(R.id.com1Score);
        }
        else if (which == 1) {
            tv = (TextView) findViewById(R.id.com2Score);
        }
        else{
            tv = (TextView) findViewById(R.id.com3Score);
        }
        updateScore(-1, tv);

        for (int i = 0; i < boxCards.length; i++) {
            if (!boxIsEmpty[i]) {
                boxCards[i].deactivate();
            }
        }
        for (int other = 0; other < 3; other++) {
            if (other!=which) {
                ArrayList<Card> tempComputerCards = comCards[other];
                if (tempComputerCards.size()>0) {
                    for (int i = 0; i < tempComputerCards.size(); i++) {
                        tempComputerCards.get(i).deactivate();
                    }
                }
            }
        }
        if (suddendeathCount == 0) {
            endRound();
        }
        else {
            if (suddendeathMode) {
                suddendeathCount--;
            }
            if (which == 2) {
                playerTurn();
            }
            else {
                int temp = which;
                temp++;
                computerMoves(temp);
            }
        }
    }

    private void computerdiscardhighest(int index, int howMany, int w) {
        final int which = w;
        ArrayList<Card> computerCards = comCards[which];
        computerCards.remove(index);
        TextView tv;
        final ImageView blast1;
        final ImageView blast2;
        if (which == 0) {
            tv = (TextView)findViewById(R.id.com1Score);
            blast1 = (ImageView) findViewById(R.id.blastCom2);
            blast2 = (ImageView) findViewById(R.id.blastCom3);
        }
        else if (which == 1) {
            tv = (TextView)findViewById(R.id.com2Score);
            blast1 = (ImageView) findViewById(R.id.blastCom1);
            blast2 = (ImageView) findViewById(R.id.blastCom3);
        }
        else {
            tv = (TextView)findViewById(R.id.com3Score);
            blast1 = (ImageView) findViewById(R.id.blastCom1);
            blast2 = (ImageView) findViewById(R.id.blastCom2);
        }
        if (howMany == 2) {
            updateScore(-11, tv);
        }
        else if (howMany == 3) {
            updateScore(-12, tv);
        }
        final AnimationDrawable a = (AnimationDrawable) blast1.getBackground();
        final AnimationDrawable b = (AnimationDrawable) blast2.getBackground();
        blast1.setVisibility(View.VISIBLE);
        blast2.setVisibility(View.VISIBLE);
        a.start();
        b.start();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                blast1.clearAnimation();
                blast2.clearAnimation();
                blast1.setVisibility(View.INVISIBLE);
                blast2.setVisibility(View.INVISIBLE);
            }
        }, 1000);

        for (int other = 0; other < 3; other++) {
            if (other!=which) {
                ArrayList<Card> tempComputerCards = comCards[other];
                TextView temptv;
                if (other == 0) {
                    temptv = (TextView) findViewById(R.id.com1Score);
                } else if (other == 1) {
                    temptv = (TextView) findViewById(R.id.com2Score);
                } else {
                    temptv = (TextView) findViewById(R.id.com3Score);
                }
                Collections.sort(tempComputerCards);
                int valueToDeduct;
                for (int i = 0; i < howMany; i++) {
                    if (tempComputerCards.size()>0) {
                        valueToDeduct = tempComputerCards.get(tempComputerCards.size()-1).getValue();
                        tempComputerCards.remove(tempComputerCards.size()-1);
                        updateScore(-valueToDeduct, temptv);
                    }
                }
            }
        }

        ArrayList<Card> allCards = new ArrayList<Card>();
        for (int i = 0; i < boxCards.length; i++) {
            if (!boxIsEmpty[i]) {
                allCards.add(boxCards[i]);
            }
        }
        Collections.sort(allCards);

        if (allCards.size() == 0) {
            Handler h = new Handler();
            h.postDelayed(new Runnable() {
                public void run() {
                    if (suddendeathCount == 0) {
                        endRound();
                    }
                    else {
                        if (suddendeathMode) {
                            suddendeathCount--;
                        }
                        if (which == 2) {
                            playerTurn();
                        }
                        else {
                            int temp = which;
                            temp++;
                            computerMoves(temp);
                        }
                    }
                }
            }, 1000);
        }
        else {
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
                    AnimationDrawable c = (AnimationDrawable) boxImages[toDiscard].getDrawable();
                    c.start();
                    int valueToDeduct = boxCards[toDiscard].getValue();
                    TextView mytv = (TextView) findViewById(R.id.playerScore4P);
                    updateScore(-valueToDeduct, mytv);
                    toDiscardList.add(toDiscard);
                    allCards.remove(allCards.size()-1);
                }
            }
            Handler h2 = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    for (int m = 0; m < toDiscardList.size(); m++) {
                        boxImages[toDiscardList.get(m)].clearAnimation();
                        boxImages[toDiscardList.get(m)].setImageResource(R.drawable.empty);
                        boxImages[toDiscardList.get(m)].setVisibility(View.INVISIBLE);
                        boxIsEmpty[toDiscardList.get(m)] = true;
                        boxCards[toDiscardList.get(m)] = null;
                    }
                    if (suddendeathCount == 0) {
                        endRound();
                    }
                    else {
                        if (suddendeathMode) {
                            suddendeathCount--;
                        }
                        if (which == 2) {
                            playerTurn();
                        }
                        else {
                            int temp = which;
                            temp++;
                            computerMoves(temp);
                        }
                    }
                }
            }, 1000);
        }
    }

    private void computerrestoration(int index, int which) {
        ArrayList<Card> computerCards = comCards[which];
        computerCards.remove(index);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.gamelayout4P);
        layout.setBackgroundColor(Color.RED);
        round.setTextColor(Color.parseColor("#00e5ee"));
        TextView playertext = (TextView)findViewById(R.id.playerText4P);
        playertext.setTextColor(Color.BLACK);
        TextView playertv = (TextView)findViewById(R.id.playerScore4P);
        playertv.setTextColor(Color.BLACK);
        TextView com1text = (TextView)findViewById(R.id.com1Text);
        com1text.setTextColor(Color.BLACK);
        TextView com1tv = (TextView)findViewById(R.id.com1Score);
        com1tv.setTextColor(Color.BLACK);
        TextView com2text = (TextView)findViewById(R.id.com2Text);
        com2text.setTextColor(Color.BLACK);
        TextView com2tv = (TextView)findViewById(R.id.com2Score);
        com2tv.setTextColor(Color.BLACK);
        TextView com3text = (TextView)findViewById(R.id.com3Text);
        com3text.setTextColor(Color.BLACK);
        TextView com3tv = (TextView)findViewById(R.id.com3Score);
        com3tv.setTextColor(Color.BLACK);

        winCondition = 0;
        if (which == 0) {
            updateScore(-1, com1tv);
        }
        else if (which == 1) {
            updateScore(-1, com2tv);
        }
        else{
            updateScore(-1, com3tv);
        }
        if (suddendeathCount == 0) {
            endRound();
        }
        else {
            if (suddendeathMode) {
                suddendeathCount--;
            }
            if (which == 2) {
                playerTurn();
            }
            else {
                int temp = which;
                temp++;
                computerMoves(temp);
            }
        }
    }

    private void computersuddendeath(int index, int which) {
        ArrayList<Card> computerCards = comCards[which];
        computerCards.remove(index);
        TextView tv;
        if (which == 0) {
            tv = (TextView) findViewById(R.id.com1Score);
        }
        else if (which == 1) {
            tv = (TextView) findViewById(R.id.com2Score);
        }
        else{
            tv = (TextView) findViewById(R.id.com3Score);
        }
        updateScore(-13, tv);

        suddendeathMode = true;
        suddendeathCount--;
        if (which == 2) {
            playerTurn();
        }
        else {
            int temp = which;
            temp++;
            computerMoves(temp);
        }
    }

    private void computersabotage(int index, int which) {
        ArrayList<Card> computerCards = comCards[which];
        computerCards.remove(index);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.gamelayout4P);
        layout.setBackgroundColor(Color.BLACK);
        round.setTextColor(Color.parseColor("#00e5ee"));
        TextView playertext = (TextView)findViewById(R.id.playerText4P);
        playertext.setTextColor(Color.YELLOW);
        TextView playertv = (TextView)findViewById(R.id.playerScore4P);
        playertv.setTextColor(Color.YELLOW);
        TextView com1text = (TextView)findViewById(R.id.com1Text);
        com1text.setTextColor(Color.YELLOW);
        TextView com1tv = (TextView)findViewById(R.id.com1Score);
        com1tv.setTextColor(Color.YELLOW);
        TextView com2text = (TextView)findViewById(R.id.com2Text);
        com2text.setTextColor(Color.YELLOW);
        TextView com2tv = (TextView)findViewById(R.id.com2Score);
        com2tv.setTextColor(Color.YELLOW);
        TextView com3text = (TextView)findViewById(R.id.com3Text);
        com3text.setTextColor(Color.YELLOW);
        TextView com3tv = (TextView)findViewById(R.id.com3Score);
        com3tv.setTextColor(Color.YELLOW);

        winCondition = 1;
        if (which == 0) {
            updateScore(-13, com1tv);
        }
        else if (which == 1) {
            updateScore(-13, com2tv);
        }
        else{
            updateScore(-13, com3tv);
        }
        if (suddendeathCount == 0) {
            endRound();
        }
        else {
            if (suddendeathMode) {
                suddendeathCount--;
            }
            if (which == 2) {
                playerTurn();
            }
            else {
                int temp = which;
                temp++;
                computerMoves(temp);
            }
        }
    }
}
