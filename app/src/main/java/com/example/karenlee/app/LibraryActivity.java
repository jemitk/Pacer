package com.example.karenlee.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.karenlee.app.db.SongBPMDbHelper;

import java.util.ArrayList;

public class LibraryActivity extends AppCompatActivity {


    /** Use this to access the database. */
    private SongBPMDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        ListView list = (ListView) findViewById(R.id.listView);

        mDbHelper = new SongBPMDbHelper(getApplicationContext());

        final ArrayList<Song> songs = mDbHelper.getSongs();

        ArrayAdapter<Song> adapter = new ArrayAdapter<Song>(this,
                R.layout.list_item, songs){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view;
                TextView txtTitle;
                TextView txtArtist;
                TextView txtBpm;

                if (convertView == null) {
                    view =  LayoutInflater.from(LibraryActivity.this).inflate(R.layout.list_item, parent, false);
                } else {
                    view = convertView;
                }
                try {
                    //  Otherwise, find the TextView field within the layout
                    txtTitle = (TextView) view.findViewById(R.id.txtTitle);
                    txtArtist = (TextView) view.findViewById(R.id.txtArtist);
                    txtBpm = (TextView) view.findViewById(R.id.txtBpm);
                } catch (ClassCastException e) {
                    Log.e("ArrayAdapter", "You must supply a resource ID for a TextView");
                    throw new IllegalStateException(
                            "ArrayAdapter requires the resource ID to be a TextView", e);
                }

                Song item = getItem(position);
                if (item.getTitle().length() < 30)
                    txtTitle.setText(item.getTitle());
                else
                    txtTitle.setText(item.getTitle().substring(0, 30) + "...");
                txtArtist.setText(item.getArtist());
                txtBpm.setText("" + (int) item.getBpm());

                return view;
            }
        };

        list.setAdapter(adapter);

        // Create a message handling object as an anonymous class.
        AdapterView.OnItemClickListener mMessageClickedHandler = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                Intent retapSongIntent = new Intent(LibraryActivity.this, BPMMusicFinderActivity.class);
                ArrayList<Song> retapSongs = new ArrayList<>();
                retapSongs.add(songs.get(position));
                retapSongIntent.putExtra(BPMMusicFinderActivity.EXTRA_SONGS, retapSongs);
                startActivity(retapSongIntent);
            }
        };

        list.setOnItemClickListener(mMessageClickedHandler);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_library, menu);
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
}
