package com.cheong.tenten;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ExplosiveActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explosive);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.explosiveActivity);
        AnimationDrawable explosion = (AnimationDrawable) layout.getBackground();
        explosion.start();

        Animation a = AnimationUtils.loadAnimation(this, R.anim.scale);
        TextView tv = (TextView) findViewById(R.id.explosiveText);
        tv.startAnimation(a);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                finish();
            }
        }, 4800);
    }
}
