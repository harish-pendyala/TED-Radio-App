package com.example.heman.group14_hw07;

/*
Assignment: Homework07
File name: Play.java
Full Name: Harish Pendyala, Hemanth Sai Thota
 */

import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Play extends AppCompatActivity implements MyMediaController.Controller {

    private ImageView control;
    private SeekBar progress;
    private Handler handler;
    private boolean initiated;

    private MyMediaController myMediaController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        control = (ImageView) findViewById(R.id.control);
        progress = (SeekBar) findViewById(R.id.play_progress);
        myMediaController = MyMediaController.getInstance();
        handler = new Handler();
        initiated = false;

        final EpisodeInfo episodeInfo = (EpisodeInfo) getIntent().getExtras().getSerializable("Object");

        ((TextView)findViewById(R.id.title_placer)).setText(episodeInfo.getTitle());
        try {
            Date date = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z").parse(episodeInfo.getPubDate());
            ((TextView)findViewById(R.id.date_placer)).setText("Publication Date: "+new SimpleDateFormat("MM/dd/yyyy").format(date));
        } catch (ParseException e) {
            ((TextView)findViewById(R.id.date_placer)).setText("Null");
        }
        Picasso.with(this).load(episodeInfo.getImageURL()).into(((ImageView)findViewById(R.id.imageView)));
        ((TextView)findViewById(R.id.desc_placer)).setText("Description: "+episodeInfo.getDescription());
        ((TextView)findViewById(R.id.desc_placer)).setMovementMethod(new ScrollingMovementMethod());
        ((TextView)findViewById(R.id.duration_placer)).setText("Duration: "+episodeInfo.getDuration());

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setLogo(R.mipmap.ted_icon);
        toolbar.setTitle("Play!");
        toolbar.setBackgroundColor(Color.BLACK);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!initiated) {
                    try {
                        myMediaController.setMediaPlayer(episodeInfo.getTrailerURL(),Play.this);
                        initiated = true;
                    } catch (IOException e) { }
                    return;
                }
                else if(myMediaController.isPlaying()) {
                    control.setImageResource(R.drawable.play2);
                    myMediaController.pause();
                }
                else {
                    control.setImageResource(R.drawable.pause1);
                    myMediaController.resume();
                }
            }
        });

        progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(myMediaController.isStarted() && initiated)
                    myMediaController.seekTo(progress);
                else
                    seekBar.setProgress(0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    @Override
    public void setControl(boolean visible) {
        if(visible) {
            progress.setMax(myMediaController.getDuration());
            progress.setProgress(0);
            updateProgress();
            control.setImageResource(R.drawable.pause1);
        }
        else {
            progress.setProgress(0);
            control.setImageResource(R.drawable.play2);
            initiated = false;
        }
    }

    @Override
    protected void onDestroy() {
        if(initiated)
            myMediaController.stop();
        super.onDestroy();
    }

    protected void updateProgress() {
        if(this.equals(myMediaController.getController()) && myMediaController.isStarted()) {
            progress.setProgress(myMediaController.getCurrentPosition());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateProgress();
                }
            }, 1000);
        }
    }
}
