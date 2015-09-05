package com.neykov.podcastportal.view.discover;


import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.neykov.podcastportal.R;
import com.neykov.podcastportal.view.base.ToolbarFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExploreFrament extends ToolbarFragment<DiscoverPresenter> {

    public static final String TAG = ExploreFrament.class.getSimpleName();

    public static ExploreFrament newInstance() {
        return new ExploreFrament();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_explore, container, false);
    }

    @NonNull
    @Override
    protected Toolbar onSetToolbar(View view) {
        return (Toolbar) view.findViewById(R.id.toolbar);
    }

    @Override
    protected void onConfigureToolbar(Toolbar toolbar) {

    }

    @NonNull
    @Override
    protected DiscoverPresenter onCreatePresenter() {
        return getDependencyResolver()
                .getDiscoverComponent()
                .createDiscoverPresenter();
    }
}
