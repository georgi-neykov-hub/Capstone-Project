package com.neykov.podcastportal.playback;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.neykov.podcastportal.model.utils.Global;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subjects.Subject;

public class PlaybackSessionConnector implements PlaybackSessionProvider {

    private Context mContext;
    private PlaybackSession mPlaybackSession;
    private boolean mBoundToService;
    private Subject<PlaybackSession, PlaybackSession> mPlaybackSessionSubject;

    private List<PlaybackSessionConnectionListener> mListeners;

    @Inject
    public PlaybackSessionConnector(@Global Context context) {
        this.mContext = context;
        mListeners = new ArrayList<>();
        mPlaybackSessionSubject = BehaviorSubject.create();
    }

    public void connectToSession() {
        bindToService();
    }

    public void disconnectFromSession() {
        unbindFromService();
    }

    public void destroy(){
        if(mBoundToService){
            disconnectFromSession();
        }
        mPlaybackSessionSubject.onCompleted();
    }

    @NonNull
    @Override
    public Observable<PlaybackSession> getStream() {
        return mPlaybackSessionSubject.asObservable();
    }

    @Nullable
    @Override
    public PlaybackSession getPlaybackSession() {
        return mPlaybackSession;
    }

    @Override
    public void addMediaSessionConnectionListener(PlaybackSessionConnectionListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Null listener provided.");
        }
        mListeners.add(listener);
    }

    @Override
    public void removeMediaSessionConnectionListener(PlaybackSessionConnectionListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Null listener provided.");
        }
        mListeners.remove(listener);
    }

    private void notifySessionConnected(PlaybackSession session) {
        mPlaybackSessionSubject.onNext(session);
        for (PlaybackSessionConnectionListener l : mListeners) {
            l.onConnected(session);
        }
    }

    private void notifySessionDisconnected() {
        mPlaybackSessionSubject.onNext(null);
        for (PlaybackSessionConnectionListener l : mListeners) {
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
        if (mBoundToService) {
            notifyAndReleaseResources();
            mContext.unbindService(mPlaybackServiceConnection);
            mBoundToService = false;
        }
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
            mPlaybackSession = (PlaybackSession) service;
            notifySessionConnected(mPlaybackSession);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            notifyAndReleaseResources();
        }
    };
}


