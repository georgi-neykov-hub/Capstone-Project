package com.neykov.podcastportal.model.entity;

import android.os.Parcel;

import java.util.Date;

public class Subscription extends Podcast {
    private String localLogoUrl;
    private Date dateUpdated;

    /*public Subscription(Podcast p, String localLogoUrl, Date dateUpdated) {
        this(p.getTitle(), p.getDescription(), p.getUrl(), p.getWebsite(), p.getSubscribers(), p.getLogoUrl(), localLogoUrl, dateUpdated);
    }*/

    public Subscription(String title, String description, String url, String website, int subscribers, String logoUrl, String localLogoUrl, Date dateUpdated) {
        super(title, description, url, website, subscribers, logoUrl);
        this.localLogoUrl = localLogoUrl;
        this.dateUpdated = dateUpdated;
    }

    public String getLocalLogoUrl() {
        return localLogoUrl;
    }

    public Date getDateUpdated() {
        return dateUpdated;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.localLogoUrl);
        dest.writeLong(dateUpdated != null ? dateUpdated.getTime() : -1);
    }

    protected Subscription(Parcel in) {
        super(in);
        this.localLogoUrl = in.readString();
        long tmpDateUpdated = in.readLong();
        this.dateUpdated = tmpDateUpdated == -1 ? null : new Date(tmpDateUpdated);
    }

    public static final Creator<Subscription> CREATOR = new Creator<Subscription>() {
        public Subscription createFromParcel(Parcel source) {
            return new Subscription(source);
        }

        public Subscription[] newArray(int size) {
            return new Subscription[size];
        }
    };

    public static class Builder {
        private String title;
        private String description;
        private String url;
        private String website;
        private int subscribers;
        private String logoUrl;
        private String localLogoUrl;
        private Date dateUpdated;

        public Builder(){

        }

        public Builder(Podcast p){
            this.title = p.getTitle();
            this.description = p.getDescription();
            this.url = p.getUrl();
            this.website = p.getWebsite();
            this.subscribers = p.getSubscribers();
            this.logoUrl = p.getLogoUrl();
        }

        public Builder(Subscription subscription){
            this.title = subscription.getTitle();
            this.description = subscription.getDescription();
            this.url = subscription.getUrl();
            this.website = subscription.getWebsite();
            this.subscribers = subscription.getSubscribers();
            this.logoUrl = subscription.getLogoUrl();
            this.localLogoUrl = subscription.getLocalLogoUrl();
            this.dateUpdated = subscription.getDateUpdated();
        }

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

        public Builder setLocalLogoUrl(String localLogoUrl) {
            this.localLogoUrl = localLogoUrl;
            return this;
        }

        public Builder setDateUpdated(Date dateUpdated) {
            this.dateUpdated = dateUpdated;
            return this;
        }

        public Subscription build() {
            return new Subscription(title, description, url, website, subscribers, logoUrl, localLogoUrl, dateUpdated);
        }
    }
}
