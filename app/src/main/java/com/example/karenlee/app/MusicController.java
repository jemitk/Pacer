package com.example.karenlee.app;

import android.content.Context;
import android.widget.MediaController;

public class MusicController extends MediaController {

    public MusicController(Context c){
        super(c);
    }

    // we will prevent it to hide after 3 seconds by overwriting this method
    public void hide(){}

}