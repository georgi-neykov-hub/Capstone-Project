package com.neykov.podcastportal.view.player.presenter;

import android.content.Context;
import android.os.Bundle;

import com.neykov.podcastportal.playback.PlaybackSession;
import com.neykov.podcastportal.playback.PlaybackSessionConnectionListener;
import com.neykov.podcastportal.playback.PlaybackSessionConnector;
import com.neykov.podcastportal.playback.PlaybackService;
import com.neykov.podcastportal.model.utils.Global;
import com.neykov.podcastportal.view.base.BasePresenter;
import com.neykov.podcastportal.view.player.view.PlayerSlidingView;

import javax.inject.Inject;

public class PlayerSlidingViewPresenter extends BasePresenter<PlayerSlidingView> {

    private Context mContext;
    private PlaybackSessionConnector mConnector;

    @Inject
    public PlayerSlidingViewPresenter(@Global Context context) {
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        mConnector = new PlaybackSessionConnector(mContext);
        mConnector.connectToSession();
    }

    @Override
    protected void onDestroy() {
        mConnector.disconnectFromSession();
        mConnector.destroy();
        mConnector = null;
        super.onDestroy();
    }

    @Override
    protected void onTakeView(PlayerSlidingView playerSlidingView) {
        super.onTakeView(playerSlidingView);
        mConnector.addMediaSessionConnectionListener(playerSlidingView);
        PlaybackSession session = mConnector.getPlaybackSession();
        if(session != null){
            playerSlidingView.onConnected(session);
        }
    }

    @Override
    protected void onDropView() {
        PlayerSlidingView playerSlidingView = getView();
        if(playerSlidingView == null){
            throw new AssertionError("onDropView() called, but there is no view to drop.");
        }
        mConnector.removeMediaSessionConnectionListener(playerSlidingView);
        PlaybackSession session = mConnector.getPlaybackSession();
        if(session != null){
            playerSlidingView.onDisconnected();
        }
        super.onDropView();
    }
}
