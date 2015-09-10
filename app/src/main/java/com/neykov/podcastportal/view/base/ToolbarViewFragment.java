package com.neykov.podcastportal.view.base;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.neykov.podcastportal.R;

import nucleus.presenter.Presenter;

public abstract class ToolbarViewFragment<PresenterType extends Presenter>
        extends BaseViewFragment<PresenterType>{

    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar mToolbar;
    private DrawerLayoutProvider mDrawerProvider;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Object host = getHost();
        if (host instanceof DrawerLayoutProvider) {
            mDrawerProvider = (DrawerLayoutProvider) host;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mDrawerProvider = null;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mDrawerProvider != null) {
            mToolbar = callOnSetToolbar();
        }

        if (mToolbar != null) {
            createDrawerToggle(mToolbar);
            callOnConfigureToolbar();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mToolbar = null;
        mDrawerToggle = null;
    }

    @Override
    public void onResume() {
        if (mDrawerProvider != null && mDrawerToggle != null) {
            mDrawerProvider.getDrawerLayout().setDrawerListener(mDrawerToggle);
            mDrawerToggle.syncState();
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mDrawerProvider != null) {
            mDrawerProvider.getDrawerLayout().setDrawerListener(null);
        }
    }

    @Nullable
    protected Toolbar onSetToolbar(View view) {
        return null;
    }

    protected void onConfigureToolbar(Toolbar toolbar) {

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mDrawerToggle != null) {
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle != null) {
            return mDrawerToggle.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    private Toolbar callOnSetToolbar() {
        return onSetToolbar(getView());
    }

    private void callOnConfigureToolbar() {
        onConfigureToolbar(mToolbar);
    }

    private void createDrawerToggle(Toolbar toolbar) {
        if (mDrawerProvider != null) {
            //Set a drawer Toggle t
            mDrawerToggle = new ActionBarDrawerToggle(
                    getActivity(),
                    mDrawerProvider.getDrawerLayout(),
                    toolbar,
                    R.string.navigation_drawer_open,
                    R.string.navigation_drawer_close);
            mDrawerToggle.syncState();
        }
    }
}
