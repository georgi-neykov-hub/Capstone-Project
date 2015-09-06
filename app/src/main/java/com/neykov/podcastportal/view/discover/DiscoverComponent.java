package com.neykov.podcastportal.view.discover;

import com.neykov.podcastportal.view.discover.presenter.PopularTagsPresenter;

import dagger.Subcomponent;

@Subcomponent
public interface DiscoverComponent {
    DiscoverPresenter createDiscoverPresenter();
    PopularTagsPresenter createTopTagsPresenter();
}
