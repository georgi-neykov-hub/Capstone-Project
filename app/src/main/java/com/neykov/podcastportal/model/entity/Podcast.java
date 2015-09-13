package com.neykov.podcastportal.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import javax.annotation.Generated;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Podcast implements Parcelable, Comparable<Podcast> {

    @Expose
    private String title;
    @Expose
    private String description;
    @Expose
    private String url;
    @Expose
    private String website;
    @Expose
    private int subscribers;
    @SerializedName("logo_url")
    @Expose
    private String logoUrl;

    protected Podcast(String title, String description, String url, String website, int subscribers, String logoUrl) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.website = website;
        this.subscribers = subscribers;
        this.logoUrl = logoUrl;
    }

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

    public int getSubscribers() {
        return subscribers;
    }

    public String getLogoUrl() {
        return logoUrl;
    }


    public Podcast() {
    }

    @Override
    public int compareTo(Podcast another) {
        int thisSubscribers = this.getSubscribers();
        int thatSubscribers = another.getSubscribers();
        return thisSubscribers > thatSubscribers ? 1 : thisSubscribers == thatSubscribers ? 0 : -1;
    }

    public final class Builder {
        private String title;
        private String description;
        private String url;
        private String website;
        private int subscribers;
        private String logoUrl;

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setWebsite(String website) {
            this.website = website;
            return this;
        }

        public Builder setSubscribers(int subscribers) {
            this.subscribers = subscribers;
            return this;
        }

        public Builder setLogoUrl(String logoUrl) {
            this.logoUrl = logoUrl;
            return this;
        }

        public Podcast createPodcast() {
            return new Podcast(title, description, url, website, subscribers, logoUrl);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.url);
        dest.writeString(this.website);
        dest.writeInt(this.subscribers);
        dest.writeString(this.logoUrl);
    }

    protected Podcast(Parcel in) {
        this.title = in.readString();
        this.description = in.readString();
        this.url = in.readString();
        this.website = in.readString();
        this.subscribers = in.readInt();
        this.logoUrl = in.readString();
    }

    public static final Creator<Podcast> CREATOR = new Creator<Podcast>() {
        public Podcast createFromParcel(Parcel source) {
            return new Podcast(source);
        }

        public Podcast[] newArray(int size) {
            return new Podcast[size];
        }
    };
}