package com.neykov.podcastportal.playback;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import rx.Observable;

public interface PlaybackSessionProvider {

    @NonNull
    Observable<PlaybackSession> getStream();

    @Nullable
    PlaybackSession getPlaybackSession();

    void addMediaSessionConnectionListener(PlaybackSessionConnectionListener listener);

    void removeMediaSessionConnectionListener(PlaybackSessionConnectionListener listener);

}
