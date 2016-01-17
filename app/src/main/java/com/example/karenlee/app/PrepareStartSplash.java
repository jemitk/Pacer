package com.example.karenlee.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class PrepareStartSplash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        startActivity(runIntent);
        finish();
    }
}
