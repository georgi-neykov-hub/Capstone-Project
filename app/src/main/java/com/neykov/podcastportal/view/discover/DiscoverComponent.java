package com.neykov.podcastportal.view.discover;

import dagger.Subcomponent;

@Subcomponent
public interface DiscoverComponent {
    DiscoverPresenter createDiscoverPresenter();
}
