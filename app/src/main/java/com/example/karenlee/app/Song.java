package com.example.karenlee.app;

import java.io.Serializable;

public class Song implements Serializable{

    private long id;
    private String title;
    private String artist;
    private double bpm = 0.0;
    private int duration = -1; // in seconds

    public Song(long songID, String songTitle, String songArtist) {
        id = songID;
        title = songTitle;
        artist = songArtist;
    }

    public Song(long songID, String songTitle, String songArtist, double songBpm) {
        this(songID, songTitle, songArtist);
        bpm = songBpm;
    }

    public Song(long songID, String songTitle, String songArtist, int duration, double songBpm) {
        this(songID, songTitle, songArtist, songBpm);
        this.duration = duration;
    }

    public long getID(){return id;}
    public String getTitle(){return title;}
    public String getArtist(){return artist;}
    public double getBpm(){return bpm;}
    public int getDuration(){return duration;}
    public void setBpm(double bpm) {
        this.bpm = bpm;
    }

    @Override
    public String toString() {
        return id + " " + title + " by " + artist + ", " + bpm + " bpm";
    }

    @Override
    public boolean equals(Object other) {
        return (this.title.equals(((Song)other).title) &&
                this.artist.equals(((Song)other).artist));
    }
}
