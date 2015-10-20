package com.neykov.podcastportal.view;

import android.support.v4.app.Fragment;

import com.neykov.podcastportal.view.base.NavigationDrawerActivity;
import com.neykov.podcastportal.view.subscriptions.view.MyPodcastsFragment;

public class MyPodcastsActivity extends NavigationDrawerActivity {

    @Override
    protected Fragment onCreateInitialScreen() {
        return MyPodcastsFragment.newInstance();
    }
}