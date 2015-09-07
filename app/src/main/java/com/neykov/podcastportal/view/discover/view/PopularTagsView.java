package com.neykov.podcastportal.view.discover.view;

import com.neykov.podcastportal.model.entity.Tag;
import com.neykov.podcastportal.view.base.LoadingView;

import java.util.List;

/**
 * Created by Georgi on 6.9.2015 г..
 */
public interface PopularTagsView extends LoadingView {
    void onTagsLoaded(List<Tag> tags);
    void onTagsLoadFailed();
}
