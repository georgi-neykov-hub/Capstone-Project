package com.neykov.podcastportal.view.player.presenter;

import android.content.Context;
import android.os.Bundle;

import com.neykov.podcastportal.model.utils.Global;
import com.neykov.podcastportal.playback.PlaybackSession;
import com.neykov.podcastportal.playback.PlaybackSessionConnector;
import com.neykov.podcastportal.view.base.BasePresenter;
import com.neykov.podcastportal.view.player.view.PlayerSlidingView;
import com.neykov.podcastportal.view.player.view.PlayerView;

import javax.inject.Inject;

public class PlayerPresenter extends BasePresenter<PlayerView> {

    private PlaybackSessionConnector mConnector;
    private Context mContext;

    @Inject
    public PlayerPresenter(@Global Context context) {
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        mConnector = new PlaybackSessionConnector(mContext);
        mConnector.connectToSession();
    }

    @Override
    protected void onTakeView(PlayerView playerView) {
        super.onTakeView(playerView);
        mConnector.addMediaSessionConnectionListener(playerView);
        PlaybackSession session = mConnector.getPlaybackSession();
        if(session != null){
            playerView.onConnected(session);
        }
    }

    @Override
    protected void onDropView() {
        PlayerView playerView = getView();
        if(playerView == null){
            throw new AssertionError("onDropView() called, but there is no view to drop.");
        }
        mConnector.removeMediaSessionConnectionListener(playerView);
        PlaybackSession session = mConnector.getPlaybackSession();
        if(session != null){
            playerView.onDisconnected();
        }
        super.onDropView();
    }

    @Override
    protected void onDestroy() {
        mConnector.disconnectFromSession();
        mConnector.destroy();
        mConnector = null;
        super.onDestroy();
    }
}
