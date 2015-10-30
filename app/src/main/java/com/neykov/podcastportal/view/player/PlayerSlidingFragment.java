package com.neykov.podcastportal.view.player;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.neykov.podcastportal.R;
import com.neykov.podcastportal.view.base.fragment.BaseViewFragment;
import com.neykov.podcastportal.view.player.presenter.PlayerSlidingViewPresenter;
import com.neykov.podcastportal.view.player.view.PlayerSlidingView;

public class PlayerSlidingFragment extends BaseViewFragment<PlayerSlidingViewPresenter> implements PlayerSlidingView {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_player_sliding_panel, container, false);
        return rootView;
    }

    @NonNull
    @Override
    protected PlayerSlidingViewPresenter onCreatePresenter() {
        return getDependencyResolver().getPlayerComponent()
                .createPlayerSlidingViewPresenter();
    }
}
