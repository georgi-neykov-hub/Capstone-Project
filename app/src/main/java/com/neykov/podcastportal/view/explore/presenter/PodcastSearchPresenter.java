package com.neykov.podcastportal.view.explore.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.neykov.podcastportal.model.entity.RemotePodcastData;
import com.neykov.podcastportal.model.networking.GPodderService;
import com.neykov.podcastportal.model.subscriptions.SubscriptionsManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;

public class PodcastSearchPresenter extends BaseDiscoverPodcastsPresenter{

    private static final String KEY_SEARCH_QUERY = "PodcastSearchPresenter.KEY_SEARCH_QUERY";

    private GPodderService mService;
    private String mQuery;

    @Inject
    public PodcastSearchPresenter(GPodderService mService, SubscriptionsManager manager) {
        super(manager);
        this.mService = mService;
    }

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        if (savedState != null) {
            mQuery = savedState.getString(KEY_SEARCH_QUERY);
        }
    }

    @Override
    protected void onSave(Bundle state) {
        super.onSave(state);
        state.putString(KEY_SEARCH_QUERY, mQuery);
    }

    @NonNull
    @Override
    protected Observable<List<RemotePodcastData>> getRemotePodcastsObservable() {
        if(mQuery != null) {
            return mService.searchPodcasts(mQuery);
        } else {
            return Observable.just(new ArrayList<>(0));
        }
    }

    public void setQuery(String query){
        mQuery = query;
    }
}
