package com.neykov.podcastportal.view.explore;

import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;

import com.neykov.podcastportal.R;
import com.neykov.podcastportal.view.base.NavigationDrawerActivity;
import com.neykov.podcastportal.view.explore.view.ExploreFragment;

public class ExploreActivity extends NavigationDrawerActivity {

    @Override
    protected Fragment onCreateInitialScreen() {
        return ExploreFragment.newInstance();
    }

    @Override
    protected void onConfigureNavigationView(NavigationView view) {
        super.onConfigureNavigationView(view);
        view.setCheckedItem(R.id.navigation_explore);
    }
}