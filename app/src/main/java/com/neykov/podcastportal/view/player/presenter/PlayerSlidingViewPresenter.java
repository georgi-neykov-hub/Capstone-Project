package com.neykov.podcastportal.view.player.presenter;

import android.content.Context;
import android.os.Bundle;

import com.neykov.podcastportal.playback.PlaybackConnectionListener;
import com.neykov.podcastportal.playback.PlaybackConnector;
import com.neykov.podcastportal.playback.PlaybackService;
import com.neykov.podcastportal.model.utils.Global;
import com.neykov.podcastportal.view.base.BasePresenter;
import com.neykov.podcastportal.view.player.view.PlayerSlidingView;

import javax.inject.Inject;

public class PlayerSlidingViewPresenter extends BasePresenter<PlayerSlidingView> {

    private PlaybackService.PlaybackInterface mPlaybackInterface;
    private PlaybackConnector mConnector;

    @Inject
    public PlayerSlidingViewPresenter(@Global Context context) {
        this.mConnector = new PlaybackConnector(context, mConnectionListener);
    }

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        mConnector.connect();
    }

    @Override
    protected void onDestroy() {
        mConnector.disconnect();
        super.onDestroy();
    }

    @Override
    protected void onTakeView(PlayerSlidingView playerSlidingView) {
        super.onTakeView(playerSlidingView);
        if(mPlaybackInterface != null){
            playerSlidingView.onConnected(mPlaybackInterface);
        }
    }

    @Override
    protected void onDropView() {
        if(mPlaybackInterface != null){
            //noinspection ConstantConditions
            getView().onDisconnected();
        }
        super.onDropView();
    }

    private final PlaybackConnectionListener mConnectionListener = new PlaybackConnectionListener() {
        @Override
        public void onConnected(PlaybackService.PlaybackInterface playbackInterface) {
            mPlaybackInterface = playbackInterface;
            if(getView() != null){
                getView().onConnected(playbackInterface);
            }
        }

        @Override
        public void onDisconnected() {
            if(getView() != null){
                getView().onDisconnected();
                mPlaybackInterface = null;
            }
        }
    };

}
