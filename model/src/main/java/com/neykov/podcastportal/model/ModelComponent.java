package com.neykov.podcastportal.model;

import com.neykov.podcastportal.model.playlist.PlaylistComponent;
import com.squareup.picasso.Picasso;

import javax.inject.Singleton;

import dagger.Subcomponent;

@Subcomponent
public interface ModelComponent {
    @Singleton
    Picasso getPicasso();

    @Singleton
    PlaylistComponent getPlaylistComponent();
}
