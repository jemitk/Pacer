package com.example.karenlee.app;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.karenlee.app.db.SongBPMDbHelper;

public class MainActivity extends AppCompatActivity {

    static final String TAG = "MAIN_ACTIVITY";

    static final int SETUP_ACTIVITY_CODE = 0;

    // note: on create gets called when the screen rotates
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.startButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startVoidActivity(PrepareStartSplash.class);
            }
        });
        findViewById(R.id.workoutsButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startVoidActivity(WorkoutsActivity.class);
            }
        });
        findViewById(R.id.settingsButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startVoidActivity(SettingsActivity.class);
            }
        });
        startMusicUpload();
    }

    /**
     * Starts an activity that doesn't need a result / return value.
     * @param newActivity The class of the new activity to create (Foo.class)
     */
    public void startVoidActivity(Class<?> newActivity) {
        Intent newIntent = new Intent(this, newActivity);
        startActivity(newIntent);
    }

    public void startMusicUpload(){
        Intent setupIntent = new Intent(this, SetupActivity.class);
        startActivityForResult(setupIntent, SETUP_ACTIVITY_CODE);
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param newItem the new ingredient to add to the list
     */
    public void onActivityResult(int requestCode, int resultCode, Intent newItem){
        // gets the new item from the new window
    Log.d(TAG, "Request code:" + requestCode);
        if(requestCode == SETUP_ACTIVITY_CODE){
            if (resultCode == RESULT_OK){
                Log.v(TAG, "Everything went okay from setup activity");
            }
            if (resultCode == RESULT_CANCELED) {
                // if canceled, check to see if it was a permissions issue.
                if (!newItem.getBooleanExtra(SetupActivity.EXTRA_PERMISSION, true)) {
                    // something went wrong! Quit the app!
                    finish();
                }
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();

    }
}
