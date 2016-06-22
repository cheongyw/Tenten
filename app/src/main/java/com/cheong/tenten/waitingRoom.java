package com.cheong.tenten;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class waitingRoom extends AppCompatActivity {

    private ListView playerList;
    private Button buttonLeave;
    private Button buttonStart;
    private HashMap<String, HashMap<String, Object>> players;
    private HashMap<String, Object> player;
    private DatabaseReference roomDataRef;
    private String key;
    private String username;
    private ValueEventListener roomListener;
    private Room room;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        playerList = (ListView) findViewById(R.id.listView);
        buttonLeave = (Button) findViewById(R.id.button_leave);
        buttonStart = (Button) findViewById(R.id.button_start);

        Intent intent = getIntent();
        key = intent.getStringExtra("key");
        username = intent.getStringExtra("user");
        roomDataRef = FirebaseDatabase.getInstance().getReference().child("games").child(key);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room);

        //set up listview

        ValueEventListener roomListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get room object and use the values to update the UI
                room = dataSnapshot.getValue(Room.class);
                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        roomDataRef.addValueEventListener(roomListener);
    }

    public void leaveRoom(View view){
        //remove user name from database
        players = new HashMap<String, HashMap<String, Object>> (room.players());
        players.remove(username);
        roomDataRef.child("players").setValue(players);

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
        if (true){

        }
        else { //split into another if else here for 2p and 4p
            Intent intent = new Intent(this, waitingRoom.class);
            intent.putExtra("key", key);
            intent.putExtra("user", username);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }
}
