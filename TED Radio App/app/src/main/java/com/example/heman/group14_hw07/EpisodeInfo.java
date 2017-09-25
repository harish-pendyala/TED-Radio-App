package com.example.heman.group14_hw07;

/*
Assignment: Homework07
File name: EpisodeInfo.java
Full Name: Harish Pendyala, Hemanth Sai Thota
 */

import android.graphics.Bitmap;
import android.util.Xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by heman on 3/3/2017.
 */
public class EpisodeInfo implements Serializable {

    private String title, duration, description, imageURL, pubDate, trailerURL;

    public String getTrailerURL() {
        return trailerURL;
    }

    public void setTrailerURL(String trailerURL) {
        this.trailerURL = trailerURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public static class EpisodesInfoParser extends DefaultHandler {

        private ArrayList<EpisodeInfo> episodeInfos;
        private EpisodeInfo episodeInfo;
        private StringBuffer contents;
        private boolean readItem;

        public ArrayList<EpisodeInfo> getEpisodeInfos() {
            return episodeInfos;
        }

        public static ArrayList<EpisodeInfo> parseInfo(InputStream in) throws IOException, SAXException {
            EpisodesInfoParser parser = new EpisodesInfoParser();
            Xml.parse(in, Xml.Encoding.UTF_8,parser);
            return parser.getEpisodeInfos();
        }

        @Override
        public void startDocument() throws SAXException {
            episodeInfos = new ArrayList<>();
            contents = new StringBuffer();
            readItem = false;
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if(localName.equals("item")) {
                episodeInfos.add(episodeInfo);
                readItem = false;
            }
            else if(localName.equals("title") && readItem)
                episodeInfo.setTitle(contents.toString().trim());
            else if(localName.equals("pubDate") && readItem)
                episodeInfo.setPubDate(contents.toString().trim());
            else if(qName.equals("itunes:duration") && readItem)
                episodeInfo.setDuration(contents.toString().trim());
            else if(localName.equals("description") && readItem)
                episodeInfo.setDescription(contents.toString().trim());
            contents.setLength(0);
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if(localName.equals("item")) {
                episodeInfo = new EpisodeInfo();
                readItem = true;
            }
            else if(qName.equals("itunes:image") && readItem)
                episodeInfo.setImageURL(attributes.getValue("href").trim());
            else if(localName.equals("enclosure") && readItem)
                episodeInfo.setTrailerURL(attributes.getValue("url").trim());
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            contents.append(ch, start, length);
        }
    }

    public EpisodeLoadImage getInstance(EpisodeListAdapter.ViewHolder holder, int id, String url) {
        return new EpisodeLoadImage(holder, id, url);
    }

    public class EpisodeLoadImage implements Serializable {
        private EpisodeListAdapter.ViewHolder holder;
        private int id;
        private String url;
        private Bitmap image;

        public EpisodeLoadImage(EpisodeListAdapter.ViewHolder holder, int id, String url) {
            this.holder = holder;
            this.id = id;
            this.url = url;
        }

        public boolean isSame() {
            /*if(id == holder.getTag())
                return true;*/
            return false;
        }

        public EpisodeListAdapter.ViewHolder getHolder() {
            return holder;
        }

        public void setHolder(EpisodeListAdapter.ViewHolder holder) {
            this.holder = holder;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Bitmap getImage() {
            return image;
        }

        public void setImage(Bitmap image) {
            this.image = image;
        }
    }

    @Override
    public String toString() {
        return "EpisodeInfo[" +
                "title='" + title + '\'' +
                ", duration='" + duration + '\'' +
                ", description='" + description + '\'' +
                ", imageURL='" + imageURL + '\'' +
                ", pubDate='" + pubDate + '\'' +
                ", trailerURL='" + trailerURL + '\'' +
                ']';
    }
}
