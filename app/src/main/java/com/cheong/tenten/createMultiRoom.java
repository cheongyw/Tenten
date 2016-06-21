package com.cheong.tenten;

/**
 * Created by Sherry on 17/06/2016.
 */
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

public class createMultiRoom extends AppCompatActivity {
    //UI components
    private TextView tvUsername;
    private TextView tvCreateTitle;
    private TextView tvCreateName;
    private TextView tvJoinTitle;
    private TextView tvJoinName;
    private TextView tvAvailability;
    private EditText etUsername;
    private EditText etCreateName;
    private EditText etJoinName;
    private Button create2p;
    private Button create4p;
    private Button joinName;
    private Button join2p;
    private Button join4p;

    private DatabaseReference database;
    private String roomName;
    private String username;
    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //private static final String TAG = "createMultiplayerRoomActivity";

        tvUsername = (TextView) findViewById(R.id.textView_username);
        tvCreateTitle = (TextView) findViewById(R.id.textView_createRoomTitle);
        tvCreateName = (TextView) findViewById(R.id.textView_createRoomName);
        tvJoinTitle = (TextView) findViewById(R.id.textView_joinRoomTitle);
        tvJoinName = (TextView) findViewById(R.id.textView_joinRoomName);
        tvAvailability = (TextView) findViewById(R.id.textView_roomAvailability);
        etUsername = (EditText) findViewById(R.id.editText_username);
        etCreateName = (EditText) findViewById(R.id.editText_createRoomName);
        etJoinName = (EditText) findViewById(R.id.editText_joinRoomName);
        create2p = (Button) findViewById(R.id.startMulti2PGame);
        create4p = (Button) findViewById(R.id.startMulti4PGame);
        joinName = (Button) findViewById(R.id.joinGameName);
        join2p = (Button) findViewById(R.id.joinMulti2PGame);
        join4p = (Button) findViewById(R.id.joinMulti4PGame);

        database = FirebaseDatabase.getInstance().getReference();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_create);
    }

    public void start2PlayerGame(View view) {
        etUsername = (EditText) findViewById(R.id.editText_username);
        etCreateName = (EditText) findViewById(R.id.editText_createRoomName);
        roomName = etCreateName.getText().toString();
        String creator = etUsername.getText().toString();
        Room newRoom = new Room(roomName, 2, creator);
        Map<String, Object> newRoomValues = newRoom.toMap();

        key = database.child("games").push().getKey();
        Map<String, Object> childUpdate = new HashMap<>();
        childUpdate.put("/games/" + key, newRoomValues);
        database.updateChildren(childUpdate);

        Intent intent = new Intent(this, waitingRoom.class);
        intent.putExtra("key", key);
        intent.putExtra("user", username);
        startActivity(intent);
    }

    public void start4PlayerGame(View view) {
        etUsername = (EditText) findViewById(R.id.editText_username);
        etCreateName = (EditText) findViewById(R.id.editText_createRoomName);
        roomName = etCreateName.getText().toString();
        String creator = etUsername.getText().toString();
        Room newRoom = new Room(roomName, 4, creator);
        Map<String, Object> newRoomValues = newRoom.toMap();

        key = database.child("games").push().getKey();
        Map<String, Object> childUpdate = new HashMap<>();
        childUpdate.put("/games/" + key, newRoomValues);
        database.updateChildren(childUpdate);

        Intent intent = new Intent(this, waitingRoom.class);
        intent.putExtra("key", key);
        intent.putExtra("user", username);
        startActivity(intent);
    }

    public void joinGameName(View view) {
        etUsername = (EditText) findViewById(R.id.editText_username);
        etJoinName = (EditText) findViewById(R.id.editText_joinRoomName);
        //check if have too many ppl before joining!
        roomName = etJoinName.getText().toString();
        username = etUsername.getText().toString();

        database.child("games").equalTo("roomName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot child: snapshot.getChildren()) {
                    if (!child.exists()) {
                        tvAvailability.setVisibility(View.VISIBLE);
                    } else {
                        Room room = child.getValue(Room.class);
                        key = child.getKey();
                        List<HashMap<String, Object>> players = room.players;
                        HashMap<String, Object> user = new HashMap<String, Object>();
                        user.put("username", username);
                        user.put("score", 0);
                        user.put("cards", new ArrayList<Card>());
                        players.add(user);
                        database.child("games").child(key).child("players").setValue(players);
                        break;
                    }
                }

            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
            }
        });
        Intent intent = new Intent(this, waitingRoom.class);
        intent.putExtra("key", key);
        intent.putExtra("user", username);
        startActivity(intent);
    }

    public void join2PlayerGame(View view) {
        etUsername = (EditText) findViewById(R.id.editText_username);
        username = etUsername.getText().toString();

        database.child("games").orderByChild("nPlayers").equalTo(2).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                key = "0";
                Room room = new Room();
                for (DataSnapshot child: snapshot.getChildren()) {
                    room = child.getValue(Room.class);
                    if (!child.exists()) {
                        tvAvailability.setVisibility(View.VISIBLE);
                    }
                    else if (room.gameStarted == true){
                        continue;
                    }
                    else {
                        key = child.getKey();
                        List<HashMap<String, Object>> players = room.players;
                        HashMap<String, Object> user = new HashMap<String, Object>();
                        user.put("username", username);
                        user.put("score", 0);
                        user.put("cards", new ArrayList<Card>());
                        players.add(user);
                        database.child("games").child(key).child("players").setValue(players);
                        break;
                    }
                }

            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
            }
        });
        if (key.equals("0")){
            tvAvailability.setVisibility(View.VISIBLE);
        }
        else {
            Intent intent = new Intent(this, waitingRoom.class);
            intent.putExtra("key", key);
            intent.putExtra("user", username);
            startActivity(intent);
        }
    }

    public void join4PlayerGame(View view) {
        etUsername = (EditText) findViewById(R.id.editText_username);
        username = etUsername.getText().toString();

        database.child("games").orderByChild("nPlayers").equalTo(4).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                key = "0";
                Room room = new Room();
                for (DataSnapshot child: snapshot.getChildren()) {
                    room = child.getValue(Room.class);
                    if (!child.exists()) {
                        tvAvailability.setVisibility(View.VISIBLE);
                    }
                    else if (room.gameStarted == true){
                        continue;
                    }
                    else {
                        key = child.getKey();
                        List<HashMap<String, Object>> players = room.players;
                        HashMap<String, Object> user = new HashMap<String, Object>();
                        user.put("username", username);
                        user.put("score", 0);
                        user.put("cards", new ArrayList<Card>());
                        players.add(user);
                        database.child("games").child(key).child("players").setValue(players);
                        break;
                    }
                }

            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
            }
        });
        if (key.equals("0")){
            tvAvailability.setVisibility(View.VISIBLE);
        }
        else {
            Intent intent = new Intent(this, waitingRoom.class);
            intent.putExtra("key", key);
            intent.putExtra("user", username);
            startActivity(intent);
        }
    }

}
