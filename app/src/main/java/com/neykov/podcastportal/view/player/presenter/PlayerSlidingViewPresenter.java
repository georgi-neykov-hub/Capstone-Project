package com.neykov.podcastportal.view.player.presenter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.media.session.MediaControllerCompat;

import com.neykov.podcastportal.playback.PlaybackService;
import com.neykov.podcastportal.model.utils.Global;
import com.neykov.podcastportal.view.base.BasePresenter;
import com.neykov.podcastportal.view.player.view.PlayerSlidingView;

import javax.inject.Inject;

public class PlayerSlidingViewPresenter extends BasePresenter<PlayerSlidingView> {

    private Context mContext;
    private PlaybackService.PlaybackInterface mPlaybackInterface;
    private MediaControllerCompat mMediaController;

    @Inject
    public PlayerSlidingViewPresenter(@Global Context context) {
        this.mContext = context;
    }

    @Override
    protected void onTakeView(PlayerSlidingView playerSlidingView) {
        super.onTakeView(playerSlidingView);
        bindToService();
        if(mPlaybackInterface != null){
            playerSlidingView.onConnected(mPlaybackInterface, mMediaController);
        }
    }

    @Override
    protected void onDropView() {
        unbindFromService();
        super.onDropView();
    }

    private void bindToService(){
        Intent serviceIntent = new Intent(mContext, PlaybackService.class);
        mContext.bindService(serviceIntent, mPlaybackServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void unbindFromService(){
        if(mPlaybackInterface != null){
            if(getView() != null){
                getView().onDisconnected();
            }
            mPlaybackInterface.setVideoPlaybackSurface(null);
            mPlaybackInterface.setOnVideoSizeChangedListener(null);
            mMediaController = null;
            mPlaybackInterface = null;
        }
        mContext.unbindService(mPlaybackServiceConnection);
    }

    private final ServiceConnection mPlaybackServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mPlaybackInterface = (PlaybackService.PlaybackInterface) service;
            try {
                mMediaController = new MediaControllerCompat(mContext, mPlaybackInterface.getMediaSessionToken());
            } catch (RemoteException e) {
                throw new RuntimeException("Error while creating MediaController.",e);
            }
            if(getView() != null){
                getView().onConnected(mPlaybackInterface, mMediaController);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if(mPlaybackInterface != null){
                if(getView() != null){
                    getView().onDisconnected();
                }
                mPlaybackInterface.setVideoPlaybackSurface(null);
                mPlaybackInterface.setOnVideoSizeChangedListener(null);
                mMediaController = null;
                mPlaybackInterface = null;
            }
        }
    };
}
