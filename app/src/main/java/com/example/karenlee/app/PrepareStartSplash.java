package com.example.karenlee.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.karenlee.app.songlists.SimpleSongListGenerator;

public class PrepareStartSplash extends AppCompatActivity {

    static final String TAG = "PREPARE_START_SPLASH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Creating this activity.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepare_start_splash);
        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startRunActivity();
            }
        });
    }

    public void startRunActivity(){
        Intent runIntent = new Intent(this, RunActivity.class);
        runIntent.putExtra(RunActivity.EXTRA_LIST_GENERATOR, new SimpleSongListGenerator());
        startActivity(runIntent);
        Log.i(TAG, "Exiting this activity.");
        finish();
    }
}
