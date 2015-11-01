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

    private long id;
    private long podcastId;
    private String title;
    private String description;
    private String contentUrl;
    private String mimeType;
    private String fileUrl;
    private Long fileSize;
    private String thumbnail;
    @DownloadState
    private int downloadState;
    private Long duration;
    private boolean watched;
    private Long playlistEntryId;
    private Date released;

    public Episode(long id, long podcastId, String title, String description, String contentUrl, String mimeType, String fileUrl, Long fileSize, int downloadState, Long duration, String thumbnail, boolean watched, Long playlistEntryId, Date released) {
        this.id = id;
        this.podcastId = podcastId;
        this.title = title;
        this.description = description;
        this.contentUrl = contentUrl;
        this.mimeType = mimeType;
        this.fileUrl = fileUrl;
        this.fileSize = fileSize;
        this.downloadState = downloadState;
        this.duration = duration;
        this.watched = watched;
        this.playlistEntryId = playlistEntryId;
        this.released = released;
        this.thumbnail = thumbnail;
    }

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

    public Long getDuration() {
        return duration;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public
    @DownloadState
    int getDownloadState() {
        return downloadState;
    }

    public boolean isWatched() {
        return watched;
    }

    public boolean canBePlayedLocally(){
        return downloadState == DOWNLOADED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Episode episode = (Episode) o;

        if (id != episode.id) return false;
        if (podcastId != episode.podcastId) return false;
        if (downloadState != episode.downloadState) return false;
        if (watched != episode.watched) return false;
        if (title != null ? !title.equals(episode.title) : episode.title != null) return false;
        if (description != null ? !description.equals(episode.description) : episode.description != null)
            return false;
        if (!contentUrl.equals(episode.contentUrl)) return false;
        if (mimeType != null ? !mimeType.equals(episode.mimeType) : episode.mimeType != null)
            return false;
        if (fileUrl != null ? !fileUrl.equals(episode.fileUrl) : episode.fileUrl != null)
            return false;
        if (fileSize != null ? !fileSize.equals(episode.fileSize) : episode.fileSize != null)
            return false;
        if (thumbnail != null ? !thumbnail.equals(episode.thumbnail) : episode.thumbnail != null)
            return false;
        if (duration != null ? !duration.equals(episode.duration) : episode.duration != null)
            return false;
        if (playlistEntryId != null ? !playlistEntryId.equals(episode.playlistEntryId) : episode.playlistEntryId != null)
            return false;
        return released.equals(episode.released);

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (podcastId ^ (podcastId >>> 32));
        result = 31 * result + contentUrl.hashCode();
        return result;
    }
}
