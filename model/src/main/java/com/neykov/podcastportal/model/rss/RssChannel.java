package com.neykov.podcastportal.model.rss;

import java.util.List;

public class RssChannel {
    private String title;
    private String description;
    private List<RssItem> itemList;

    public RssChannel(String title, String description, List<RssItem> itemList) {
        this.title = title;
        this.description = description;
        this.itemList = itemList;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<RssItem> getItemList() {
        return itemList;
    }
}

