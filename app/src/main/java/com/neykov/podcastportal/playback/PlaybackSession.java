package com.neykov.podcastportal.playback;

import android.support.v4.media.session.MediaSessionCompat;
import android.view.Surface;

/**
 * Created by Georgi on 2.11.2015 г..
 */
public interface PlaybackSession {
    MediaSessionCompat.Token getMediaSessionToken();

    void setVideoPlaybackSurface(Surface surface);

    void setOnVideoSizeChangedListener(OnVideoSizeChangedListener listener);
}
