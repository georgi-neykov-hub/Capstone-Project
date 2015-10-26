package com.neykov.podcastportal.view.player.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.neykov.podcastportal.R;
import com.neykov.podcastportal.view.base.fragment.ToolbarViewFragment;
import com.neykov.podcastportal.view.player.presenter.PlayerPresenter;

public class PlayerFragment extends ToolbarViewFragment<PlayerPresenter> {

    public static PlayerFragment newInstance() {
        return new PlayerFragment();
    }
    @NonNull
    @Override
    protected PlayerPresenter onCreatePresenter() {
        return getDependencyResolver().getPlayerComponent().createPlayerPresenter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_player, container, false);
    }

    @Nullable
    @Override
    protected Toolbar onSetToolbar(View view) {
        return (Toolbar) view.findViewById(R.id.toolbar);
    }
}
