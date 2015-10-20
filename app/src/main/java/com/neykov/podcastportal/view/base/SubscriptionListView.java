package com.neykov.podcastportal.view.base;

import com.neykov.podcastportal.model.entity.Subscription;
import com.neykov.podcastportal.view.base.fragment.ItemListView;

public interface SubscriptionListView extends ItemListView {
    void onUnsubscribe(Subscription subscription);
    void onUnsubscribeError(Subscription subscription);
}
