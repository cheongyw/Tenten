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
    private TextView errorMessage;
    private EditText etUsername;
    private EditText etCreateName;
    private EditText etJoinName;

    private DatabaseReference database;
    private String roomName;
    private String username;
    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //private static final String TAG = "createMultiplayerRoomActivity";

        errorMessage = (TextView) findViewById(R.id.textView_errorMessage);
        etUsername = (EditText) findViewById(R.id.editText_username);
        etCreateName = (EditText) findViewById(R.id.editText_createRoomName);
        etJoinName = (EditText) findViewById(R.id.editText_joinRoomName);

        database = FirebaseDatabase.getInstance().getReference();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_create);
    }

    public void start2PlayerGame(View view) {
        etUsername = (EditText) findViewById(R.id.editText_username);
        etCreateName = (EditText) findViewById(R.id.editText_createRoomName);
        errorMessage = (TextView) findViewById(R.id.textView_errorMessage);
        if (etUsername.getText().toString().equals("")) {
            errorMessage.setText("Please enter user name.");
        }
        else if (etCreateName.getText().toString().equals("")) {
            errorMessage.setText("Please enter room name.");
        }
        else {
            errorMessage.setText(null);
            roomName = etCreateName.getText().toString();
            String creator = etUsername.getText().toString();
            Room newRoom = new Room(roomName, 2, creator);
            Map<String, Object> newRoomValues = newRoom.toMap();

            key = database.child("games").push().getKey();
            Map<String, Object> childUpdate = new HashMap<>();
            childUpdate.put("/games/" + key, newRoomValues);
            database.updateChildren(childUpdate);

            Intent intent = new Intent(this, waitingRoom.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("key", key);
            intent.putExtra("user", username);
            startActivity(intent);
            finish();
        }
    }

    public void start4PlayerGame(View view) {
        etUsername = (EditText) findViewById(R.id.editText_username);
        etCreateName = (EditText) findViewById(R.id.editText_createRoomName);
        errorMessage = (TextView) findViewById(R.id.textView_errorMessage);
        if (etUsername.getText().toString().equals("")) {
            errorMessage.setText("Please enter user name.");
        }
        else if (etCreateName.getText().toString().equals("")) {
            errorMessage.setText("Please enter room name.");
        }
        else {
            errorMessage.setText(null);
            roomName = etCreateName.getText().toString();
            String creator = etUsername.getText().toString();
            Room newRoom = new Room(roomName, 4, creator);
            Map<String, Object> newRoomValues = newRoom.toMap();

            key = database.child("games").push().getKey();
            Map<String, Object> childUpdate = new HashMap<>();
            childUpdate.put("/games/" + key, newRoomValues);
            database.updateChildren(childUpdate);

            Intent intent = new Intent(this, waitingRoom.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("key", key);
            intent.putExtra("user", username);
            startActivity(intent);
            finish();
        }
    }

    public void joinGameName(View view) {
        etUsername = (EditText) findViewById(R.id.editText_username);
        etJoinName = (EditText) findViewById(R.id.editText_joinRoomName);
        errorMessage = (TextView) findViewById(R.id.textView_errorMessage);
        if (etUsername.getText().toString().equals("")) {
            errorMessage.setText("Please enter user name.");
        }
        else if (etJoinName.getText().toString().equals("")) {
            errorMessage.setText("Please enter room name.");
        }
        else {
            errorMessage.setText(null);
            //check if have too many ppl before joining!
            roomName = etJoinName.getText().toString();
            username = etUsername.getText().toString();

            database.child("games").equalTo("roomName").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot child: snapshot.getChildren()) {
                        if (!child.exists()) {
                            errorMessage.setText("Room Unavailable.");
                        } else {
                            errorMessage.setText(null);
                            Room room = child.getValue(Room.class);
                            key = child.getKey();
                            HashMap<String, HashMap<String, Object>> players = room.players;
                            HashMap<String, Object> user = new HashMap<>();
                            user.put("score", 0);
                            user.put("cards", new ArrayList<Card>());
                            players.put(username, user);
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
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("key", key);
            intent.putExtra("user", username);
            startActivity(intent);
            finish();
        }
    }

    public void join2PlayerGame(View view) {
        etUsername = (EditText) findViewById(R.id.editText_username);
        errorMessage = (TextView) findViewById(R.id.textView_errorMessage);
        if (etUsername.getText().toString().equals("")) {
            errorMessage.setText("Please enter user name.");
        }
        else {
            errorMessage.setText(null);
            username = etUsername.getText().toString();
            database.child("games").orderByChild("nPlayers").equalTo(2).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    key = "0";
                    Room room = new Room();
                    for (DataSnapshot child: snapshot.getChildren()) {
                        room = child.getValue(Room.class);
                        if (!child.exists()) {
                            errorMessage.setText("Room Unavailable.");
                        }
                        else if (room.gameStarted == true){
                            errorMessage.setText(null);
                            continue;
                        }
                        else {
                            errorMessage.setText(null);
                            key = child.getKey();
                            HashMap<String, HashMap<String, Object>> players = room.players;
                            HashMap<String, Object> user = new HashMap<>();
                            user.put("score", 0);
                            user.put("cards", new ArrayList<Card>());
                            players.put(username, user);
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
                errorMessage.setText("Room Unavailable.");
            }
            else {
                errorMessage.setText(null);
                Intent intent = new Intent(this, waitingRoom.class);
                intent.putExtra("key", key);
                intent.putExtra("user", username);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        }
    }

    public void join4PlayerGame(View view) {
        etUsername = (EditText) findViewById(R.id.editText_username);
        errorMessage = (TextView) findViewById(R.id.textView_errorMessage);
        if (etUsername.getText().toString().equals("")) {
            errorMessage.setText("Please enter user name.");
        }
        else {
            errorMessage.setText(null);
            username = etUsername.getText().toString();
            database.child("games").orderByChild("nPlayers").equalTo(4).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    key = "0";
                    Room room = new Room();
                    for (DataSnapshot child: snapshot.getChildren()) {
                        room = child.getValue(Room.class);
                        if (!child.exists()) {
                            errorMessage.setText("Room Unavailable.");
                        }
                        else if (room.gameStarted == true){
                            errorMessage.setText(null);
                            continue;
                        }
                        else {
                            errorMessage.setText(null);
                            key = child.getKey();
                            HashMap<String, HashMap<String, Object>> players = room.players;
                            HashMap<String, Object> user = new HashMap<>();
                            user.put("score", 0);
                            user.put("cards", new ArrayList<Card>());
                            players.put(username, user);
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
                errorMessage.setText("Room Unavailable.");
            }
            else {
                errorMessage.setText(null);
                Intent intent = new Intent(this, waitingRoom.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("key", key);
                intent.putExtra("user", username);
                startActivity(intent);
                finish();
            }
        }
    }

}
