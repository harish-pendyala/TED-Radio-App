package com.example.heman.group14_hw07;

/*
Assignment: Homework07
File name: MyMediaController.java
Full Name: Harish Pendyala, Hemanth Sai Thota
 */

import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by heman on 3/8/2017.
 */

public class MyMediaController implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    private MediaPlayer mediaPlayer;
    private boolean playing, started;
    private Controller controller;
    private static MyMediaController instance;

    public MyMediaController() {
        playing = false;
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        instance = this;
    }

    public static MyMediaController getInstance() {
        return instance;
    }

    public Controller getController() {
        return controller;
    }

    public void setMediaPlayer(String url, Controller controller) throws IOException {
        controller.setControl(false);
        started = false;
        mediaPlayer.reset();
        mediaPlayer.setDataSource(url);
        mediaPlayer.prepareAsync();
        this.controller = controller;
    }

    public boolean isStarted() {
        return started;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mediaPlayer.start();
        playing = true;
        started = true;
        controller.setControl(true);
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public void seekTo(int seekPosition) {
        mediaPlayer.seekTo(seekPosition);
    }

    public void pause() {
        mediaPlayer.pause();
        playing = false;
    }

    public void resume() {
        mediaPlayer.start();
        playing = true;
    }

    public void release() {
        mediaPlayer.release();
    }


    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public boolean isPlaying() {
        return playing;
    }

    public void stop() {
        mediaPlayer.stop();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        controller.setControl(false);
        started = false;
    }

    public interface Controller {
        void setControl(boolean visible);
    }

}
