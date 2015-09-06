package com.neykov.podcastportal.view.base;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.neykov.podcastportal.DependencyResolver;

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
