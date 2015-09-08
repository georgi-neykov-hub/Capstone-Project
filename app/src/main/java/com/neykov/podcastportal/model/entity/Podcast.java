package com.neykov.podcastportal.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Podcast implements Parcelable, Comparable<Podcast> {

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



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.website);
        dest.writeString(this.description);
        dest.writeString(this.title);
        dest.writeString(this.url);
        dest.writeInt(this.positionLastWeek);
        dest.writeInt(this.subscribersLastWeek);
        dest.writeInt(this.subscribers);
        dest.writeString(this.mygpoLink);
        dest.writeString(this.logoUrl);
    }

    public Podcast() {
    }

    protected Podcast(Parcel in) {
        this.website = in.readString();
        this.description = in.readString();
        this.title = in.readString();
        this.url = in.readString();
        this.positionLastWeek = in.readInt();
        this.subscribersLastWeek = in.readInt();
        this.subscribers = in.readInt();
        this.mygpoLink = in.readString();
        this.logoUrl = in.readString();
    }

    public static final Parcelable.Creator<Podcast> CREATOR = new Parcelable.Creator<Podcast>() {
        public Podcast createFromParcel(Parcel source) {
            return new Podcast(source);
        }

        public Podcast[] newArray(int size) {
            return new Podcast[size];
        }
    };

    @Override
    public int compareTo(Podcast another) {
        int thisSubscribers = this.getSubscribers();
        int thatSubscribers = another.getSubscribers();
        return thisSubscribers > thatSubscribers ? 1 : thisSubscribers == thatSubscribers ? 0 : -1;
    }
}