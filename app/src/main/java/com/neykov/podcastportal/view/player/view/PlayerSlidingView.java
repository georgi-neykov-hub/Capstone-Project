package com.neykov.podcastportal.view.player.view;

import android.support.v4.media.session.MediaControllerCompat;

import com.neykov.podcastportal.playback.PlaybackService;

/**
 * Created by Georgi on 28.10.2015 г..
 */
public interface PlayerSlidingView {

    void onConnected(PlaybackService.PlaybackInterface playbackInterface, MediaControllerCompat controller);
    void onDisconnected();
    void onVideoDimensionsAvailable(int width, int height);
}
