package com.example.karenlee.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.karenlee.app.songlists.WorkoutSongListGenerator;

/**
 * The midscreen before starting a workout. Contains the defintion of each workout.
 */
public class WorkoutsActivity extends AppCompatActivity {
    static final String TAG = "WORKOUTS_ACTIVITY";

    private double[] simpleIncrease = {0.0, 5.0, 10.0, 15.0, 20.0, 25.0, 30.0};
    private double[] simpleDecrease = {0.0, -5.0, -10.0, -15.0, -20.0, -25.0, -30.0};
    private double[] vWorkout = {0.0, -5.0, -10.0, -15.0, -10.0, -5.0, 0.0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workouts);
        findViewById(R.id.increaseButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                moveToWorkout(simpleIncrease);
            }
        });

        findViewById(R.id.constantButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                moveToWorkout(vWorkout);
            }
        });

        findViewById(R.id.decreaseButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                moveToWorkout(simpleDecrease);
            }
        });

    }

    public void moveToWorkout(double[] workoutType) {
        Intent workoutIntent = new Intent(this, RunActivity.class);
        workoutIntent.putExtra(RunActivity.EXTRA_LIST_GENERATOR,
                new WorkoutSongListGenerator(workoutType));
        startActivity(workoutIntent);
    }
}
