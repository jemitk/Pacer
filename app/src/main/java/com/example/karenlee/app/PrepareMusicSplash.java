package com.example.karenlee.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PrepareMusicSplash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepare_music_splash);

        try {
            Thread.sleep(3000);
        }catch(Exception e){
            e.printStackTrace();
        }

        returntoSetup();
    }

    public void returntoSetup(){
        finish();
    }
}
