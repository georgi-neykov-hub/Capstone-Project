package com.neykov.podcastportal.playback;

/**
 * Created by Georgi on 2.11.2015 г..
 */
public interface PlaybackSessionProvider {

    void addMediaSessionConnectionListener(PlaybackSessionConnectionListener listener);
    void removeMediaSessionConnectionListener(PlaybackSessionConnectionListener listener);

}
