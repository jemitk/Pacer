package com.example.karenlee.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class NextSplash extends AppCompatActivity {

    static final String TAG = "NEXT_SPLASH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Creating this activity.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_splash);
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                returnToTapActivity();
            }
        });
    }

    public void returnToTapActivity(){
        finish();
    }
}
