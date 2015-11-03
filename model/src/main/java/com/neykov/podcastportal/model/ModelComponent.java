package com.neykov.podcastportal.model;

import com.neykov.podcastportal.model.playlist.PlaylistComponent;
import com.squareup.picasso.Picasso;

import javax.inject.Singleton;

import dagger.Subcomponent;

/**
 * Created by Georgi on 31.10.2015 г..
 */

@Subcomponent
public interface ModelComponent {
    @Singleton
    Picasso getPicasso();

    @Singleton
    PlaylistComponent getPlaylistComponent();

}
