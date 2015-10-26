package com.neykov.podcastportal.model.entity;

public class PlaylistEntry {
    private long id;
    private Long previousItemId;
    private Long nextItemId;
    private Episode episode;
    private String podcastTitle;

    public PlaylistEntry(long id, Long previousItemId, Long nextItemId, Episode episode, String podcastTitle) {
        this.id = id;
        this.previousItemId = previousItemId;
        this.nextItemId = nextItemId;
        this.episode = episode;
        this.podcastTitle = podcastTitle;
    }

    public long getId() {
        return id;
    }

    public Long getNextItemId() {
        return nextItemId;
    }

    public Long getPreviousItemId() {
        return previousItemId;
    }

    public Episode getEpisode() {
        return episode;
    }

    public String getPodcastTitle() {
        return podcastTitle;
    }
}
