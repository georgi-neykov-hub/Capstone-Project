package com.neykov.podcastportal.view.base;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

import com.neykov.podcastportal.R;

public abstract class NavigationDrawerActivity extends BaseActivity implements DrawerLayoutProvider, NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mNavigationDrawer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_activity);
        mNavigationDrawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        NavigationView navView = (NavigationView) findViewById(R.id.navigationView);
        onConfigureNavigationView(navView);
        if(savedInstanceState == null){
            Fragment initialFragment = onCreateInitialScreen();
            getFragmentStack().push(initialFragment);
        }
    }

    @NonNull
    @Override
    public DrawerLayout getDrawerLayout() {
        return mNavigationDrawer;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        mNavigationDrawer.closeDrawer(GravityCompat.START);
        switch (menuItem.getItemId()) {
            case R.id.navigation_explore:
                break;
            case R.id.navigation_my_podcasts:
                break;
            case R.id.navigation_about:
            default:
                return false;
        }

        return true;
    }

    @CallSuper
    protected void onConfigureNavigationView(NavigationView view) {
        view.setNavigationItemSelectedListener(this);
    }

    protected abstract Fragment onCreateInitialScreen();
}
