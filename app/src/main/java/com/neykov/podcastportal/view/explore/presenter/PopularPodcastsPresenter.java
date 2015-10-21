package com.neykov.podcastportal.view.explore.presenter;

import android.support.annotation.NonNull;

import com.neykov.podcastportal.model.entity.RemotePodcastData;
import com.neykov.podcastportal.model.networking.GPodderService;
import com.neykov.podcastportal.model.subscriptions.SubscriptionsManager;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

public class PopularPodcastsPresenter extends BaseDiscoverPodcastsPresenter {

    private GPodderService mService;

    @Inject
    public PopularPodcastsPresenter(GPodderService service, SubscriptionsManager manager) {
        super(manager);
        this.mService = service;
    }

    @NonNull
    @Override
    protected Observable<List<RemotePodcastData>> getRemotePodcastsObservable() {
        return mService.getTopPodcasts(100);
    }
}
