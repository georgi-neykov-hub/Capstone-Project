package com.neykov.podcastportal.model.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.neykov.podcastportal.model.entity.EpisodeData;

import java.util.Date;

public class RemoteEpisodeData implements EpisodeData {

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

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getContentUrl() {
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
