package com.example.karenlee.app.songlists;

import com.example.karenlee.app.MusicService;
import com.example.karenlee.app.Song;
import com.example.karenlee.app.db.SongBPMDbHelper;

import java.util.ArrayList;

/**
 * Created by brycewilley on 2/14/16.
 */
public class WorkoutSongListGenerator implements ISongListGenerator {

    private final double[] myWorkoutFormat;

    /**
     * Constructor that takes in the format for the workout.
     * @param workoutFormat An array of doubles that MUST START WITH 0.0.
     *                      The array is a representation of the bpm offsets starting from the first
     *                      song.
     */
    public WorkoutSongListGenerator(double[] workoutFormat) {
        // if first entry in the array isn't 0, freak out
        if (workoutFormat.length == 0 || workoutFormat[0] != 0.0) {
            throw new IllegalStateException("Something wrong with the input array!");
        }
        this.myWorkoutFormat = workoutFormat;
    }

    @Override
    public ArrayList<Song> fillList(MusicService srv, SongBPMDbHelper mDbHelper, double bpm) {
        ArrayList<Song> list = new ArrayList<>();
        // TODO: just finds one song per bpm: make a version that dynamically corrects it according
        // TODO: (pt2) to the length of each song choosen/ takes length into account
        for (double offset : myWorkoutFormat) {
            list.add(mDbHelper.getBpmSongExcluding(bpm + offset, list));
        }
        srv.setList(list);
        srv.setSong(0);
        return list;
    }
}
