package com.neykov.podcastportal.model.playback;

public interface PlaybackSessionConnectionListener {
    void onConnected(PlaybackSession playbackSession);
    void onDisconnected();
}
