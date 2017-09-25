package com.example.heman.group14_hw07;

/*
Assignment: Homework07
File name: LoadEpisodeIconAsync.java
Full Name: Harish Pendyala, Hemanth Sai Thota
 */

import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by heman on 3/4/2017.
 */

public class LoadEpisodeIconAsync extends AsyncTask<EpisodeInfo.EpisodeLoadImage, Void, EpisodeInfo.EpisodeLoadImage> {

    LoadImage activity;

    public LoadEpisodeIconAsync(LoadImage activity) {
        this.activity = activity;
    }

    public interface LoadImage {
        void getImage(EpisodeInfo.EpisodeLoadImage episodeLoadImage);
    }

    @Override
    protected void onPostExecute(EpisodeInfo.EpisodeLoadImage episodeLoadImage) {
        this.activity.getImage(episodeLoadImage);
    }

    @Override
    protected EpisodeInfo.EpisodeLoadImage doInBackground(EpisodeInfo.EpisodeLoadImage... params) {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(params[0].getUrl()).openConnection();
            con.setRequestMethod("GET");
            if(con.getResponseCode() == HttpURLConnection.HTTP_OK)
                params[0].setImage(BitmapFactory.decodeStream(con.getInputStream()));
            return params[0];
        }  catch (Exception e) {
            Log.d("error",e.toString());
        }
        return null;
    }
}
