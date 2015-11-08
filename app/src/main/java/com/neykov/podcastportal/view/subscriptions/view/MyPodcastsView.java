package com.neykov.podcastportal.view.subscriptions.view;

import com.neykov.podcastportal.model.subscriptions.SubscriptionsManager;
import com.neykov.podcastportal.view.base.ErrorDisplayView;
import com.neykov.podcastportal.view.base.LoadingView;

public interface MyPodcastsView extends LoadingView, ErrorDisplayView{
    void onSyncStateChanged(SubscriptionsManager.SyncState newState);
}
