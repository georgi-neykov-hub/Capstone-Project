package com.neykov.podcastportal.view.discover.view;

import com.neykov.podcastportal.view.base.LoadingView;

import java.util.List;

/**
 * Created by Georgi on 6.9.2015 г..
 */
public interface PopularTagsView extends LoadingView {
    void onTagsLoaded(List<String> tags);
    void onTagsLoadFailed();
}
