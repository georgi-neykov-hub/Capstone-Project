package com.neykov.podcastportal.view.subscriptions;

import com.neykov.podcastportal.view.subscriptions.view.PodcastDetailFragment;

import javax.inject.Singleton;

import dagger.Subcomponent;

@Subcomponent
@Singleton
public interface SubscriptionsComponent {
    PodcastDetailPresenter createPodcastDetailPresenter();
    void inject(PodcastDetailFragment fragment);
}
