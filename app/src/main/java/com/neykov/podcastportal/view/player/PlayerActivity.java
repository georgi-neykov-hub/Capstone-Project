package com.neykov.podcastportal.view.player;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.neykov.podcastportal.playback.PlaybackSession;
import com.neykov.podcastportal.playback.PlaybackSessionConnectionListener;
import com.neykov.podcastportal.playback.PlaybackSessionConnector;
import com.neykov.podcastportal.playback.PlaybackSessionProvider;
import com.neykov.podcastportal.view.base.NavigationDrawerActivity;
import com.neykov.podcastportal.view.player.view.PlayerFragment;

import rx.Observable;

public class PlayerActivity extends NavigationDrawerActivity implements PlaybackSessionProvider{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Observable<PlaybackSession> getStream() {
        return null;
    }

    @Nullable
    @Override
    public PlaybackSession getPlaybackSession() {
        return null;
    }

    @Override
    public void addMediaSessionConnectionListener(PlaybackSessionConnectionListener listener) {

    }

    @Override
    public void removeMediaSessionConnectionListener(PlaybackSessionConnectionListener listener) {

    }

    @Override
    protected Fragment onCreateInitialScreen() {
        return PlayerFragment.newInstance();
    }
}

