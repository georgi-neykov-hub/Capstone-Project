package com.neykov.podcastportal.view.explore;

import com.neykov.podcastportal.view.explore.presenter.ExplorePresenter;
import com.neykov.podcastportal.view.explore.presenter.PodcastSearchPresenter;
import com.neykov.podcastportal.view.explore.presenter.PodcastsForTagPresenter;
import com.neykov.podcastportal.view.explore.presenter.PopularPodcastsPresenter;
import com.neykov.podcastportal.view.explore.presenter.PopularTagsPresenter;

import javax.inject.Singleton;

import dagger.Subcomponent;

@Subcomponent
@Singleton
public interface DiscoverComponent {
    ExplorePresenter createDiscoverPresenter();
    PopularTagsPresenter createTopTagsPresenter();
    PopularPodcastsPresenter createPopularPodcastsPresenter();
    PodcastsForTagPresenter createPodcastsForTagPresenter();
    PodcastSearchPresenter createPodcastSearchPresenter();
}
