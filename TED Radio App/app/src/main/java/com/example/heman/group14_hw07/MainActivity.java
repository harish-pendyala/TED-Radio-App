package com.example.heman.group14_hw07;

/*
Assignment: Homework07
File name: MainActivity.java
Full Name: Harish Pendyala, Hemanth Sai Thota
 */

import android.app.ProgressDialog;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements GetEpisodeListAsync.GetEpisodeList, MyMediaController.Controller {

    private RecyclerView episodesListContainer;
    private RecyclerView.LayoutManager container;
    private RecyclerView.Adapter<EpisodeListAdapter.ViewHolder> episodesDataset;
    private ProgressDialog dialog;
    private Toolbar toolbar;
    private Handler handler;
    private ArrayList<EpisodeInfo> episodeInfos;
    private MyMediaController myMediaController;
    private ImageView control;
    private SeekBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        control = (ImageView) findViewById(R.id.control);
        progress = (SeekBar) findViewById(R.id.play_progress);
        handler = new Handler();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading Episodes");
        dialog.setCancelable(false);
        dialog.show();

        episodesListContainer = (RecyclerView) findViewById(R.id.episodesList);
        episodesListContainer.setHasFixedSize(true);
        container =  new LinearLayoutManager(this);
        episodesListContainer.setLayoutManager(container);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.mipmap.ted_icon);
        toolbar.setTitle("TED Radio Hour Podcast");
        toolbar.setBackgroundColor(Color.BLACK);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        myMediaController = new MyMediaController();

        // Temperoray code starts

        /*//container = new LinearLayoutManager(this);
        container =  new GridLayoutManager(this,2);
        episodesListContainer.setLayoutManager(container);
        episodesDataset = new EpisodeListAdapter(new String[]{"Hello","Bye","Hemanth"});
        episodesListContainer.setAdapter(episodesDataset);

        // Temporary code ends*/
        if(!isConnected()) {
            Toast.makeText(this,"No Internet Connection",Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            return;
        }
        new GetEpisodeListAsync(this).execute("https://www.npr.org/rss/podcast.php?id=510298");

        control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myMediaController.isPlaying()) {
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
                if(myMediaController.isStarted())
                    myMediaController.seekTo(progress);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_refresh)
            if(container.getClass().equals(LinearLayoutManager.class)) {
                container = new GridLayoutManager(this,2);
                episodesListContainer.setLayoutManager(container);
                episodesListContainer.setAdapter(episodesDataset);
            } else {
                container =  new LinearLayoutManager(this);
                episodesListContainer.setLayoutManager(container);
                episodesListContainer.setAdapter(episodesDataset);
            }
        return true;
    }

    private boolean isConnected() {
        ConnectivityManager com = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = com.getActiveNetworkInfo();
        if(info != null && info.isConnected())
            return true;
        return false;
    }

    @Override
    public void getList(ArrayList<EpisodeInfo> episodeInfos) {
        dialog.dismiss();
        if(episodeInfos == null) {
            Toast.makeText(this,"Invalid Source..!",Toast.LENGTH_SHORT).show();
            return;
        }
        Collections.sort(episodeInfos, new Comparator<EpisodeInfo>() {
            @Override
            public int compare(EpisodeInfo o1, EpisodeInfo o2) {
                try {
                    Date date1 = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z").parse(o1.getPubDate());
                    Date date2 = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z").parse(o2.getPubDate());
                    return date2.compareTo(date1);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });
        this.episodeInfos = episodeInfos;
        episodesDataset = new EpisodeListAdapter(episodeInfos, this);
        episodesListContainer.setAdapter(episodesDataset);
    }

    public void onPlay(View v) {
        try {
            myMediaController.setMediaPlayer(v.getTag().toString(), MainActivity.this);
        } catch (IOException e) {
            Log.d("error",e.toString());
        }
    }

    @Override
    protected void onDestroy() {
        myMediaController.release();
        super.onDestroy();
    }

    @Override
    public void setControl(boolean visible) {
        if(visible) {
            progress.setMax(myMediaController.getDuration());
            progress.setProgress(0);
            control.setImageResource(R.drawable.pause1);
            control.setVisibility(View.VISIBLE);
            progress.setVisibility(View.VISIBLE);
            updateProgress();
        } else {
            control.setVisibility(View.INVISIBLE);
            progress.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onResume() {
        if(!this.equals(myMediaController.getController())) {
            control.setVisibility(View.INVISIBLE);
            progress.setVisibility(View.INVISIBLE);
        }
        super.onResume();
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
