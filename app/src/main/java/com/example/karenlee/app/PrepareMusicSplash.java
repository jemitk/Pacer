package com.example.karenlee.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class PrepareMusicSplash extends AppCompatActivity {

    static final String TAG = "PREPARE_MUSIC_SPLASH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Creating splash.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepare_music_splash);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                returntoSetup();

            }
        });

    }

    public void returntoSetup(){
        finish();
    }
}
