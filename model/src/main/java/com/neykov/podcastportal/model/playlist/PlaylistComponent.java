package com.neykov.podcastportal.model.playlist;

import dagger.Subcomponent;

/**
 * Created by Georgi on 31.10.2015 г..
 */
@Subcomponent
public interface PlaylistComponent {
    PlaylistManager getPlaylistManager();
}
