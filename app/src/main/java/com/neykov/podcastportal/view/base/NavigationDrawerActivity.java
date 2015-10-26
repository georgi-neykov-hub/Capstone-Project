package com.neykov.podcastportal.view.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import com.neykov.podcastportal.R;
import com.neykov.podcastportal.view.explore.ExploreActivity;
import com.neykov.podcastportal.view.player.PlayerActivity;
import com.neykov.podcastportal.view.subscriptions.MyPodcastsActivity;

public abstract class NavigationDrawerActivity extends BaseActivity implements DrawerLayoutProvider, NavigationView.OnNavigationItemSelectedListener {

    private static final java.lang.String KEY_KEEP_DRAWER_CLOSED = "NavigationDrawerActivity.KEY_KEEP_DRAWER_CLOSED";
    private DrawerLayout mNavigationDrawer;
    private boolean mKeepDrawerClosed;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_activity);
        mNavigationDrawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        NavigationView navView = (NavigationView) findViewById(R.id.navigationView);
        onConfigureNavigationView(navView);
        getFragmentStack().setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);

        if(savedInstanceState == null){
            Fragment initialFragment = onCreateInitialScreen();
            getFragmentStack().push(initialFragment);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_KEEP_DRAWER_CLOSED, mKeepDrawerClosed);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mKeepDrawerClosed = savedInstanceState.getBoolean(KEY_KEEP_DRAWER_CLOSED);
        if(mKeepDrawerClosed){
            mNavigationDrawer.closeDrawer(Gravity.LEFT);
            mKeepDrawerClosed = false;
        }
    }

    @NonNull
    @Override
    public DrawerLayout getDrawerLayout() {
        return mNavigationDrawer;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        mNavigationDrawer.closeDrawer(Gravity.LEFT);
        mKeepDrawerClosed = true;
        switch (menuItem.getItemId()) {
            case R.id.navigation_explore:
                openSection(ExploreActivity.class);
                break;
            case R.id.navigation_my_podcasts:
                openSection(MyPodcastsActivity.class);
                break;
            case R.id.navigation_player:
                openSection(PlayerActivity.class);
            case R.id.navigation_about:
        }
        return false;
    }

    @CallSuper
    protected void onConfigureNavigationView(NavigationView view) {
        view.setNavigationItemSelectedListener(this);
    }

    protected abstract Fragment onCreateInitialScreen();

    private <T extends Class<? extends Activity>> void openSection( T activityClass){
        //noinspection EqualsBetweenInconvertibleTypes
        if(!this.getClass().equals(activityClass)) {
            mNavigationDrawer.getHandler().post(() -> {
                Intent activityIntent = new Intent(NavigationDrawerActivity.this, activityClass)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(activityIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            });
        }
    }
}
