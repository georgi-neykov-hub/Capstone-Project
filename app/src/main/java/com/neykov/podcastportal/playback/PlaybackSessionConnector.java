package com.neykov.podcastportal.playback;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.neykov.podcastportal.model.utils.Global;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class PlaybackSessionConnector implements PlaybackSessionProvider{

    private Context mContext;
    private PlaybackService.PlaybackSession mPlaybackSession;
    private boolean mBoundToService;

    private List<PlaybackSessionConnectionListener> mListeners;

    @Inject
    public PlaybackSessionConnector(@Global Context context) {
        this.mContext = context;
        mListeners = new ArrayList<>();
    }

    public void connectToSession() {
        bindToService();
    }

    public void disconnectFromSession() {
        unbindFromService();
    }

    private void notifySessionDisconnected(PlaybackService.PlaybackSession session){
        for (PlaybackSessionConnectionListener l : mListeners){
            l.onConnected(session);
        }
    }

    private void notifySessionDisconnected(){
        for (PlaybackSessionConnectionListener l : mListeners){
            l.onDisconnected();
        }
    }

    private void bindToService() {
        if (!mBoundToService) {
            Intent serviceIntent = new Intent(mContext, PlaybackService.class);
            mBoundToService = mContext.bindService(serviceIntent, mPlaybackServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private void unbindFromService() {
        notifyAndReleaseResources();
        mContext.unbindService(mPlaybackServiceConnection);
        mBoundToService = false;
    }

    private void notifyAndReleaseResources() {
        if (mPlaybackSession != null) {
            notifySessionDisconnected();
            mPlaybackSession.setVideoPlaybackSurface(null);
            mPlaybackSession.setOnVideoSizeChangedListener(null);
            mPlaybackSession = null;
        }
    }

    private final ServiceConnection mPlaybackServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mPlaybackSession = (PlaybackService.PlaybackSession) service;
            notifySessionDisconnected(mPlaybackSession);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            notifyAndReleaseResources();
        }
    };

    @Override
    public void addMediaSessionConnectionListener(PlaybackSessionConnectionListener listener) {
        if(listener == null){
            throw new IllegalArgumentException("Null listener provided.");
        }
        mListeners.add(listener);
    }

    @Override
    public void removeMediaSessionConnectionListener(PlaybackSessionConnectionListener listener) {
        if(listener == null){
            throw new IllegalArgumentException("Null listener provided.");
        }
        mListeners.remove(listener);
    }
}


