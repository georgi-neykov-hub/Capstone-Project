package com.neykov.podcastportal.view.base.fragment;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.neykov.podcastportal.DependencyResolver;
import com.neykov.podcastportal.view.base.DependencyResolverProvider;

/**
 * Created by Georgi on 6.9.2015 г..
 */
public class BaseFragment extends Fragment implements DependencyResolverProvider {

    @NonNull
    @Override
    public DependencyResolver getDependencyResolver() {
        return ((DependencyResolverProvider)getContext().getApplicationContext())
                .getDependencyResolver();
    }
}
