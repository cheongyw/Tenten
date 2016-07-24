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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Collections;
import java.util.Random;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiGameActivity4P extends AppCompatActivity implements View.OnClickListener {

    // UI components
    private TextView com1Text;
    private TextView com2Text;
    private TextView com3Text;
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
    private ArrayList<Integer> com1_Cards;
    private ArrayList<Integer> com2_Cards;
    private ArrayList<Integer> com3_Cards;
    private ArrayList<Integer>[] comCards;
    private ArrayList<Integer> ownCards;
    private boolean suddendeathMode;
    private int suddendeathCount;
    private int winCondition;
    private boolean gameEnded;

    private DatabaseReference roomDataRef;
    private String key;
    private String username;
    private String otherName1;
    private String otherName2;
    private String otherName3;
    private ArrayList<String> turnArray;
    private ValueEventListener roomListener;
    private Room room;
    private int turnNo;
    private boolean ready;

    private final static Random random = new Random();

    private ArrayList<Integer> drawnCards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_game_4p);

        Intent intent = getIntent();
        key = intent.getStringExtra("key");
        username = intent.getStringExtra("user");
        turnArray = intent.getStringArrayListExtra("turnOrder");
        roomDataRef = FirebaseDatabase.getInstance().getReference().child("games").child(key);
        com1Text = (TextView) findViewById(R.id.com1name);
        com2Text = (TextView) findViewById(R.id.com2name);
        com3Text = (TextView) findViewById(R.id.com3name);
        ready = false;
        if (turnArray.get(0).equals(username)) {
            otherName1 = turnArray.get(1);
            com1Text.setText(turnArray.get(1));
            otherName2 = turnArray.get(2);
            com2Text.setText(turnArray.get(2));
            otherName3 = turnArray.get(3);
            com3Text.setText(turnArray.get(3));
        } else if (turnArray.get(1).equals(username)){
            otherName1 = turnArray.get(0);
            com1Text.setText(turnArray.get(0));
            otherName2 = turnArray.get(2);
            com2Text.setText(turnArray.get(2));
            otherName3 = turnArray.get(3);
            com3Text.setText(turnArray.get(3));
        }
        else if (turnArray.get(2).equals(username)) {
            otherName1 = turnArray.get(0);
            com1Text.setText(turnArray.get(0));
            otherName2 = turnArray.get(1);
            com2Text.setText(turnArray.get(1));
            otherName3 = turnArray.get(3);
            com3Text.setText(turnArray.get(3));
        }
        else {
            otherName1 = turnArray.get(0);
            com1Text.setText(turnArray.get(0));
            otherName2 = turnArray.get(1);
            com2Text.setText(turnArray.get(1));
            otherName3 = turnArray.get(2);
            com3Text.setText(turnArray.get(2));
        }

        useAbility = (Button) findViewById(R.id.useAbility4P);
        drawButton = (ImageButton) findViewById(R.id.drawButton4P);
        boxImage0 = (ImageView) findViewById(R.id.boxImage0_4P);
        boxImage1 = (ImageView) findViewById(R.id.boxImage1_4P);
        boxImage2 = (ImageView) findViewById(R.id.boxImage2_4P);
        boxImage3 = (ImageView) findViewById(R.id.boxImage3_4P);
        boxImage4 = (ImageView) findViewById(R.id.boxImage4_4P);
        boxImage5 = (ImageView) findViewById(R.id.boxImage5_4P);

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

        ValueEventListener roomListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get room object and use the values to update the UI
                room = dataSnapshot.getValue(Room.class);
                final TextView showMessage = (TextView) findViewById(R.id.showMessage);
                if (room.readytoStart().size() < 3 && ready == false) {
                    com1_Cards = room.getCards(otherName1);
                    com2_Cards = room.getCards(otherName2);
                    com3_Cards = room.getCards(otherName3);
                    comCards[0] = com1_Cards;
                    comCards[1] = com2_Cards;
                    comCards[2] = com3_Cards;
                    for (int i = 4; i < 6; i++) {
                        boxIsEmpty[i] = true;
                        boxCards[i] = null;
                    }
                    ownCards = room.getCards(username);
                    for (int j = 0; j < 4; j++) {
                        Card card = new Card(ownCards.get(j));
                        boxImages[j].setImageResource(card.getImage());
                        boxImages[j].setVisibility(View.VISIBLE);
                        boxIsEmpty[j] = false;
                        boxCards[j] = card;
                        //updateScore(card.getValue(), tv);
                    }
                    TextView tv = (TextView) findViewById(R.id.playerScore);
                    updateScoreFromFirebase((int) room.players().get(username).get("score"), tv);
                    TextView comptv1 = (TextView) findViewById(R.id.otherScore1);
                    updateScoreFromFirebase((int) room.players().get(otherName1).get("score"), comptv1);
                    TextView comptv2 = (TextView) findViewById(R.id.otherScore2);
                    updateScoreFromFirebase((int) room.players().get(otherName2).get("score"), comptv2);
                    TextView comptv3 = (TextView) findViewById(R.id.otherScore3);
                    updateScoreFromFirebase((int) room.players().get(otherName3).get("score"), comptv3);

                    winCondition = 0;
                    ready = true;
                    room.playerReady();
                    roomDataRef.setValue(room.toMap());
                } else if (room.readytoStart().size() < 5 && ready == true) {
                    computerMoves();
                    showMessage.setText("Drawing cards; please wait.");
                } else {
                    //check for updates in wincondition etc first and set appropriate
                    TextView comptv1 = (TextView) findViewById(R.id.otherScore1);
                    updateScoreFromFirebase((int) room.players().get(otherName1).get("score"), comptv1);
                    TextView comptv2 = (TextView) findViewById(R.id.otherScore2);
                    updateScoreFromFirebase((int) room.players().get(otherName2).get("score"), comptv2);
                    TextView comptv3 = (TextView) findViewById(R.id.otherScore3);
                    updateScoreFromFirebase((int) room.players().get(otherName3).get("score"), comptv3);
                    com1_Cards = room.getCards(otherName1);
                    com2_Cards = room.getCards(otherName2);
                    com3_Cards = room.getCards(otherName3);
                    comCards[0] = com1_Cards;
                    comCards[1] = com2_Cards;
                    comCards[2] = com3_Cards;
                    suddendeathMode = room.suddendeathMode();
                    suddendeathCount = room.suddendeathCount();
                    //gameEnded = room.gameEnded();
                    drawnCards = room.drawnCards();
                    if (suddendeathCount == -1 || drawnCards.size() == 52) {
                        endGame();
                    }
                    if (dataSnapshot.hasChild("abilityUser") == true && !(room.abilityUser().equals(username))) {
                        int newWinCondition = room.winCondition();
                        if (!(winCondition == newWinCondition)) {
                            winCondition = newWinCondition;
                            if (winCondition == 0) {
                                startExplosion("Restoration" + "\n" + "Highest hand wins!");
                            } else if (winCondition == 1) {
                                startExplosion("Sabotage" + "\n" + "Lowest hand wins!");
                            }
                        } else if (suddendeathCount == 1) {
                            startExplosion("Sudden death" + "\n" + "Game ends next round");
                        } else if (room.deactivateCheck() == true) {
                            //TextView showMessage = (TextView) findViewById(R.id.showMessage);
                            showMessage.setText(room.abilityUser() + " used Deactivate!");
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    showMessage.setText(null);
                                    for (int i = 0; i < boxCards.length; i++) {
                                        if (!boxIsEmpty[i]) {
                                            boxCards[i].deactivate();
                                        }
                                    }
                                }
                            }, 3000);
                            room.setDeactivateCheck(false);
                        } else {
                            ArrayList<Integer> valuesToDiscard = room.discarded();
                            ArrayList<Integer> indexesToDiscard = new ArrayList<Integer>();
                            //TextView showMessage = (TextView) findViewById(R.id.showMessage);
                            showMessage.setText(room.abilityUser() + " used Discard!");
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                }
                            }, 3000);
                            showMessage.setText(null);
                            for (int i = 0; i < boxCards.length; i++) {
                                if (!boxIsEmpty[i] && valuesToDiscard.contains(boxCards[i].getRank())) {
                                    indexesToDiscard.add(i);
                                }
                            }
                            for (int i : indexesToDiscard) {
                                boxImages[i].setImageResource(R.drawable.blast);
                                final AnimationDrawable a = (AnimationDrawable) boxImages[i].getDrawable();
                                a.start();
                            }
                            TextView tv = (TextView) findViewById(R.id.playerScore);
                            updateScoreFromFirebase((int) room.players().get(username).get("score"), tv);
                            handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                }
                            }, 1000);
                            for (int m = 0; m < indexesToDiscard.size(); m++) {
                                boxImages[indexesToDiscard.get(m)].clearAnimation();
                                boxImages[indexesToDiscard.get(m)].setImageResource(R.drawable.empty);
                                boxIsEmpty[indexesToDiscard.get(m)] = true;
                                boxCards[indexesToDiscard.get(m)] = null;
                            }
                            room.setDiscarded(new ArrayList<Integer>());
                        }
                        room.setAbilityUser("");
                    }
                    //determine moves
                    turnNo = room.turnNo();
                    if (turnArray.get(turnNo).equals(username)) {
                        showMessage.setText(null);
                        playerTurn();
                    } else {
                        computerMoves();
                        showMessage.setText(turnArray.get(turnNo) + "'s turn.");
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        roomDataRef.addValueEventListener(roomListener);
    }
    @Override
    public void onClick(View v) {
        final ArrayList<Card> temp1 = new ArrayList<Card>();
        final ArrayList<Integer> temp2 = new ArrayList<Integer>();
        for (int i = 0; i < boxCards.length; i++) {
            if (!boxIsEmpty[i]) {
                if (boxCards[i].getAbility() != "None") {
                    temp1.add(boxCards[i]);
                    temp2.add(i);
                }
            }
        }
        CharSequence[] options = new CharSequence[temp1.size()];
        for (int j = 0; j < temp1.size(); j++) {
            options[j] = temp1.get(j).toString() + " - " + temp1.get(j).getAbility();
        }
        AlertDialog.Builder chooseAbility = new AlertDialog.Builder(this);
        chooseAbility.setTitle("Choose an ability to use.");
        chooseAbility.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                TextView showMessage = (TextView) findViewById(R.id.showMessage);
                showMessage.setText(null);
                drawButton.setClickable(false);
                executeAbility(temp2.get(id));
            }
        });
        chooseAbility.show();
        chooseAbility.setCancelable(true);
    }

    private void drawCard(int howMany) {
        TextView showMessage = (TextView) findViewById(R.id.showMessage);
        showMessage.setText(null);
        // Draw a card from the deckPile
        int value = random.nextInt(52);
        while (drawnCards.contains(value)) {
            value = random.nextInt(52);
        }
        // Set the image in one of the boxes
        Card card = new Card(value);
        if (boxIsEmpty[0]) {
            ownCards.add(value);
            room.setCards(username, ownCards);
            boxImage0.setImageResource(card.getImage());
            boxImage0.setVisibility(View.VISIBLE);
            boxIsEmpty[0] = false;
            boxCards[0] = card;
            TextView tv = (TextView) findViewById(R.id.playerScore);
            updateScore(card.getValue(), tv, username);
            drawnCards.add(value);
            room.setDrawnCards(drawnCards);
            if (drawnCards.size() == 52) {
                room.nextTurn();
                roomDataRef.setValue(room.toMap());
            } else {
                if (howMany != 1) {
                    howMany--;
                    drawCard(howMany);
                } else {
                    if (suddendeathCount == 0) {
                        room.minusSuddenDeathCount();
                        room.nextTurn();
                        roomDataRef.setValue(room.toMap());
                    } else if (suddendeathMode) {
                        suddendeathCount--;
                        room.minusSuddenDeathCount();
                        room.nextTurn();
                        roomDataRef.setValue(room.toMap());
                    } else {
                        room.nextTurn();
                        roomDataRef.setValue(room.toMap());
                    }
                }
            }
        } else if (boxIsEmpty[1]) {
            ownCards.add(value);
            room.setCards(username, ownCards);
            boxImage1.setImageResource(card.getImage());
            boxImage1.setVisibility(View.VISIBLE);
            boxIsEmpty[1] = false;
            boxCards[1] = card;
            TextView tv = (TextView) findViewById(R.id.playerScore);
            updateScore(card.getValue(), tv, username);
            drawnCards.add(value);
            room.setDrawnCards(drawnCards);
            if (drawnCards.size() == 52) {
                room.nextTurn();
                roomDataRef.setValue(room.toMap());
            } else {
                if (howMany != 1) {
                    howMany--;
                    drawCard(howMany);
                } else {
                    if (suddendeathCount == 0) {
                        room.minusSuddenDeathCount();
                        room.nextTurn();
                        roomDataRef.setValue(room.toMap());
                    } else if (suddendeathMode) {
                        suddendeathCount--;
                        room.minusSuddenDeathCount();
                        room.nextTurn();
                        roomDataRef.setValue(room.toMap());
                    } else {
                        room.nextTurn();
                        roomDataRef.setValue(room.toMap());
                    }
                }
            }
        } else if (boxIsEmpty[2]) {
            ownCards.add(value);
            room.setCards(username, ownCards);
            boxImage2.setImageResource(card.getImage());
            boxImage2.setVisibility(View.VISIBLE);
            boxIsEmpty[2] = false;
            boxCards[2] = card;
            TextView tv = (TextView) findViewById(R.id.playerScore);
            updateScore(card.getValue(), tv, username);
            drawnCards.add(value);
            room.setDrawnCards(drawnCards);
            if (drawnCards.size() == 52) {
                room.nextTurn();
                roomDataRef.setValue(room.toMap());
            } else {
                if (howMany != 1) {
                    howMany--;
                    drawCard(howMany);
                } else {
                    if (suddendeathCount == 0) {
                        room.minusSuddenDeathCount();
                        room.nextTurn();
                        roomDataRef.setValue(room.toMap());
                    } else if (suddendeathMode) {
                        suddendeathCount--;
                        room.minusSuddenDeathCount();
                        room.nextTurn();
                        roomDataRef.setValue(room.toMap());
                    } else {
                        room.nextTurn();
                        roomDataRef.setValue(room.toMap());
                    }
                }
            }
        } else if (boxIsEmpty[3]) {
            ownCards.add(value);
            room.setCards(username, ownCards);
            boxImage3.setImageResource(card.getImage());
            boxImage3.setVisibility(View.VISIBLE);
            boxIsEmpty[3] = false;
            boxCards[3] = card;
            TextView tv = (TextView) findViewById(R.id.playerScore);
            updateScore(card.getValue(), tv, username);
            drawnCards.add(value);
            room.setDrawnCards(drawnCards);
            if (drawnCards.size() == 52) {
                room.nextTurn();
                roomDataRef.setValue(room.toMap());
            } else {
                if (howMany != 1) {
                    howMany--;
                    drawCard(howMany);
                } else {
                    if (suddendeathCount == 0) {
                        room.minusSuddenDeathCount();
                        room.nextTurn();
                        roomDataRef.setValue(room.toMap());
                    } else if (suddendeathMode) {
                        suddendeathCount--;
                        room.minusSuddenDeathCount();
                        room.nextTurn();
                        roomDataRef.setValue(room.toMap());
                    } else {
                        room.nextTurn();
                        roomDataRef.setValue(room.toMap());
                    }
                }
            }
        } else if (boxIsEmpty[4]) {
            ownCards.add(value);
            room.setCards(username, ownCards);
            boxImage4.setImageResource(card.getImage());
            boxImage4.setVisibility(View.VISIBLE);
            boxIsEmpty[4] = false;
            boxCards[4] = card;
            TextView tv = (TextView) findViewById(R.id.playerScore);
            updateScore(card.getValue(), tv, username);
            drawnCards.add(value);
            room.setDrawnCards(drawnCards);
            if (drawnCards.size() == 52) {
                room.nextTurn();
                roomDataRef.setValue(room.toMap());
            } else {
                if (howMany != 1) {
                    howMany--;
                    drawCard(howMany);
                } else {
                    if (suddendeathCount == 0) {
                        room.minusSuddenDeathCount();
                        room.nextTurn();
                        roomDataRef.setValue(room.toMap());
                    } else if (suddendeathMode) {
                        suddendeathCount--;
                        room.minusSuddenDeathCount();
                        room.nextTurn();
                        roomDataRef.setValue(room.toMap());
                    } else {
                        room.nextTurn();
                        roomDataRef.setValue(room.toMap());
                    }
                }
            }
        } else if (boxIsEmpty[5]) {
            ownCards.add(value);
            room.setCards(username, ownCards);
            boxImage5.setImageResource(card.getImage());
            boxImage5.setVisibility(View.VISIBLE);
            boxIsEmpty[5] = false;
            boxCards[5] = card;
            TextView tv = (TextView) findViewById(R.id.playerScore);
            updateScore(card.getValue(), tv, username);
            drawnCards.add(value);
            room.setDrawnCards(drawnCards);
            if (drawnCards.size() == 52) {
                room.nextTurn();
                roomDataRef.setValue(room.toMap());
            } else {
                if (howMany != 1) {
                    howMany--;
                    drawCard(howMany);
                } else {
                    if (suddendeathCount == 0) {
                        room.minusSuddenDeathCount();
                        room.nextTurn();
                        roomDataRef.setValue(room.toMap());
                    } else if (suddendeathMode) {
                        suddendeathCount--;
                        room.minusSuddenDeathCount();
                        room.nextTurn();
                        roomDataRef.setValue(room.toMap());
                    } else {
                        room.nextTurn();
                        roomDataRef.setValue(room.toMap());
                    }
                }
            }
        } else {
            replaceCard(card, value, howMany);
        }
    }

    private void computerMoves() {
        drawButton.setClickable(false);
        useAbility.setClickable(false);
        useAbility.setTextColor(Color.parseColor("#60C0C0C0"));
        useAbility.setBackgroundColor(Color.parseColor("#60C0C0C0"));
    }

    private void playerTurn() {
        TextView showMessage = (TextView) findViewById(R.id.showMessage);
        showMessage.setText("It's your turn. Draw a card or use an ability.");
        drawButton.setClickable(true);
        useAbility.setClickable(false);
        useAbility.setTextColor(Color.parseColor("#60C0C0C0"));
        useAbility.setBackgroundColor(Color.parseColor("#60C0C0C0"));
        for (int j = 0; j < boxCards.length; j++) {
            if (!boxIsEmpty[j]) {
                if (boxCards[j].getAbility() != "None") {
                    useAbility.setClickable(true);
                    useAbility.setTextColor(Color.BLACK);
                    useAbility.setBackgroundColor(Color.LTGRAY);
                }
            }
        }
    }

    private void endGame() {
        gameEnded = true;
        room.setGameEnded(true);
        drawButton.setImageResource(R.drawable.empty);
        drawButton.setVisibility(View.INVISIBLE);
        drawButton.setClickable(false);
        useAbility.setClickable(false);
        TextView playertv = (TextView) findViewById(R.id.playerScore);
        int playerScore = Integer.parseInt(playertv.getText().toString());
        TextView comptv = (TextView) findViewById(R.id.otherScore);
        TextView com1_tv = (TextView)findViewById(R.id.com1Score);
        int com1_Score = Integer.parseInt(com1_tv.getText().toString());
        TextView com2_tv = (TextView)findViewById(R.id.com2Score);
        int com2_Score = Integer.parseInt(com2_tv.getText().toString());
        TextView com3_tv = (TextView)findViewById(R.id.com3Score);
        int com3_Score = Integer.parseInt(com3_tv.getText().toString());
        Hashtable<String, Integer> table = new Hashtable<>();
        table.put(com1Text.getText().toString(),com1_Score);
        table.put(com2Text.getText().toString(),com2_Score);
        table.put(com3Text.getText().toString(),com3_Score);
        table.put("You",playerScore);
        ArrayList<String> sortedKeys = sortValue(table);
        AlertDialog.Builder outcome = new AlertDialog.Builder(this);
        if (winCondition == 0) {
            if (sortedKeys.get(3).equals("You")) {
                outcome.setTitle("You win!");
            }
            else {
                outcome.setTitle("You lose!");
            }
            String message = sortedKeys.get(3)+"\n"+"\n"+sortedKeys.get(2)+"\n"+"\n"+sortedKeys.get(1)+"\n"+"\n"+sortedKeys.get(0);
            outcome.setMessage(message);
        }
        else if (winCondition == 1) {
            if (sortedKeys.get(0).equals("You")) {
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

    private void updateScore(int value, TextView tv, String playerName) {
        String text = tv.getText().toString();
        int currentScore = Integer.parseInt(text);
        int newScore = currentScore + value;
        tv.setText(String.valueOf(newScore));
        room.players.get(playerName).put("score", newScore);
    }

    private void updateScoreFromFirebase(int value, TextView tv) {
        tv.setText(String.valueOf(value));
    }

    private void replaceCard(Card c, int v, int m) {
        drawButton.setClickable(false);
        useAbility.setClickable(false);
        useAbility.setTextColor(Color.parseColor("#60C0C0C0"));
        useAbility.setBackgroundColor(Color.parseColor("#60C0C0C0"));
        final TextView showMessage = (TextView) findViewById(R.id.showMessage);
        showMessage.setText("You have the maximum number of cards. Choose one to discard.");
        final ImageView drawnCardImage = (ImageView) findViewById(R.id.drawnCardImage);
        drawnCardImage.setImageResource(c.getImage());
        final TextView tv = (TextView) findViewById(R.id.playerScore);
        final Card card = c;
        final int value = v;
        final int howMany = m;

        drawnCardImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMessage.setText(null);
                drawnCards.add(value);
                room.setDrawnCards(drawnCards);
                drawnCardImage.setImageResource(R.drawable.empty);
                drawnCardImage.setVisibility(View.INVISIBLE);
                drawnCardImage.setClickable(false);
                for (int i = 0; i < boxImages.length; i++) {
                    boxImages[i].setClickable(false);
                }
                if (drawnCards.size() == 52) {
                    room.nextTurn();
                    roomDataRef.setValue(room.toMap());
                } else {
                    if (howMany != 1) {
                        int temp = howMany;
                        temp--;
                        drawCard(temp);
                    } else {
                        if (suddendeathCount == 0) {
                            room.minusSuddenDeathCount();
                            room.nextTurn();
                            roomDataRef.setValue(room.toMap());
                        } else if (suddendeathMode) {
                            suddendeathCount--;
                            room.minusSuddenDeathCount();
                            room.nextTurn();
                            roomDataRef.setValue(room.toMap());
                        } else {
                            room.nextTurn();
                            roomDataRef.setValue(room.toMap());
                        }
                    }
                }
            }
        });

        for (int i = 0; i < boxImages.length; i++) {
            final int id = i;
            boxImages[id].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showMessage.setText(null);
                    ownCards.remove((Integer)boxCards[id].getRank());
                    updateScore(-boxCards[id].getValue(), tv, username);
                    boxCards[id] = card;
                    boxImages[id].setImageResource(card.getImage());
                    ownCards.add(card.getRank());
                    room.setCards(username, ownCards);
                    updateScore(card.getValue(), tv, username);
                    drawnCards.add(value);
                    room.setDrawnCards(drawnCards);
                    drawnCardImage.setImageResource(R.drawable.empty);
                    drawnCardImage.setVisibility(View.INVISIBLE);
                    drawnCardImage.setClickable(false);
                    for (int i = 0; i < boxImages.length; i++) {
                        boxImages[i].setClickable(false);
                    }
                    if (drawnCards.size() == 52) {
                        room.nextTurn();
                        roomDataRef.setValue(room.toMap());
                    } else {
                        if (howMany != 1) {
                            int temp = howMany;
                            temp--;
                            drawCard(temp);
                        } else {
                            if (suddendeathCount == 0) {
                                room.minusSuddenDeathCount();
                                room.nextTurn();
                                roomDataRef.setValue(room.toMap());
                            } else if (suddendeathMode) {
                                suddendeathCount--;
                                room.minusSuddenDeathCount();
                                room.nextTurn();
                                roomDataRef.setValue(room.toMap());
                            } else {
                                room.nextTurn();
                                roomDataRef.setValue(room.toMap());
                            }
                        }
                    }
                }
            });
        }
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
        } else if (boxCards[boxNumber].getAbility() == "Sudden death: Game ends next round") {
            Intent intent = new Intent(this, ExplosiveActivity.class);
            intent.putExtra("Message", "Sudden death" + "\n" + "Game ends next round");
            startActivity(intent);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    suddendeath(boxNumber);
                }
            }, 6000);
        } else if (boxCards[boxNumber].getAbility() == "Sabotage: Lowest hand wins") {
            Intent intent = new Intent(this, ExplosiveActivity.class);
            intent.putExtra("Message", "Sabotage" + "\n" + "Lowest hand wins!");
            startActivity(intent);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    sabotage(boxNumber);
                }
            }, 6000);
        } else {
            final TextView showMessage = (TextView) findViewById(R.id.showMessage);
            showMessage.setText("You used " + boxCards[boxNumber].toString() + "!");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    showMessage.setText(null);
                    if (boxCards[boxNumber].getAbility() == "Deactivate opponent's cards") {
                        deactivate(boxNumber);
                    } else if (boxCards[boxNumber].getAbility() == "Opponent discards 2 random cards") {
                        discard2(boxNumber);
                    } else if (boxCards[boxNumber].getAbility() == "Draw 2 cards") {
                        draw(boxNumber, 2);
                    } else if (boxCards[boxNumber].getAbility() == "Draw 3 cards") {
                        draw(boxNumber, 3);
                    } else if (boxCards[boxNumber].getAbility() == "Opponent discards 2 highest cards") {
                        discardhighest(boxNumber, 2);
                    } else if (boxCards[boxNumber].getAbility() == "Opponent discards 3 highest cards") {
                        discardhighest(boxNumber, 3);
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
        ArrayList<Integer> toDiscard = new ArrayList<Integer>();
        for (int which = 0; which < 3; which++) {
            ArrayList<Integer> computerCards = comCards[which];
            String otherName;
            TextView tv;
            if (which == 0) {
                tv = (TextView) findViewById(R.id.com1Score);
                otherName = com1Text.getText().toString();
            }
            else if (which == 1) {
                tv = (TextView) findViewById(R.id.com2Score);
                otherName = com2Text.getText().toString();
            }
            else {
                tv = (TextView) findViewById(R.id.com3Score);
                otherName = com3Text.getText().toString();
            }
            if (computerCards.size()>0) {
                room.setAbilityUser(username);
                int index = random.nextInt(computerCards.size());
                int valueToDeduct = new Card(computerCards.get(index)).getValue();
                toDiscard.add(computerCards.get(index));
                computerCards.remove(index);
                updateScore(-valueToDeduct, tv, otherName);
                if (computerCards.size()>0) {
                    index = random.nextInt(computerCards.size());
                    valueToDeduct = new Card(computerCards.get(index)).getValue();
                    toDiscard.add(computerCards.get(index));
                    computerCards.remove(index);
                    updateScore(-valueToDeduct, tv, otherName);
                }
            }
            room.setCards(otherName, computerCards);
        }
        room.setDiscarded(toDiscard);

        TextView tv = (TextView) findViewById(R.id.playerScore);
        updateScore(-8, tv, username);
        boxImages[boxNumber].setImageResource(R.drawable.empty);
        ownCards.remove((Integer)boxCards[boxNumber].getRank());
        room.setCards(username, ownCards);
        boxIsEmpty[boxNumber] = true;
        boxCards[boxNumber] = null;
        if (suddendeathCount == 0) {
            room.minusSuddenDeathCount();
            room.nextTurn();
            roomDataRef.setValue(room.toMap());
        } else if (suddendeathMode) {
            suddendeathCount--;
            room.minusSuddenDeathCount();
            room.nextTurn();
            roomDataRef.setValue(room.toMap());
        } else {
            room.nextTurn();
            roomDataRef.setValue(room.toMap());
        }
    }

    private void draw(int boxNumber, int howMany) {
        TextView tv = (TextView) findViewById(R.id.playerScore);
        if (howMany == 2) {
            updateScore(-9, tv, username);
        } else if (howMany == 3) {
            updateScore(-10, tv, username);
        }
        boxImages[boxNumber].setImageResource(R.drawable.empty);
        ownCards.remove((Integer)boxCards[boxNumber].getRank());
        room.setCards(username, ownCards);
        boxIsEmpty[boxNumber] = true;
        boxCards[boxNumber] = null;

        drawCard(howMany);
    }

    private void deactivate(int boxNumber) {
        room.setAbilityUser(username);
        room.setDeactivateCheck(true);
        /*
        for (int which = 0; which < 3; which++) {
            ArrayList<Integer> computerCards = comCards[which];
            String otherName;
            if (which == 0) {
                otherName = com1Text.getText().toString();
            }
            else if (which == 1) {
                otherName = com2Text.getText().toString();
            }
            else {
                otherName = com3Text.getText().toString();
            }
            if (computerCards.size()>0) {
                for (int i = 0; i < computerCards.size(); i++) {
                    computerCards.get(i).deactivate();
                }
            }
            room.setCards(otherName, computerCards);
        }*/

        TextView tv = (TextView) findViewById(R.id.playerScore);
        updateScore(-1, tv, username);
        boxImages[boxNumber].setImageResource(R.drawable.empty);
        ownCards.remove((Integer)boxCards[boxNumber].getRank());
        room.setCards(username, ownCards);
        boxIsEmpty[boxNumber] = true;
        boxCards[boxNumber] = null;
        if (suddendeathCount == 0) {
            room.minusSuddenDeathCount();
            room.nextTurn();
            roomDataRef.setValue(room.toMap());
        } else if (suddendeathMode) {
            suddendeathCount--;
            room.minusSuddenDeathCount();
            room.nextTurn();
            roomDataRef.setValue(room.toMap());
        } else {
            room.nextTurn();
            roomDataRef.setValue(room.toMap());
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

        room.setAbilityUser(username);
        ArrayList<Integer> toDiscard = new ArrayList<Integer>();
        for (int which = 0; which < 3; which++) {
            ArrayList<Integer> computerCards = comCards[which];
            TextView tv;
            String otherName;
            if (which == 0) {
                tv = (TextView) findViewById(R.id.com1Score);
                otherName = com1Text.getText().toString();
            }
            else if (which == 1) {
                tv = (TextView) findViewById(R.id.com2Score);
                otherName = com2Text.getText().toString();
            }
            else {
                tv = (TextView) findViewById(R.id.com3Score);
                otherName = com3Text.getText().toString();
            }
            Collections.sort(computerCards);
            int valueToDeduct;
            for (int i = 0; i < howMany; i++) {
                if (computerCards.size()>0) {
                    valueToDeduct = new Card(computerCards.get(computerCards.size()-1)).getValue();
                    toDiscard.add(computerCards.get(computerCards.size() - 1));
                    computerCards.remove(computerCards.size()-1);
                    room.setCards(otherName, computerCards);
                    updateScore(-valueToDeduct, tv, otherName);
                }
            }
        }
        room.setDiscarded(toDiscard);

        TextView tv = (TextView) findViewById(R.id.playerScore);
        if (howMany == 2) {
            updateScore(-11, tv, username);
        } else if (howMany == 3) {
            updateScore(-12, tv, username);
        }
        boxImages[boxNumber].setImageResource(R.drawable.empty);
        ownCards.remove((Integer)boxCards[boxNumber].getRank());
        room.setCards(username, ownCards);
        boxIsEmpty[boxNumber] = true;
        boxCards[boxNumber] = null;
        if (suddendeathCount == 0) {
            room.minusSuddenDeathCount();
            room.nextTurn();
            roomDataRef.setValue(room.toMap());
        } else if (suddendeathMode) {
            suddendeathCount--;
            room.minusSuddenDeathCount();
            room.nextTurn();
            roomDataRef.setValue(room.toMap());
        } else {
            room.nextTurn();
            roomDataRef.setValue(room.toMap());
        }
    }

    private void restoration(int boxNumber) {
        room.setAbilityUser(username);
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.gamelayout);
        layout.setBackgroundColor(Color.RED);
        winCondition = 0;
        room.setWinCondition(0);

        TextView tv = (TextView) findViewById(R.id.playerScore);
        updateScore(-1, tv, username);
        boxImages[boxNumber].setImageResource(R.drawable.empty);
        ownCards.remove((Integer)boxCards[boxNumber].getRank());
        room.setCards(username, ownCards);
        boxIsEmpty[boxNumber] = true;
        boxCards[boxNumber] = null;
        if (suddendeathCount == 0) {
            room.minusSuddenDeathCount();
            room.nextTurn();
            roomDataRef.setValue(room.toMap());
        } else if (suddendeathMode) {
            suddendeathCount--;
            room.minusSuddenDeathCount();
            room.nextTurn();
            roomDataRef.setValue(room.toMap());
        } else {
            room.nextTurn();
            roomDataRef.setValue(room.toMap());
        }
    }

    private void suddendeath(int boxNumber) {
        room.setAbilityUser(username);
        suddendeathMode = true;
        room.setSDM(true);
        suddendeathCount--;
        room.minusSuddenDeathCount();

        TextView tv = (TextView) findViewById(R.id.playerScore);
        updateScore(-13, tv, username);
        boxImages[boxNumber].setImageResource(R.drawable.empty);
        ownCards.remove((Integer)boxCards[boxNumber].getRank());
        room.setCards(username, ownCards);
        boxIsEmpty[boxNumber] = true;
        boxCards[boxNumber] = null;
        room.nextTurn();
        roomDataRef.setValue(room.toMap());
    }

    private void sabotage(int boxNumber) {
        room.setAbilityUser(username);
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.gamelayout);
        layout.setBackgroundColor(Color.BLACK);
        winCondition = 1;
        room.setWinCondition(1);

        TextView tv = (TextView) findViewById(R.id.playerScore);
        updateScore(-13, tv, username);
        boxImages[boxNumber].setImageResource(R.drawable.empty);
        ownCards.remove((Integer)boxCards[boxNumber].getRank());
        room.setCards(username, ownCards);
        boxIsEmpty[boxNumber] = true;
        boxCards[boxNumber] = null;
        if (suddendeathCount == 0) {
            room.minusSuddenDeathCount();
            room.nextTurn();
            roomDataRef.setValue(room.toMap());
        } else if (suddendeathMode) {
            suddendeathCount--;
            room.minusSuddenDeathCount();
            room.nextTurn();
            roomDataRef.setValue(room.toMap());
        } else {
            room.nextTurn();
            roomDataRef.setValue(room.toMap());
        }
    }

    /* -----------Methods for card abilities (Opponent) ------------------------- */
    private void startExplosion(String message) {
        Intent intent = new Intent(this, ExplosiveActivity.class);
        intent.putExtra("Message", message);
        startActivity(intent);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                RelativeLayout layout = (RelativeLayout) findViewById(R.id.gamelayout);
                layout.setBackgroundColor(Color.RED);
            }
        }, 6000);
    }
}
