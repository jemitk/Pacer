package com.example.karenlee.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class FinishUploadSplash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_upload_splash);


        returntoMain();
    }

    public void returntoMain(){
        finish();
    }
}
