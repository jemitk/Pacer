package com.example.karenlee.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import com.example.karenlee.app.R;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class WorkoutsActivity extends AppCompatActivity {
    static final String TAG = "WORKOUTS_ACTIVITY";
    static final String EXTRA_WORKOUTS = "com.example.karenlee.extras.EXTRA_WORKOUTS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workouts);
        findViewById(R.id.increaseButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                moveToWorkout("Increase");
            }
        });

        findViewById(R.id.constantButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                moveToWorkout("V-shaped");
            }
        });

        findViewById(R.id.decreaseButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                moveToWorkout("Decrease");
            }
        });

    }

    public void moveToWorkout(String workoutType) {
        Intent workoutIntent = new Intent(this, WorkoutMusicActivity.class);
        workoutIntent.putExtra(EXTRA_WORKOUTS, workoutType);
        startActivity(workoutIntent);
    }
}
