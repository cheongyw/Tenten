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
                values.clear();
                for (String player : room.players().keySet()) {
                    values.add(player);
                }
                adapter.notifyDataSetChanged();
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
        else if (room.nPlayers() == 2){ //split into another if else here for 2p and 4p
            Intent intent = new Intent(this, MultiGameActivity.class);
            intent.putExtra("key", key);
            intent.putExtra("user", username);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        else {
            Intent intent = new Intent(this, MultiGameActivity4P.class);
            intent.putExtra("key", key);
            intent.putExtra("user", username);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }
}
