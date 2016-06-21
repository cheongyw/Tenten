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

    private TextView tvTitle;
    private TextView tvRoomName;
    private ListView playerList;
    private Button buttonLeave;
    private Button buttonStart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        tvTitle = (TextView) findViewById(R.id.textView_players);
        tvRoomName = (TextView) findViewById(R.id.textView_roomName);
        playerList = (ListView) findViewById(R.id.listView);
        buttonLeave = (Button) findViewById(R.id.button_leave);
        buttonStart = (Button) findViewById(R.id.button_start);

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room);
        Intent intent = getIntent();
        String key = intent.getStringExtra("key");

    }

    public void leaveRoom(View view){
        //remove user name from database
        //go back to mainactivity
    }

    public void startRoom(View view){
        //check if number of players match
        //if no, set msg visibility
        //else move to multigameactivity
    }
}
