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
import java.util.Collections;
import java.util.Random;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiGameActivity extends AppCompatActivity implements View.OnClickListener {

    // UI components
    private TextView otherNameText;
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
    private ArrayList<Card> otherCards;
    private ArrayList<Card> ownCards;
    private boolean suddendeathMode;
    private int suddendeathCount;
    private int winCondition;
    private boolean gameEnded;
    private String[] players;

    private DatabaseReference roomDataRef;
    private String key;
    private String username;
    private String otherName;
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
        setContentView(R.layout.activity_multi_game);

        Intent intent = getIntent();
        key = intent.getStringExtra("key");
        username = intent.getStringExtra("user");
        turnArray = intent.getStringArrayListExtra("turnOrder");
        roomDataRef = FirebaseDatabase.getInstance().getReference().child("games").child(key);
        otherNameText = (TextView) findViewById(R.id.otherScoreStaticText);
        ready = false;
        if (turnArray.get(0).equals(username)) {
            otherName = turnArray.get(1);
            otherNameText.setText(turnArray.get(1));
        } else {
            otherName = turnArray.get(0);
            otherNameText.setText(turnArray.get(0));
        }

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

        ValueEventListener roomListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get room object and use the values to update the UI
                room = dataSnapshot.getValue(Room.class);
                TextView showMessage = (TextView) findViewById(R.id.showMessage);
                if (room.readytoStart().size() < 3 && ready == false) {
                    otherCards = room.getCards(otherName);
                    for (int i = 4; i < 6; i++) {
                        boxIsEmpty[i] = true;
                        boxCards[i] = null;
                    }
                    ownCards = room.getCards(username);
                    for (int j = 0; j < 4; j++) {
                        Card card = ownCards.get(j);
                        boxImages[j].setImageResource(card.getImage());
                        boxImages[j].setVisibility(View.VISIBLE);
                        boxIsEmpty[j] = false;
                        boxCards[j] = card;
                        //updateScore(card.getValue(), tv);
                    }
                    TextView tv = (TextView) findViewById(R.id.playerScore);
                    updateScoreFromFirebase((int)room.players().get(username).get("score"), tv);
                    TextView comptv = (TextView) findViewById(R.id.otherScore);
                    updateScoreFromFirebase((int)room.players().get(otherName).get("score"), comptv);

                    winCondition = 0;
                    ready = true;
                    room.playerReady();
                    roomDataRef.setValue(room.toMap());
                } else if (room.readytoStart().size() < 3 && ready == true) {
                    computerMoves();
                    showMessage.setText("Drawing cards; please wait.");
                }
                else {
                    //check for updates in wincondition etc first and set appropriate
                    TextView comptv = (TextView) findViewById(R.id.otherScore);
                    updateScoreFromFirebase((int)room.players().get(otherName).get("score"), comptv);
                    otherCards = room.getCards(otherName);

                    suddendeathMode = room.suddendeathMode();
                    suddendeathCount = room.suddendeathCount();
                    //gameEnded = room.gameEnded();
                    drawnCards = room.drawnCards();
                    if (suddendeathCount == -1 || drawnCards.size() == 52) {endGame();}
                    int newWinCondition = room.winCondition();
                    if (!(winCondition == newWinCondition)) {
                        winCondition = newWinCondition;
                        if (winCondition == 0) {
                            RelativeLayout layout = (RelativeLayout) findViewById(R.id.gamelayout);
                            layout.setBackgroundColor(Color.RED);
                        }
                        else if (winCondition == 1) {
                            RelativeLayout layout = (RelativeLayout) findViewById(R.id.gamelayout);
                            layout.setBackgroundColor(Color.BLACK);
                        }
                    }

                    turnNo = room.turnNo();
                    if (turnArray.get(turnNo).equals(username)) {
                        showMessage.setText(null);
                        playerTurn();
                    } else {
                        computerMoves();
                        showMessage.setText(turnArray.get(turnNo)+"'s turn.");
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
            ownCards.add(card);
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
            ownCards.add(card);
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
            ownCards.add(card);
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
            ownCards.add(card);
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
            ownCards.add(card);
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
            ownCards.add(card);
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
        int compScore = Integer.parseInt(comptv.getText().toString());
        AlertDialog.Builder outcome = new AlertDialog.Builder(this);
        if (winCondition == 0) {
            if (playerScore > compScore) {
                outcome.setMessage("You win!");
            } else if (playerScore == compScore) {
                outcome.setMessage("It's a tie!");
            } else {
                outcome.setMessage("You lose!");
            }
        } else if (winCondition == 1) {
            if (playerScore < compScore) {
                outcome.setMessage("You win!");
            } else if (playerScore == compScore) {
                outcome.setMessage("It's a tie!");
            } else {
                outcome.setMessage("You lose!");
            }
        }
        outcome.setCancelable(true);
        outcome.show();
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
                    ownCards.remove(boxCards[id]);
                    updateScore(-boxCards[id].getValue(), tv, username);
                    boxCards[id] = card;
                    boxImages[id].setImageResource(card.getImage());
                    ownCards.add(card);
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

        if (otherCards.size() > 0) {
            int index = random.nextInt(otherCards.size());
            int valueToDeduct = otherCards.get(index).getValue();
            otherCards.remove(index);
            TextView tv = (TextView) findViewById(R.id.otherScore);
            updateScore(-valueToDeduct, tv, otherName);
            if (otherCards.size() > 0) {
                index = random.nextInt(otherCards.size());
                valueToDeduct = otherCards.get(index).getValue();
                otherCards.remove(index);
                updateScore(-valueToDeduct, tv, otherName);
            }
        }
        room.setCards(otherName, otherCards);

        TextView tv = (TextView) findViewById(R.id.playerScore);
        updateScore(-8, tv, username);
        boxImages[boxNumber].setImageResource(R.drawable.empty);
        ownCards.remove(boxCards[boxNumber]);
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
        ownCards.remove(boxCards[boxNumber]);
        room.setCards(username, ownCards);
        boxIsEmpty[boxNumber] = true;
        boxCards[boxNumber] = null;

        drawCard(howMany);
    }

    private void deactivate(int boxNumber) {
        if (otherCards.size() > 0) {
            for (int i = 0; i < otherCards.size(); i++) {
                otherCards.get(i).deactivate();
            }
        }
        room.setCards(otherName, otherCards);

        TextView tv = (TextView) findViewById(R.id.playerScore);
        updateScore(-1, tv, username);
        boxImages[boxNumber].setImageResource(R.drawable.empty);
        ownCards.remove(boxCards[boxNumber]);
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

        Collections.sort(otherCards);
        int valueToDeduct;
        for (int i = 0; i < howMany; i++) {
            if (otherCards.size() > 0) {
                valueToDeduct = otherCards.get(otherCards.size() - 1).getValue();
                otherCards.remove(otherCards.size() - 1);
                room.setCards(otherName, otherCards);
                TextView tv = (TextView) findViewById(R.id.otherScore);
                updateScore(-valueToDeduct, tv, otherName);
            }
        }

        TextView tv = (TextView) findViewById(R.id.playerScore);
        if (howMany == 2) {
            updateScore(-11, tv, username);
        } else if (howMany == 3) {
            updateScore(-12, tv, username);
        }
        boxImages[boxNumber].setImageResource(R.drawable.empty);
        ownCards.remove(boxCards[boxNumber]);
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
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.gamelayout);
        layout.setBackgroundColor(Color.RED);
        winCondition = 0;
        room.setWinCondition(0);

        TextView tv = (TextView) findViewById(R.id.playerScore);
        updateScore(-1, tv, username);
        boxImages[boxNumber].setImageResource(R.drawable.empty);
        ownCards.remove(boxCards[boxNumber]);
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
        suddendeathMode = true;
        room.setSDM(true);
        suddendeathCount--;
        room.minusSuddenDeathCount();

        TextView tv = (TextView) findViewById(R.id.playerScore);
        updateScore(-13, tv, username);
        boxImages[boxNumber].setImageResource(R.drawable.empty);
        ownCards.remove(boxCards[boxNumber]);
        room.setCards(username, ownCards);
        boxIsEmpty[boxNumber] = true;
        boxCards[boxNumber] = null;
        room.nextTurn();
        roomDataRef.setValue(room.toMap());
    }

    private void sabotage(int boxNumber) {
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.gamelayout);
        layout.setBackgroundColor(Color.BLACK);
        winCondition = 1;
        room.setWinCondition(1);

        TextView tv = (TextView) findViewById(R.id.playerScore);
        updateScore(-13, tv, username);
        boxImages[boxNumber].setImageResource(R.drawable.empty);
        ownCards.remove(boxCards[boxNumber]);
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
}
