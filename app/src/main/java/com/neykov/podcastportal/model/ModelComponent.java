package com.neykov.podcastportal.model;

import com.neykov.podcastportal.model.playlist.PlaylistManager;
import com.neykov.podcastportal.model.subscriptions.SubscriptionsManager;
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
}
