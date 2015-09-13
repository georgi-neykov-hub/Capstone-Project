package com.neykov.podcastportal.model.entity;

import java.util.Date;

public class SubscriptionBuilder {
    private String title;
    private String description;
    private String url;
    private String website;
    private int subscribers;
    private String logoUrl;
    private String localLogoUrl;
    private Date dateUpdated;

    public SubscriptionBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public SubscriptionBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public SubscriptionBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    public SubscriptionBuilder setWebsite(String website) {
        this.website = website;
        return this;
    }

    public SubscriptionBuilder setSubscribers(int subscribers) {
        this.subscribers = subscribers;
        return this;
    }

    public SubscriptionBuilder setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
        return this;
    }

    public SubscriptionBuilder setLocalLogoUrl(String localLogoUrl) {
        this.localLogoUrl = localLogoUrl;
        return this;
    }

    public SubscriptionBuilder setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
        return this;
    }

    public Subscription createSubscription() {
        return new Subscription(title, description, url, website, subscribers, logoUrl, localLogoUrl, dateUpdated);
    }
}