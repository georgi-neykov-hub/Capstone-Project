package com.neykov.podcastportal.view.player.view;

import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.neykov.podcastportal.R;
import com.neykov.podcastportal.model.playback.PlaybackSession;
import com.neykov.podcastportal.view.base.fragment.ToolbarViewFragment;
import com.neykov.podcastportal.view.player.presenter.PlayerPresenter;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class PlayerFragment extends ToolbarViewFragment<PlayerPresenter> implements PlayerView {

    public static PlayerFragment newInstance() {
        return new PlayerFragment();
    }

    private View mSliderContentContainerView;
    private SlidingUpPanelLayout mSlidingLayoutView;

    private MediaControllerCompat mMediaController;

    @NonNull
    @Override
    protected PlayerPresenter onCreatePresenter() {
        return getDependencyResolver().getPlayerComponent().createPlayerPresenter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_player, container, false);
        mSliderContentContainerView = rootView.findViewById(R.id.slidingContentContainer);
        mSlidingLayoutView = (SlidingUpPanelLayout) rootView.findViewById(R.id.slidingLayout);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        mSlidingLayoutView = null;
        mSliderContentContainerView = null;
        super.onDestroyView();
    }



    @Nullable
    @Override
    protected Toolbar onSetToolbar(View view) {
        return (Toolbar) view.findViewById(R.id.toolbar);
    }

    @Override
    public void onConnected(PlaybackSession playbackSession) {
        try {
            mMediaController = new MediaControllerCompat(getContext(), playbackSession.getMediaSessionToken());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        mMediaController.registerCallback(mMediaControllerListener);
        handlePlaybackStateChange(mMediaController.getPlaybackState().getState());
    }

    @Override
    public void onDisconnected() {
        mMediaController.unregisterCallback(mMediaControllerListener);
        mMediaController = null;
    }

    private void showSliderFragment() {
        Fragment playerFragment = getChildFragmentManager().findFragmentById(mSliderContentContainerView.getId());
        if (playerFragment == null) {
            // Add the sliding player fragment.
            getChildFragmentManager().beginTransaction()
                    .replace(mSliderContentContainerView.getId(), new PlayerSlidingFragment())
                    .commit();
            mSlidingLayoutView.setEnabled(true);
            mSlidingLayoutView.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
    }

    private void hideSlidingFragment() {
        Fragment playerFragment = getChildFragmentManager().findFragmentById(mSliderContentContainerView.getId());
        if (playerFragment != null) {
            // Add the sliding player fragment.
            getChildFragmentManager().beginTransaction()
                    .remove(playerFragment)
                    .commit();
            mSlidingLayoutView.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        }
    }

    private void handlePlaybackStateChange(@PlaybackStateCompat.State int playbackStatus){
        if(playbackStatus == PlaybackStateCompat.STATE_NONE){
            if(mSlidingLayoutView.getPanelState()== SlidingUpPanelLayout.PanelState.COLLAPSED) {
                hideSlidingFragment();
            }
        } else {
            showSliderFragment();
        }
    }

    private final MediaControllerCompat.Callback mMediaControllerListener = new MediaControllerCompat.Callback() {
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            handlePlaybackStateChange(state.getState());
        }

        @Override
        public void onSessionDestroyed() {
            hideSlidingFragment();
        }
    };
}
