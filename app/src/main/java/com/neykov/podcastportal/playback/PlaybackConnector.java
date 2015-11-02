package com.neykov.podcastportal.playback;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.neykov.podcastportal.model.utils.Global;

import javax.inject.Inject;

public class PlaybackConnector {

    private Context mContext;
    private PlaybackConnectionListener mListener;
    private PlaybackService.PlaybackInterface mPlaybackInterface;
    private boolean mBoundToService;

    @Inject
    public PlaybackConnector(@Global Context context, PlaybackConnectionListener listener) {
        if(listener == null){
            throw new IllegalArgumentException("Null listener provided.");
        }
        this.mContext = context;
        mListener = listener;
    }

    public void connect() {
        bindToService();
    }

    public void disconnect() {
        unbindFromService();
    }

    public void setListener(PlaybackConnectionListener listener) {
        this.mListener = listener;
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
        if (mPlaybackInterface != null) {
            mListener.onDisconnected();
            mPlaybackInterface.setVideoPlaybackSurface(null);
            mPlaybackInterface.setOnVideoSizeChangedListener(null);
            mPlaybackInterface = null;
        }
    }

    private final ServiceConnection mPlaybackServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mPlaybackInterface = (PlaybackService.PlaybackInterface) service;
            mListener.onConnected(mPlaybackInterface);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            notifyAndReleaseResources();
        }
    };
}


