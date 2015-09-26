package com.neykov.podcastportal.view.discover.view;

import com.neykov.podcastportal.model.entity.Podcast;
import com.neykov.podcastportal.model.entity.Subscription;
import com.neykov.podcastportal.view.base.ItemListView;

public interface DiscoverPodcastsView extends ItemListView {

    int ERROR_SUBSCRIBING = 3;
    
    void onPodcastSubcribed(Subscription podcast);
    void onPodcastUnsubscribed(Podcast podcast);
}
