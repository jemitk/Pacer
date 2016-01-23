package com.example.karenlee.app;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import android.net.Uri;
import android.content.ContentResolver;
import android.database.Cursor;
import android.widget.EditText;

import com.example.karenlee.app.db.SongBPMDbHelper;

public class SetupActivity extends AppCompatActivity implements ExplainDialogFragment.OnDialogFragmentListener {
    static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    static final String TAG = "SETUP_ACTIVITY";
    static final String EXTRA_PERMISSION = "com.example.karenlee.extras.EXTRA_PERMISSON";

    private boolean isSetup = false;
    private boolean havePermissions = false;
    private ArrayList<Song> localSongList;
    private ArrayList<Song> addedSongList = new ArrayList<>();
    static final String EXTRA_SONGS = "com.example.karenlee.extras.EXTRA_SONGS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setup);

        // retrieve the ListView instance using the ID we gave it in the main layout
        //songView = (ListView)findViewById(R.id.song_list);
        // instantiate the list
        localSongList = new ArrayList<>();
        havePermissions = preGetSongList();
        if (havePermissions) {
            // we have all of the things we need to have to get the song list!
            getSongList();
            // sort the data alphabetically
            Collections.sort(localSongList, new Comparator<Song>() {
                public int compare(Song a, Song b) {
                    return a.getTitle().compareTo(b.getTitle());
                }
            });

            compareToDb(localSongList);
        } else {
            // we don't have the things (permissions) to get the song list
            // wait until we get those
            Log.w(TAG, "Don't have proper permissions, waiting to see if we got those");
        }
    }

    @Override
    protected void onStart() {
        if (havePermissions) {
            Log.d(TAG, "Added songs (pre-bpm) are " + addedSongList.toString() + ", isSetup is " + isSetup);
            // If there are no songs to add to the database,
            if (addedSongList.size() == 0 || isSetup) {
                Log.i(TAG, "Songs already in DB; finishing the activity");
                Intent intent = new Intent();
                intent.putExtra(EXTRA_PERMISSION, true);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                Log.i(TAG, "Starting Bpm");
                goToBPM();
                isSetup = true;
            }
        } else {
            Log.i(TAG, "Still don't have permissions");
        }

        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch(requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // we can read things!!
                    Log.d(TAG, "User allowed use to read external storage");
                    getSongList();
                } else {
                    // we can't really do anything. Show a dialogue pop up to the user, and stop the app.
                    Log.e(TAG, "User didn't allow use to read external storage. Exiting the app");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(SetupActivity.this);
                            builder.setMessage(R.string.no_permissions_dialogue_message)
                                    .setTitle(R.string.no_permissions_dialogue_title)
                                    .setNeutralButton("Ok, I'll think about it.", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Exit the application.
                                            die();
                                        }
                                    });
                            builder.create().show();
                        }
                    });
                }
                break;
            default:
                Log.w(TAG, "Why did we request " + requestCode + " again?");
        }
    }

    public void die() {
        // finish, telling the previous activity that you failed
        Intent intent = new Intent();
        intent.putExtra(EXTRA_PERMISSION, false);
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    // get the list of songs
    public void getSongList() {
        //retrieve song info
        ContentResolver musicResolver = getContentResolver();
        // retrieve the URI for external music files
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Log.d(TAG, "Querying the media store of the user");
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);

        // iterate over the results
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            int isMusicColumn = musicCursor.getColumnIndex(
                    android.provider.MediaStore.Audio.Media.IS_MUSIC);

            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                boolean thisIsMusic = Integer.parseInt(musicCursor.getString(isMusicColumn)) != 0;

                if (thisIsMusic)
                    localSongList.add(new Song(thisId, thisTitle, thisArtist));
            }
            while (musicCursor.moveToNext());
            musicCursor.close();
        }

    }

    /**
     * A method that does the permissions screening for reading media, if on Marshmallow.
     */
    public boolean preGetSongList() {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            Log.d(TAG, "We're running on marshmallow");
            // we need to explicitly check for permissions to read music. Do so.
            // if we don't have the permissions to look at the user's music, try to get them
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Log.d(TAG, "System is telling us that we should explain why we're asking for storage access");
                    // show the user a reason that we're asking them for their music
                    // TODO
                    ExplainDialogFragment newDialog = ExplainDialogFragment.newInstance();
                    newDialog.show(getFragmentManager(), "???");
                } else {
                    // just ask for permissions
                    Log.i(TAG, "Asking for permission to access storage");
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                Log.d(TAG, "User already allowed up to access external storage, so let's do that");
                return true;
            }
        } else {
            Log.d(TAG, "We're running on a system older than marshmallow");
            // just go straight to the list: we dealt with permissions at install time
            return true;
        }
    }

    public void goToBPM(){
        Log.i(TAG, addedSongList.toString());
        Intent bpmIntent = new Intent(this, BPMMusicFinderActivity.class);
        bpmIntent.putExtra(EXTRA_SONGS, addedSongList);
        startActivity(bpmIntent);
    }

    public void goToRunning() {
        Intent runIntent = new Intent(this, RunActivity.class);
        startActivity(runIntent);
    }

    public void compareToDb(ArrayList<Song> localSongList) {
        SongBPMDbHelper dbHelper = SongBPMDbHelper.getInstance(this.getApplicationContext());

        // TODO: for debugging purposes only. deletes all of the data in db
        // dbHelper.resetDb();
        //Log.i(TAG, "Reset database");
        //Log.i(TAG, "After resetting the database, the items in db are: " + dbHelper.getSongs().toString());

        ArrayList<Song> dbSongList = dbHelper.getSongs();

        ArrayList<Song> deleteSongList = new ArrayList<Song>();

        // Get the add song list
        for (Song localSong : localSongList) {
            if (!dbSongList.contains(localSong))
                addedSongList.add(localSong);
        }

        Log.i(TAG, "songs in local machine - " + localSongList.toString());
        Log.i(TAG, "songs in database - " + dbHelper.getSongs().toString());
        Log.i(TAG, "songs that should be added - " + addedSongList.toString());
        // get the delete song list
        for (Song dbSong: dbSongList) {
            if (!localSongList.contains(dbSong))
                deleteSongList.add(dbSong);
        }
        Log.i(TAG, "songs that should be deleted - " + deleteSongList.toString());
        if (deleteSongList.size() != 0) {
            dbHelper.deleteSongs(deleteSongList);
        }
        Log.i(TAG, "songs in database after deleting - " + dbHelper.getSongs().toString());
        // no need to add songs;
        // the addedSongList gets sent to the BPMMusicFinderActivity
        // and the BPMMusicFinderActivity adds the songs to the db with the bpm
        //dbHelper.addSongs(addedSongList);
    }

    @Override
    public void onDialogBye() {
        // The user has seen the dialog, refresh
        ActivityCompat.requestPermissions(SetupActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
    }
}
