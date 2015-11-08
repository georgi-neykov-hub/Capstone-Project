package com.neykov.podcastportal;

import com.neykov.podcastportal.model.ModelComponent;
import com.neykov.podcastportal.model.networking.NetworkingModule;
import com.neykov.podcastportal.model.persistence.PersistanceModule;
import com.neykov.podcastportal.util.ApplicationModule;
import com.neykov.podcastportal.view.explore.DiscoverComponent;
import com.neykov.podcastportal.view.player.PlayerComponent;
import com.neykov.podcastportal.view.subscriptions.SubscriptionsComponent;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
        modules = {
                ApplicationModule.class,
                NetworkingModule.class,
                PersistanceModule.class
        })
public interface DependencyResolver {
    @Singleton PlayerComponent getPlayerComponent();
    @Singleton DiscoverComponent getDiscoverComponent();
    @Singleton SubscriptionsComponent getSubscriptionsComponent();
    @Singleton ModelComponent getModelComponent();
}
