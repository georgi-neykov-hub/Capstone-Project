package com.neykov.podcastportal.view.explore.view;

import com.neykov.podcastportal.model.entity.RemotePodcastData;
import com.neykov.podcastportal.model.entity.PodcastSubscription;
import com.neykov.podcastportal.view.base.fragment.ItemListView;

public interface DiscoverPodcastsView extends ItemListView {

    int ERROR_SUBSCRIBING = 3;
    
    void onPodcastSubcribed(PodcastSubscription podcast);
    void onPodcastUnsubscribed(RemotePodcastData podcast);
}
