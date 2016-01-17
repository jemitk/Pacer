package com.example.karenlee.app;

import java.io.Serializable;

public class Song implements Serializable{

    private long id;
    private String title;
    private String artist;

    public Song(long songID, String songTitle, String songArtist) {
        id=songID;
        title=songTitle;
        artist=songArtist;
    }

    public long getID(){return id;}
    public String getTitle(){return title;}
    public String getArtist(){return artist;}

    @Override
    public String toString() {
        return id + " " + title + " by " + artist;
    }

    @Override
    public boolean equals(Object other) {
        return (this.title.equals(((Song)other).title) &&
                this.artist.equals(((Song)other).artist));
    }
}
