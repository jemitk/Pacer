package com.example.karenlee.app.songlists;

import android.util.Log;

import com.example.karenlee.app.MusicService;
import com.example.karenlee.app.Song;
import com.example.karenlee.app.db.SongBPMDbHelper;

import java.util.ArrayList;

/**
 * A simple implementation of a generator. Takes the bpm, finds enough songs, and puts them in the
 * music player.
 * Created by brycewilley on 2/14/16.
 */
public class SimpleSongListGenerator implements ISongListGenerator {
    @Override
    public ArrayList<Song> fillList(MusicService srv, SongBPMDbHelper mDbHelper, double bpmGoal) {
        ArrayList<Song> list = new ArrayList<>();
        // TODO: Currently only finds one song, make it find 'more'
        list.add(mDbHelper.getBpmSong(bpmGoal));
        srv.setList(list);
        srv.setSong(0);
        return list;
    }
}
