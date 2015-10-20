package com.neykov.podcastportal.model.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


import java.util.Date;

public class Episode implements EpisodeData {

    public Episode(long podcastId, String title, String url, String mimeType, String description, Date released) {
        this.podcastId = podcastId;
        this.title = title;
        this.url = url;
        this.mimeType = mimeType;
        this.description = description;
        this.released = released;
    }

    private long podcastId;
    private long id;

    private String mimeType;

    @Expose
    @SerializedName("title")
    private String title;

    @Expose
    @SerializedName("url")
    private String url;

    @Expose
    @SerializedName("description")
    private String description;

    @Expose
    @SerializedName("released")
    private Date released;

    public long getPodcastId(){
        return podcastId;
    }

    public String getMimeType() {
        return mimeType;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Date getReleased() {
        return released;
    }

}
