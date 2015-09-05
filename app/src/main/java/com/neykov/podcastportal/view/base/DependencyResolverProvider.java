package com.neykov.podcastportal.view.base;

import android.support.annotation.NonNull;

import com.neykov.podcastportal.DependencyResolver;

public interface DependencyResolverProvider {

    /**
     * Get an {@link DependencyResolver} instance.
     *
     * <p>This can be used to instantiate needed dependencies or inject them into classes.</p>
     * @see DependencyResolver
     */
    @NonNull
    DependencyResolver getDependencyResolver();
}
