package com.neykov.podcastportal;

import com.neykov.podcastportal.model.networking.NetworkingModule;
import com.neykov.podcastportal.view.discover.DiscoverComponent;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
        modules = {
                ApplicationModule.class,
                NetworkingModule.class
        })
public interface DependencyResolver {
    DiscoverComponent getDiscoverComponent();
}
