package com.neykov.podcastportal.view.subscriptions;

import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;

import com.neykov.podcastportal.R;
import com.neykov.podcastportal.view.base.NavigationDrawerActivity;
import com.neykov.podcastportal.view.subscriptions.view.MyPodcastsFragment;

public class MyPodcastsActivity extends NavigationDrawerActivity {

    @Override
    protected Fragment onCreateInitialScreen() {
        return MyPodcastsFragment.newInstance();
    }

    @Override
    protected void onConfigureNavigationView(NavigationView view) {
        super.onConfigureNavigationView(view);
        view.setCheckedItem(R.id.navigation_my_podcasts);
    }
}