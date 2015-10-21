package com.neykov.podcastportal.view.subscriptions;

import com.neykov.podcastportal.view.subscriptions.presenter.EpisodesListPresenter;
import com.neykov.podcastportal.view.subscriptions.presenter.MyPodcastsPresenter;

import javax.inject.Singleton;

import dagger.Subcomponent;

@Subcomponent
@Singleton
public interface SubscriptionsComponent {
    MyPodcastsPresenter createMyPodcastsPresenter();
    EpisodesListPresenter createEpisodesListPresenter();
}
