package com.neykov.podcastportal.model.playback;

import android.support.v4.media.session.MediaSessionCompat;
import android.view.Surface;

/**
 * Created by Georgi on 2.11.2015 г..
 */
public interface PlaybackSession {

    String EXTRA_EPISODE_DATA = "com.neykov.podcastportal.model.playback.PlaybackSession.EPISODE_DATA";
    String EXTRA_PODCAST_DATA = "com.neykov.podcastportal.model.playback.PlaybackSession.PODCAST_DATA";
    String EXTRA_SHARE_INTENT = "com.neykov.podcastportal.model.SHARE_INTENT";

    MediaSessionCompat.Token getMediaSessionToken();

    void setVideoPlaybackSurface(Surface surface);

    void setOnVideoSizeChangedListener(OnVideoSizeChangedListener listener);
}
