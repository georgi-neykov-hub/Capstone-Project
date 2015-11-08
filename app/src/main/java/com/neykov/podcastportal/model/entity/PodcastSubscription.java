package com.neykov.podcastportal.model.entity;

import android.os.Parcel;

import java.util.Date;

public class PodcastSubscription extends RemotePodcastData {
    private Long id;
    private String localLogoUrl;
    private Date dateUpdated;

    public PodcastSubscription(Long id, String title, String description, String url, String website, int subscribers, String logoUrl, String localLogoUrl, Date dateUpdated) {
        super(title, description, url, website, subscribers, logoUrl);
        this.id = id;
        this.localLogoUrl = localLogoUrl;
        this.dateUpdated = dateUpdated;
    }

    public Long getId() {
        return id;
    }

    public String getLocalLogoUrl() {
        return localLogoUrl;
    }

    public Date getDateUpdatedUtc() {
        return dateUpdated;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(this.id);
        dest.writeString(this.localLogoUrl);
        dest.writeLong(dateUpdated != null ? dateUpdated.getTime() : -1);
    }

    protected PodcastSubscription(Parcel in) {
        super(in);
        this.id = in.readLong();
        this.localLogoUrl = in.readString();
        long tmpDateUpdated = in.readLong();
        this.dateUpdated = tmpDateUpdated == -1 ? null : new Date(tmpDateUpdated);
    }

    public static final Creator<PodcastSubscription> CREATOR = new Creator<PodcastSubscription>() {
        public PodcastSubscription createFromParcel(Parcel source) {
            return new PodcastSubscription(source);
        }

        public PodcastSubscription[] newArray(int size) {
            return new PodcastSubscription[size];
        }
    };

    public static class Builder {
        private Long id;
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

        public Builder(RemotePodcastData p){
            this.title = p.getTitle();
            this.description = p.getDescription();
            this.url = p.getUrl();
            this.website = p.getWebsite();
            this.subscribers = p.getSubscribers();
            this.logoUrl = p.getLogoUrl();
        }

        public Builder(PodcastSubscription podcastSubscription){
            this.id = podcastSubscription.getId();
            this.title = podcastSubscription.getTitle();
            this.description = podcastSubscription.getDescription();
            this.url = podcastSubscription.getUrl();
            this.website = podcastSubscription.getWebsite();
            this.subscribers = podcastSubscription.getSubscribers();
            this.logoUrl = podcastSubscription.getLogoUrl();
            this.localLogoUrl = podcastSubscription.getLocalLogoUrl();
            this.dateUpdated = podcastSubscription.getDateUpdatedUtc();
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

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public PodcastSubscription build() {
            return new PodcastSubscription(id, title, description, url, website, subscribers, logoUrl, localLogoUrl, dateUpdated);
        }
    }
}
