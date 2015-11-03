package com.neykov.podcastportal.view.player.presenter;

import android.content.Context;
import android.os.Bundle;

import com.neykov.podcastportal.playback.PlaybackSessionConnectionListener;
import com.neykov.podcastportal.playback.PlaybackSessionConnector;
import com.neykov.podcastportal.playback.PlaybackService;
import com.neykov.podcastportal.model.utils.Global;
import com.neykov.podcastportal.view.base.BasePresenter;
import com.neykov.podcastportal.view.player.view.PlayerSlidingView;

import javax.inject.Inject;

public class PlayerSlidingViewPresenter extends BasePresenter<PlayerSlidingView> {

    private PlaybackService.PlaybackSession mPlaybackSession;
    private PlaybackSessionConnector mConnector;

    @Inject
    public PlayerSlidingViewPresenter(@Global Context context) {
        this.mConnector = new PlaybackSessionConnector(context, mConnectionListener);
    }

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        mConnector.connectToSession();
    }

    @Override
    protected void onDestroy() {
        mConnector.disconnectFromSession();
        super.onDestroy();
    }

    @Override
    protected void onTakeView(PlayerSlidingView playerSlidingView) {
        super.onTakeView(playerSlidingView);
        if(mPlaybackSession != null){
            playerSlidingView.onConnected(mPlaybackSession);
        }
    }

    @Override
    protected void onDropView() {
        if(mPlaybackSession != null){
            //noinspection ConstantConditions
            getView().onDisconnected();
        }
        super.onDropView();
    }

    private final PlaybackSessionConnectionListener mConnectionListener = new PlaybackSessionConnectionListener() {
        @Override
        public void onConnected(PlaybackService.PlaybackSession playbackSession) {
            mPlaybackSession = playbackSession;
            if(getView() != null){
                getView().onConnected(playbackSession);
            }
        }

        @Override
        public void onDisconnected() {
            if(getView() != null){
                getView().onDisconnected();
                mPlaybackSession = null;
            }
        }
    };

}
