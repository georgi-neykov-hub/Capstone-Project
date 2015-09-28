package com.neykov.podcastportal.model.entity;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Episode {

    @Expose
    private String title;
    @Expose
    private String url;
    @SerializedName("podcast_title")
    @Expose
    private String podcastTitle;
    @SerializedName("podcast_url")
    @Expose
    private String podcastUrl;
    @Expose
    private String description;
    @Expose
    private String website;
    @Expose
    private String released;

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getPodcastTitle() {
        return podcastTitle;
    }

    public String getPodcastUrl() {
        return podcastUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getWebsite() {
        return website;
    }

    public String getReleased() {
        return released;
    }
}
