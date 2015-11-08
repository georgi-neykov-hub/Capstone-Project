package com.neykov.podcastportal.model.downloads;

/**
 * Created by Georgi on 5.11.2015 г..
 */
public class Download {
    private long id;
    private String localFilePath;
    private int status;
    private long bytesDownloaded;
    private long totalSizeBytes;

    public Download(long id, String localFilePath, int status, long bytesDownloaded, long totalSizeBytes) {
        this.id = id;
        this.localFilePath = localFilePath;
        this.status = status;
        this.bytesDownloaded = bytesDownloaded;
        this.totalSizeBytes = totalSizeBytes;
    }

    public long getId() {
        return id;
    }

    public String getLocalFilePath() {
        return localFilePath;
    }

    public int getStatus() {
        return status;
    }

    public long getBytesDownloaded() {
        return bytesDownloaded;
    }

    public long getTotalSizeBytes() {
        return totalSizeBytes;
    }
}
