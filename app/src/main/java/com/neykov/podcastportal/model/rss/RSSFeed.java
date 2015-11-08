package com.neykov.podcastportal.model.rss;

public class RSSFeed {

    private RssChannel rssChannel;

    public RSSFeed(RssChannel rssChannel) {
        this.rssChannel = rssChannel;
    }

    public RssChannel getChannel() {
        return rssChannel;
    }
}
