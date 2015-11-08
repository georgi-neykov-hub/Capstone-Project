package com.neykov.podcastportal.model.entity;


import android.support.annotation.Nullable;

import com.neykov.podcastportal.model.persistence.DatabaseContract;

import java.util.Date;

public class Episode implements EpisodeData {

    private long id;
    private long podcastId;
    private String title;
    private String description;
    private String contentUrl;
    private String mimeType;
    private String fileUrl;
    private Long fileSize;
    private String thumbnail;
    
    @DatabaseContract.Episode.DownloadState
    private int downloadState;
    private Long downloadId;
    private Long duration;
    private boolean watched;
    private Long playlistEntryId;
    private Date released;

    public Episode(long id,
                   long podcastId,
                   String title,
                   String description,
                   String contentUrl,
                   String mimeType,
                   String fileUrl,
                   Long fileSize,
                   @DatabaseContract.Episode.DownloadState int downloadState,
                   Long downloadId,
                   Long duration,
                   String thumbnail,
                   boolean watched,
                   Long playlistEntryId,
                   Date released) {
        this.id = id;
        this.podcastId = podcastId;
        this.title = title;
        this.description = description;
        this.contentUrl = contentUrl;
        this.mimeType = mimeType;
        this.fileUrl = fileUrl;
        this.fileSize = fileSize;
        this.downloadState = downloadState;
        this.downloadId = downloadId;
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
    @DatabaseContract.Episode.DownloadState
    int getDownloadState() {
        return downloadState;
    }

    public @Nullable Long getDownloadId(){
        return downloadId;
    }
    
    public boolean isWatched() {
        return watched;
    }

    public boolean canBePlayedLocally(){
        return downloadState == DatabaseContract.Episode.DOWNLOADED;
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
    
    public static class Builder {

        private long id;
        private long podcastId;
        private String title;
        private String description;
        private String contentUrl;
        private String mimeType;
        private String fileUrl;
        private Long fileSize;
        private int downloadState;
        private Long downloadId;
        private Long duration;
        private String thumbnail;
        private boolean watched;
        private Long playlistEntryId;
        private Date released;

        public Builder(Episode episode) {
            if(episode == null){
                throw new IllegalArgumentException("Null episode provided.");
            }
            
            this.id = episode.id;
            this.podcastId = episode.podcastId;
            this.title = episode.title;
            this.description = episode.description;
            this.contentUrl = episode.contentUrl;
            this.mimeType = episode.mimeType;
            this.fileUrl = episode.fileUrl;
            this.fileSize = episode.fileSize;
            this.downloadState = episode.downloadState;
            this.downloadId = episode.downloadId;
            this.duration = episode.duration;
            this.thumbnail = episode.thumbnail;
            this.watched = episode.watched;
            this.playlistEntryId = episode.playlistEntryId;
            this.released = episode.released;
        }

        public Builder setId(long id) {
            this.id = id;
            return this;
        }

        public Builder setPodcastId(long podcastId) {
            this.podcastId = podcastId;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setContentUrl(String contentUrl) {
            this.contentUrl = contentUrl;
            return this;
        }

        public Builder setMimeType(String mimeType) {
            this.mimeType = mimeType;
            return this;
        }

        public Builder setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
            return this;
        }

        public Builder setFileSize(Long fileSize) {
            this.fileSize = fileSize;
            return this;
        }

        public Builder setDownloadState(@DatabaseContract.Episode.DownloadState int downloadState) {
            this.downloadState = downloadState;
            return this;
        }

        public Builder setDownloadId(Long downloadId) {
            this.downloadId = downloadId;
            return this;
        }

        public Builder setDuration(Long duration) {
            this.duration = duration;
            return this;
        }

        public Builder setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
            return this;
        }

        public Builder setWatched(boolean watched) {
            this.watched = watched;
            return this;
        }

        public Builder setPlaylistEntryId(Long playlistEntryId) {
            this.playlistEntryId = playlistEntryId;
            return this;
        }

        public Builder setReleased(Date released) {
            this.released = released;
            return this;
        }

        public Episode build() {
            return new Episode(id, podcastId, title, description, contentUrl, mimeType, fileUrl, fileSize, downloadState, downloadId, duration, thumbnail, watched, playlistEntryId, released);
        }
    }
}
