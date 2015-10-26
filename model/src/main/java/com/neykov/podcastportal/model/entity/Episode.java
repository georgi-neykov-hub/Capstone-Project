package com.neykov.podcastportal.model.entity;


import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;

public class Episode implements EpisodeData {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({REMOTE, DOWNLOADED, DOWNLOADING})
    @interface DownloadState {
    }

    public final static int REMOTE = 0;
    public final static int DOWNLOADING = 1;
    public final static int DOWNLOADED = 2;

    public Episode(long id, long podcastId, String title, String description, String contentUrl, String mimeType, String fileUrl, Long fileSize, int downloadState, String mediaLength, boolean watched, Long playlistEntryId, Date released) {
        this.id = id;
        this.podcastId = podcastId;
        this.title = title;
        this.description = description;
        this.contentUrl = contentUrl;
        this.mimeType = mimeType;
        this.fileUrl = fileUrl;
        this.fileSize = fileSize;
        this.downloadState = downloadState;
        this.mediaLength = mediaLength;
        this.watched = watched;
        this.playlistEntryId = playlistEntryId;
        this.released = released;
    }

    private long id;
    private long podcastId;
    private String title;
    private String description;
    private String contentUrl;
    private String mimeType;
    private String fileUrl;
    private Long fileSize;
    private
    @DownloadState
    int downloadState;
    private String mediaLength;
    private boolean watched;
    private Long playlistEntryId;
    private Date released;

    public long getId() {
        return id;
    }

    public long getPodcastId() {
        return podcastId;
    }

    public Long getPlaylistEntryId() {
        return playlistEntryId;
    }

    public String getMimeType() {
        return mimeType;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getContentUrl() {
        return contentUrl;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Date getReleased() {
        return released;
    }

    public String getMediaLength() {
        return mediaLength;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public
    @DownloadState
    int getDownloadState() {
        return downloadState;
    }

    public boolean isWatched() {
        return watched;
    }
}
