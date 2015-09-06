package com.neykov.podcastportal.view.discover.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.neykov.podcastportal.R;
import com.neykov.podcastportal.view.base.BaseFragment;

/**
 * Created by Georgi on 7.9.2015 г..
 */
public class PopularPodcastsFragment extends BaseFragment {

    public static PopularPodcastsFragment newInstance() {
        PopularPodcastsFragment fragment = new PopularPodcastsFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_popular_podcasts, container, false);
    }
}
