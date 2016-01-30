package com.example.karenlee.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

/**
 * Created by brycewilley on 1/30/16.
 */
public class FinishSplash extends Activity {

    static final String TAG = "FINISH_SPLASH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Creating this activity.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_upload_splash);
        findViewById(R.id.imageView).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                done();
            }
        });
    }

    public void done(){
        finish();
    }
}
