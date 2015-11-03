package com.neykov.podcastportal.playback;

public interface PlaybackSessionConnectionListener {
    void onConnected(PlaybackService.PlaybackSession playbackSession);
    void onDisconnected();
}
