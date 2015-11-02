package com.neykov.podcastportal.playback;

public interface PlaybackConnectionListener {
    void onConnected(PlaybackService.PlaybackInterface playbackInterface);
    void onDisconnected();
}
