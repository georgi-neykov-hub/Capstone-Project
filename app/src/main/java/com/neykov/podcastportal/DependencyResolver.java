package com.neykov.podcastportal;

import com.google.android.gms.analytics.Tracker;
import com.neykov.podcastportal.analytics.AnalyticsModule;
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
                PersistanceModule.class,
                AnalyticsModule.class
        })
public interface DependencyResolver {
    @Singleton
    PlayerComponent getPlayerComponent();

    @Singleton
    DiscoverComponent getDiscoverComponent();

    @Singleton
    SubscriptionsComponent getSubscriptionsComponent();

    @Singleton
    ModelComponent getModelComponent();

    @Singleton
    Tracker getTracker();

}
