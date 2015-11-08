package com.neykov.podcastportal.model.rss;

import java.util.Date;

public class RssItem {
    private String link;
    private String title;
    private String description;
    private Content content;
    private Date pubDate;

    public RssItem(String title, String description, Content content, Date pubDate, String link) {
        this.link = link;
        this.title = title;
        this.description = description;
        this.content = content;
        this.pubDate = pubDate;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Content getContent() {
        return content;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public String getLink() {
        return link;
    }
}
