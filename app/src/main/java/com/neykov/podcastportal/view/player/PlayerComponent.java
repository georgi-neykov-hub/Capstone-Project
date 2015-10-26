package com.neykov.podcastportal.view.player;

import com.neykov.podcastportal.view.player.presenter.PlayerPresenter;
import com.neykov.podcastportal.view.player.presenter.PlaylistPresenter;
import com.neykov.podcastportal.view.subscriptions.presenter.EpisodesListPresenter;
import com.neykov.podcastportal.view.subscriptions.presenter.MyPodcastsPresenter;

import javax.inject.Singleton;

import dagger.Subcomponent;

@Subcomponent
@Singleton
public interface PlayerComponent {
    PlayerPresenter createPlayerPresenter();
    PlaylistPresenter createPlaylistPresenter();
}
