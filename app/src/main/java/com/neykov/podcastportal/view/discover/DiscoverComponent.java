package com.neykov.podcastportal.view.discover;

import com.neykov.podcastportal.view.discover.presenter.ExplorePresenter;
import com.neykov.podcastportal.view.discover.presenter.PodcastSearchPresenter;
import com.neykov.podcastportal.view.discover.presenter.PodcastsForTagPresenter;
import com.neykov.podcastportal.view.discover.presenter.PopularPodcastsPresenter;
import com.neykov.podcastportal.view.discover.presenter.PopularTagsPresenter;

import dagger.Subcomponent;

@Subcomponent
public interface DiscoverComponent {
    ExplorePresenter createDiscoverPresenter();
    PopularTagsPresenter createTopTagsPresenter();
    PopularPodcastsPresenter createPopularPodcastsPresenter();
    PodcastsForTagPresenter createPodcastsForTagPresenter();
    PodcastSearchPresenter createPodcastSearchPresenter();
}
