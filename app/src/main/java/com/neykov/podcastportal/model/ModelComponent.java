package com.neykov.podcastportal.model;

import com.neykov.podcastportal.model.networking.connectivity.ConnectivityMonitor;
import com.neykov.podcastportal.model.playlist.PlaylistManager;
import com.neykov.podcastportal.model.subscriptions.SubscriptionsManager;
import com.neykov.podcastportal.model.utils.PreferencesHelper;
import com.squareup.picasso.Picasso;

import javax.inject.Singleton;

import dagger.Subcomponent;

@Subcomponent
public interface ModelComponent {
    @Singleton
    Picasso getPicasso();

    @Singleton
    SubscriptionsManager getSubscriptionsManager();

    @Singleton
    PlaylistManager getPlaylistManager();

    @Singleton
    PreferencesHelper getPreferencesHelper();

    @Singleton
    ConnectivityMonitor getConnectivityMonitor();
}
