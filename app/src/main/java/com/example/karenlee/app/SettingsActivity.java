package com.example.karenlee.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.karenlee.app.db.SongBPMDbHelper;

public class SettingsActivity extends AppCompatActivity {

    static final String TAG = "SETTINGS_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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

    public void viewLibrary(View view) {
        Intent newIntent = new Intent(this, LibraryActivity.class);
        startActivity(newIntent);
    }

    public void bye(View view) {
        finish();
    }

    public void resetDatabase(View view) {
        SongBPMDbHelper dbHelper = SongBPMDbHelper.getInstance(this.getApplicationContext());
        dbHelper.resetDb();
        Log.w(TAG, "Reset database");
        Log.w(TAG, "After resetting the database, the items in db are: " + dbHelper.getSongs().toString());

        Toast.makeText(getApplicationContext(), "Database has been reset", Toast.LENGTH_SHORT).show();
    }
}
