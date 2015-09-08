package com.neykov.podcastportal.view.discover;

import com.neykov.podcastportal.view.discover.presenter.PopularPodcastsPresenter;
import com.neykov.podcastportal.view.discover.presenter.PopularTagsPresenter;
import com.neykov.podcastportal.view.discover.view.PopularPodcastsAdapter;

import dagger.Subcomponent;

@Subcomponent
public interface DiscoverComponent {
    DiscoverPresenter createDiscoverPresenter();
    PopularTagsPresenter createTopTagsPresenter();
    PopularPodcastsPresenter createPopularPodcastsPresenter();
}
