package com.example.heman.group14_hw07;

/*
Assignment: Homework07
File name: GetEpisodeListAsync.java
Full Name: Harish Pendyala, Hemanth Sai Thota
 */

import android.os.AsyncTask;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by heman on 3/3/2017.
 */

public class GetEpisodeListAsync extends AsyncTask<String, Void, ArrayList<EpisodeInfo>> {

    GetEpisodeList activity;

    public GetEpisodeListAsync(GetEpisodeList activity) {
        this.activity = activity;
    }

    public interface GetEpisodeList {
        void getList(ArrayList<EpisodeInfo> episodeInfos);
    }

    @Override
    protected ArrayList<EpisodeInfo> doInBackground(String... params) {
        try {
            URL url = new URL(params[0]);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            if(con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return EpisodeInfo.EpisodesInfoParser.parseInfo(con.getInputStream());
            }
        } catch (Exception e) {
            Log.d("error",e.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<EpisodeInfo> episodeInfos) {
        this.activity.getList(episodeInfos);
    }
}
