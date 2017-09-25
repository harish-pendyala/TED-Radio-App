package com.example.heman.group14_hw07;

/*
Assignment: Homework07
File name: EpisodeListAdapter.java
Full Name: Harish Pendyala, Hemanth Sai Thota
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by heman on 3/3/2017.
 */

public class EpisodeListAdapter extends RecyclerView.Adapter<EpisodeListAdapter.ViewHolder> implements LoadEpisodeIconAsync.LoadImage {

    private ArrayList<EpisodeInfo> episodeInfos;
    private Context context;

    public EpisodeListAdapter(ArrayList<EpisodeInfo> episodeInfos, Context context) {
        this.episodeInfos = episodeInfos;
        this.context = context;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder vh = null;
        if(((RecyclerView)parent).getLayoutManager().getClass().equals(LinearLayoutManager.class)) {
            LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.linear_view_layout, parent, false);
            vh = new ViewHolder(v, (TextView) v.findViewById(R.id.title_placer), (TextView) v.findViewById(R.id.date_placer), (ImageView) v.findViewById(R.id.episode_icon), (ImageView) v.findViewById(R.id.play_icon));
        } else {
            LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_view_layout, parent, false);
            vh = new ViewHolder(v, (TextView) v.findViewById(R.id.title_placer), (ImageView) v.findViewById(R.id.episode_icon));
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final EpisodeInfo episodeInfo = episodeInfos.get(position);
        Log.d("called",episodeInfo.getTitle());
        holder.titleHolder.setText(episodeInfo.getTitle());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Play.class);
                intent.putExtra("Object", episodeInfo);
                context.startActivity(intent);
            }
        });
        holder.iconHolder.setTag(episodeInfo.getTrailerURL());
        /*EpisodeInfo.EpisodeLoadImage obj = episodeInfo.getInstance(holder,position,episodeInfo.getImageURL());
        holder.setTag(position);*/
        if(holder.dateHolder != null) {
            try {
                Date date = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z").parse(episodeInfo.getPubDate());
                holder.dateHolder.setText("posted: " + new SimpleDateFormat("EEE, dd, MMM yyyy").format(date));
            } catch (Exception e) {
                Log.d("error", e.toString());
                holder.dateHolder.setText("posted: Null");
            }
        }
        try {
            Picasso.with(context).load(episodeInfo.getImageURL()).into(holder.imageHolder);
            return;
        } catch (Exception e) { }
        holder.imageHolder.setImageResource(android.R.drawable.sym_def_app_icon);
        //new LoadEpisodeIconAsync(this).execute(obj);
    }

    @Override
    public int getItemCount() {
        return episodeInfos.size();
    }

    @Override
    public void getImage(EpisodeInfo.EpisodeLoadImage episodeLoadImage) {
        if(episodeLoadImage!=null && episodeLoadImage.isSame()) {
            episodeLoadImage.getHolder().imageHolder.setImageBitmap(episodeLoadImage.getImage());
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView titleHolder, dateHolder;
        ImageView imageHolder, iconHolder;
        View view;
        //int tag;

        /*public int getTag() {
            return tag;
        }

        public void setTag(int tag) {
            this.tag = tag;
        }*/

        public ViewHolder(View itemView, TextView titleHolder, ImageView imageHolder) {
            super(itemView);
            this.view = itemView;
            this.titleHolder = titleHolder;
            this.imageHolder = imageHolder;
            this.iconHolder = imageHolder;
            this.dateHolder = null;
        }

        public ViewHolder(View itemView, TextView titleHolder, TextView dateHolder, ImageView imageHolder, ImageView iconHolder) {
            super(itemView);
            this.view = itemView;
            this.titleHolder = titleHolder;
            this.dateHolder = dateHolder;
            this.imageHolder = imageHolder;
            this.iconHolder = iconHolder;
        }
    }
}
