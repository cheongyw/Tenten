package com.cheong.tenten;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Random;

public class waitingRoom extends AppCompatActivity {

    private ListView playerList;
    private TextView errorMessage;
    private TextView roomNameRef;
    private Button buttonLeave;
    private Button buttonStart;
    private HashMap<String, HashMap<String, Object>> players;
    private HashMap<String, Object> player;
    private DatabaseReference roomDataRef;
    private String key;
    private String username;
    private ValueEventListener roomListener;
    private Room room;
    private ArrayList<String> values;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> turnArray;

    private final static Random random = new Random();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        values = new ArrayList<String>(4);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, values);

        Intent intent = getIntent();
        key = intent.getStringExtra("key");
        username = intent.getStringExtra("user");
        roomDataRef = FirebaseDatabase.getInstance().getReference().child("games").child(key);

        ValueEventListener roomListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get room object and use the values to update the UI
                room = dataSnapshot.getValue(Room.class);
                if (room.gameStarted() == true) {
                    movetoGame();
                }
                else {
                    values.clear();
                    for (String player : room.players().keySet()) {
                        values.add(player);
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        roomDataRef.addValueEventListener(roomListener);
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            public void run() {
                doStuff();
            }
        }, 2000);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_screen);
    }

    private void doStuff() {
        setContentView(R.layout.activity_waiting_room);
        playerList = (ListView) findViewById(R.id.listView_players);
        roomNameRef = (TextView) findViewById(R.id.textView_roomName);

        playerList.setAdapter(adapter);
        roomNameRef.setText("Room name: " + room.roomName());
        Toast.makeText(this, "Welcome to "+ room.roomName() +"!", Toast.LENGTH_SHORT).show();
    }

    public void leaveRoom(View view){
        //remove user name from database
        if (room.players().size()==1) {
            //not working
            //roomDataRef.removeValue();
            roomDataRef.child("gameEnded").setValue(true);
        }
        else {
            players = new HashMap<String, HashMap<String, Object>>(room.players());
            players.remove(username);
            roomDataRef.child("players").setValue(players);
        }

        //adjust to delete room if last player in room
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
    public void startRoom(View view){
        //check if number of players match
        //if no, set msg visibility
        //else move to multigameactivity
        if (room.players().size() != room.nPlayers()){
            errorMessage = (TextView) findViewById(R.id.errorMessage);
            errorMessage.setText("Not enough players.");
        }
        else if (room.nPlayers() == 2){ //2p
            turnArray = new ArrayList<String>(2);
            int x = 0;
            for (String player : room.players().keySet()){
                turnArray.add(player);
                x +=1;
            }

            ArrayList<Integer> drawnCards = new ArrayList<Integer>();
            ArrayList<HashMap<String, Object>> playerArray = new ArrayList<HashMap<String, Object>>();
            for (int i = 0;i<2;i++) {
                int score = 0;
                ArrayList<Card> cards = new ArrayList<Card>();
                for (int j = 0; j < 4; j++) {
                    int value = random.nextInt(52);
                    while (drawnCards.contains(value)) {
                        value = random.nextInt(52);
                    }
                    Card card = new Card(value);
                    score += card.getValue();
                    cards.add(card);
                    drawnCards.add(value);
                }

                HashMap<String, Object> player = new HashMap<String, Object>();
                player.put("score", score);
                player.put("cards", cards);
                playerArray.add(player);
            }
            HashMap<String, HashMap<String, Object>> newPlayers = new HashMap<String, HashMap<String, Object>>();
            for (int i = 0; i<2;i++) {
                newPlayers.put(turnArray.get(i), playerArray.get(i));
            }
            /*
            Map<String, Object> childUpdates = new HashMap<String, Object>();
            childUpdates.put("/gameStarted", true);
            for (int i = 0;i<2;i++) {
                childUpdates.put("/players/"+turnArray[i], playerArray.get(i));
            }
            childUpdates.put("/turnArray", turnArray);
            childUpdates.put("/drawnCards", drawnCards);
            roomDataRef.updateChildren(childUpdates);*/

            room.setGameStarted(true);
            room.setPlayers(newPlayers);
            room.setTurnArray(turnArray);
            room.setDrawnCards(drawnCards);
            roomDataRef.setValue(room);
        }
        else { //4p
            turnArray = new ArrayList<String>(4);
            int x = 0;
            for (String player : room.players().keySet()){
                turnArray.add(player);
                x +=1;
            }

            ArrayList<Integer> drawnCards = new ArrayList<Integer>();
            ArrayList<HashMap<String, Object>> playerArray = new ArrayList<HashMap<String, Object>>();
            for (int i = 0;i<4;i++) {
                int score = 0;
                ArrayList<Card> cards = new ArrayList<Card>();
                for (int j = 0; j < 4; j++) {
                    int value = random.nextInt(52);
                    while (drawnCards.contains(value)) {
                        value = random.nextInt(52);
                    }
                    Card card = new Card(value);
                    score += card.getValue();
                    cards.add(card);
                    drawnCards.add(value);
                }

                HashMap<String, Object> player = new HashMap<String, Object>();
                player.put("score", score);
                player.put("cards", cards);
                playerArray.add(player);
            }
            HashMap<String, HashMap<String, Object>> newPlayers = new HashMap<String, HashMap<String, Object>>();
            for (int i = 0; i<4;i++) {
                newPlayers.put(turnArray.get(i), playerArray.get(i));
            }
            /*
            Map<String, Object> childUpdates = new HashMap<String, Object>();
            childUpdates.put("/gameStarted", true);
            for (int i = 0;i<4;i++) {
                childUpdates.put("/players/"+turnArray[i], playerArray.get(i));
            }
            childUpdates.put("/turnArray", turnArray);
            childUpdates.put("/drawnCards", drawnCards);
            roomDataRef.updateChildren(childUpdates);*/

            room.setGameStarted(true);
            room.setPlayers(newPlayers);
            room.setTurnArray(turnArray);
            room.setDrawnCards(drawnCards);
            roomDataRef.setValue(room);
        }
    }

    public void movetoGame() {
        Intent intent = new Intent(this, MultiGameActivity.class);
        intent.putExtra("key", key);
        intent.putExtra("user", username);
        intent.putExtra("turnOrder", turnArray);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
