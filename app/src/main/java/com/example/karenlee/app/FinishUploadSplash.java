package com.example.karenlee.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class FinishUploadSplash extends AppCompatActivity {

    static final String TAG = "FINISH_UPLOAD_SPLASH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Creating this activity");
        setContentView(R.layout.activity_finish_upload_splash);

        returntoMain();
    }

    public void returntoMain(){
        Log.i(TAG, "Ending this activity.");
        finish();
    }
}
