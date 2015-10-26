package com.neykov.podcastportal.view.player;

import android.support.v4.app.Fragment;

import com.neykov.podcastportal.view.base.NavigationDrawerActivity;
import com.neykov.podcastportal.view.player.view.PlayerFragment;

public class PlayerActivity extends NavigationDrawerActivity {
    @Override
    protected Fragment onCreateInitialScreen() {
        return PlayerFragment.newInstance();
    }
}

