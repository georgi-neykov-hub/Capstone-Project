package com.neykov.podcastportal.model.entity;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Podcast {

    @Expose
    private String website;
    @Expose
    private String description;
    @Expose
    private String title;
    @Expose
    private String url;
    @SerializedName("position_last_week")
    @Expose
    private int positionLastWeek;
    @SerializedName("subscribers_last_week")
    @Expose
    private int subscribersLastWeek;
    @Expose
    private int subscribers;
    @SerializedName("mygpo_link")
    @Expose
    private String mygpoLink;
    @SerializedName("logo_url")
    @Expose
    private String logoUrl;

    public String getWebsite() {
        return website;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public int getPositionLastWeek() {
        return positionLastWeek;
    }

    public int getSubscribersLastWeek() {
        return subscribersLastWeek;
    }

    public int getSubscribers() {
        return subscribers;
    }

    public String getMygpoLink() {
        return mygpoLink;
    }

    public String getLogoUrl() {
        return logoUrl;
    }
}