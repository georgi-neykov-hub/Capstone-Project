package com.neykov.podcastportal.view.discover.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.neykov.podcastportal.model.entity.Podcast;
import com.neykov.podcastportal.model.entity.Subscription;
import com.neykov.podcastportal.model.networking.GPodderService;
import com.neykov.podcastportal.model.subscriptions.SubscriptionsManager;
import com.neykov.podcastportal.view.base.BasePresenter;
import com.neykov.podcastportal.view.base.ItemListView;
import com.neykov.podcastportal.view.discover.view.PodcastsAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import retrofit.RetrofitError;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

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
    protected Observable<List<Podcast>> getRemotePodcastsObservable() {
        if(mQuery != null) {
            return mService.searchPodcasts(mQuery);
        } else {
            return Observable.defer(() -> Observable.just(new ArrayList<>(0)));
        }
    }

    public void setQuery(String query){
        mQuery = query;
    }
}
