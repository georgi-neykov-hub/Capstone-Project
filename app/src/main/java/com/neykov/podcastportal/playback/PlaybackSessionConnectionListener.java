package com.neykov.podcastportal.playback;

public interface PlaybackSessionConnectionListener {
    void onConnected(PlaybackSession playbackSession);
    void onDisconnected();
}
