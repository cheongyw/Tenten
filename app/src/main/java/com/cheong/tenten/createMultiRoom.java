package com.cheong.tenten;

/**
 * Created by Sherry on 17/06/2016.
 */
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class createMultiRoom extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_create);
    }

    public void start2PlayerGame(View view) {
        Intent intent = new Intent(this, MultiGameActivity.class);
        startActivity(intent);
    }

    public void start4PlayerGame(View view) {
        Intent intent = new Intent(this, MultiGameActivity4P.class);
        startActivity(intent);
    }
}
