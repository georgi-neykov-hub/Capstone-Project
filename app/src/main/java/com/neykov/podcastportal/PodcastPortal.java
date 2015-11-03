package com.neykov.podcastportal;

import android.app.Application;
import android.support.annotation.NonNull;

import com.neykov.podcastportal.model.ModelComponent;
import com.neykov.podcastportal.model.ModelComponentProvider;
import com.neykov.podcastportal.util.ApplicationModule;
import com.neykov.podcastportal.view.base.DependencyResolverProvider;
import com.squareup.picasso.Picasso;

public class PodcastPortal extends Application implements DependencyResolverProvider, ModelComponentProvider {

    private DependencyResolver mDependencyResolver;

    @Override
    public void onCreate() {
        super.onCreate();
        mDependencyResolver = DaggerDependencyResolver.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
        Picasso.setSingletonInstance(getDependencyResolver().getModelComponent().getPicasso());
    }

    @NonNull
    @Override
    public DependencyResolver getDependencyResolver() {
        return mDependencyResolver;
    }

    @Override
    public ModelComponent getModelComponent() {
        return getDependencyResolver().getModelComponent();
    }
}
