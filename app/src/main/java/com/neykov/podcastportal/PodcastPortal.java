package com.neykov.podcastportal;

import android.app.Application;
import android.support.annotation.NonNull;

import com.neykov.podcastportal.model.ApplicationModule;
import com.neykov.podcastportal.view.base.DependencyResolverProvider;
import com.squareup.picasso.Picasso;

public class PodcastPortal extends Application implements DependencyResolverProvider{

    private DependencyResolver mDependencyResolver;

    @Override
    public void onCreate() {
        super.onCreate();
        mDependencyResolver = DaggerDependencyResolver.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
        Picasso.setSingletonInstance(getDependencyResolver().getPicasso());
    }

    @NonNull
    @Override
    public DependencyResolver getDependencyResolver() {
        return mDependencyResolver;
    }
}
