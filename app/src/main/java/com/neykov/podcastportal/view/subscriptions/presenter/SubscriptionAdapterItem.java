package com.neykov.podcastportal.view.subscriptions.presenter;

import com.neykov.podcastportal.model.entity.Episode;
import com.neykov.podcastportal.model.entity.Subscription;

import java.util.List;

/**
 * Created by Georgi on 21.10.2015 г..
 */
class SubscriptionAdapterItem {

    private Subscription mSubscription;
    private NestedEpisodeAdapter mNestedEpisodeAdapter;

    public SubscriptionAdapterItem(Subscription subscription, List<Episode> episodes) {
        this.mSubscription = subscription;
        this.mNestedEpisodeAdapter = new NestedEpisodeAdapter(episodes);
    }

    public NestedEpisodeAdapter getAdapter() {
        return mNestedEpisodeAdapter;
    }

    public Subscription getSubscription() {
        return mSubscription;
    }
}
