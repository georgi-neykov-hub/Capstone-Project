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

public class PodcastSearchPresenter extends BasePresenter<ItemListView>{

    private static final String KEY_SEARCH_QUERY = "PodcastSearchPresenter.KEY_SEARCH_QUERY";
    private static final String KEY_REMOTE_ITEMS = "PodcastSearchPresenter.KEY_REMOTE_ITEMS";

    private GPodderService mService;
    private PodcastsAdapter mAdapter;
    private SubscriptionsManager mSubscriptionsManager;

    private ArrayList<Podcast> mRemoteItems;

    private String mQuery;

    @Inject
    public PodcastSearchPresenter(GPodderService mService, SubscriptionsManager manager) {
        this.mService = mService;
        this.mSubscriptionsManager = manager;
        mAdapter = new PodcastsAdapter();
        mRemoteItems = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        if (savedState != null) {
            mQuery = savedState.getString(KEY_SEARCH_QUERY);
            ArrayList<Podcast> savedItems = savedState.getParcelableArrayList(KEY_REMOTE_ITEMS);
            if (savedItems != null) {
                mRemoteItems.addAll(savedItems);
            }
        }
    }

    @Override
    protected void onSave(Bundle state) {
        super.onSave(state);
        state.putString(KEY_SEARCH_QUERY, mQuery);
        state.putParcelableArrayList(KEY_REMOTE_ITEMS, mRemoteItems);
    }

    public PodcastsAdapter getAdapter(){
        return mAdapter;
    }

    public void setQuery(String query){
        mQuery = query;
    }

    public void loadItems(ItemListView view, boolean showLoading) {
        if (showLoading) view.showLoadingIndicator();
        mAdapter.clearItems();
        mAdapter.notifyDataSetChanged();
        if (mQuery != null) {
            executeSearchQuery(mQuery);
        } else {
            view.hideLoadingIndicator();
        }
    }

    private List<Podcast> swapWithSubscriptions(List<Podcast> podcasts, Map<String, Subscription> subscriptionsMap){
        Podcast currentPodcast;
        Subscription match;
        for(int itemIndex = 0; itemIndex < podcasts.size(); itemIndex++){
            currentPodcast = podcasts.get(itemIndex);
            match = subscriptionsMap.get(currentPodcast.getUrl());
            if(match != null){
                podcasts.set(itemIndex, match);
            }
        }
        return podcasts;
    }

    private Observable<Map<String, Subscription>> getSubscriptionsMap(){
        return mSubscriptionsManager.getSubscriptions()
                .last()
                .flatMap(Observable::from)
                .toMap(Podcast::getUrl);
    }

    private void executeSearchQuery(@NonNull String query) {
        mService.searchPodcasts(query)
                .withLatestFrom(getSubscriptionsMap(), this::swapWithSubscriptions)
                .compose(delayUntilViewAvailable())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(baseListViewListDelivery -> {
                    baseListViewListDelivery.split((baseListView, podcasts) -> {
                        mAdapter.addItems(podcasts);
                        mAdapter.notifyDataSetChanged();
                        baseListView.hideLoadingIndicator();
                    }, (baseListView1, throwable) -> {
                        baseListView1.hideLoadingIndicator();
                        if (throwable instanceof RetrofitError) {
                            RetrofitError typedError = (RetrofitError) throwable;
                            if (typedError.getKind() == RetrofitError.Kind.NETWORK) {
                                baseListView1.showListLoadError(ItemListView.ERROR_NETWORK);
                            }
                        } else {
                            baseListView1.showListLoadError(ItemListView.ERROR_GENERAL);
                        }
                    });
                });
    }
}
