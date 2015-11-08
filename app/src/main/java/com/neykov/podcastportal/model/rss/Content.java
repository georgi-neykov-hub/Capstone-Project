package com.neykov.podcastportal.model.rss;

public class Content {
    private String contentUrl;
    private String mimeType;
    private long contentLength;

    public Content(String contentUrl, String mimeType, long contentLength) {
        this.contentUrl = contentUrl;
        this.mimeType = mimeType;
        this.contentLength = contentLength;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public String getMimeType() {
        return mimeType;
    }

    public long getContentLength() {
        return contentLength;
    }
}
