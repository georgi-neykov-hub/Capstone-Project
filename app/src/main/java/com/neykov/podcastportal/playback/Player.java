package com.neykov.podcastportal.playback;

import android.support.v4.media.session.PlaybackStateCompat;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.neykov.podcastportal.model.entity.Episode;
import com.neykov.podcastportal.model.entity.PlaylistEntry;

/**
 * Created by Georgi on 31.10.2015 г..
 */
public interface Player {
    /**
     * Start/setup the playback.
     * Resources/listeners would be allocated by implementations.
     */
    void start();

    /**
     * Stop the playback. All resources can be de-allocated by implementations here.
     * @param notifyListeners if true and a callback has been set by setCallback,
     *                        callback.onPlaybackStatusChanged will be called after changing
     *                        the state.
     */
    void stop(boolean notifyListeners);

    /**
     * Get the current {@link android.media.session.PlaybackState#getState()}
     */
    int getPlaybackState();

    /**
     * @return boolean indicating whether the player is playing or is supposed to be
     * playing when we gain audio focus.
     */
    boolean isPlaying();

    /**
     * @return pos if currently playing an item
     */
    int getCurrentStreamPosition();

    /**
     * Set the current position. Typically used when switching players that are in
     * paused state.
     *
     * @param pos position in the stream
     */
    void setCurrentStreamPosition(int pos);

    /**
     * @param item to play
     */
    void play(Episode item);

    /**
     * Pause the current playing item
     */
    void pause();

    /**
     * Seek to the given position
     */
    void seekTo(int position);

    /**
     * Set the current mediaId. This is only used when switching from one
     * playback to another.
     *
     * @param {@link Episode} to be set as the current.
     */
    void setCurrentEpisode(Episode episode);

    /**
     *
     * @return the current media Id being processed in any state or null.
     */
    Episode getCurrentEpisode();

    void setVideoPlaybackSurface(Surface surface);

    interface Callback extends OnVideoSizeChangedListener{
        /**
         * On current music completed.
         */
        void onCompletion();
        /**
         * on Playback status changed
         * Implementations can use this callback to update
         * playback state on the media sessions.
         */
        void onPlaybackStatusChanged(@PlaybackStateCompat.State int state);

        /**
         * @param error to be added to the PlaybackState
         */
        void onError(String error);

        /**
         * @param mediaId being currently played
         */
        void onMetadataChanged(String mediaId);
    }

    /**
     * @param callback to be called
     */
    void setCallback(Callback callback);
}