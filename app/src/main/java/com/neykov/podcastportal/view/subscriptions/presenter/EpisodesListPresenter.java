package com.neykov.podcastportal.view.subscriptions.presenter;

import android.os.Bundle;

import com.neykov.podcastportal.model.entity.Subscription;
import com.neykov.podcastportal.model.subscriptions.SubscriptionsManager;
import com.neykov.podcastportal.view.base.BasePresenter;
import com.neykov.podcastportal.view.base.fragment.ItemListView;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class EpisodesListPresenter extends BasePresenter<ItemListView> {

    private NestedEpisodeAdapter mAdapter;
    private SubscriptionsManager mManager;
    private rx.Subscription mStreamSubscription;

    @Inject
    public EpisodesListPresenter(SubscriptionsManager manager) {
        mManager = manager;
    }

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        mAdapter = new NestedEpisodeAdapter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unsubscribeFromEpisodeStream();
        mAdapter.clearItems();
        mAdapter = null;
    }

    public void refreshEpisodes(Subscription target){
        rx.Subscription subscription = mManager.updateSubscription(target)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(delayUntilViewAvailable())
                .subscribe(delivery -> delivery.split(
                        (itemListView, subscription1) -> itemListView.hideLoadingIndicator(),
                        (itemListView, throwable) -> itemListView.hideLoadingIndicator()));
        this.add(subscription);
    }

    public void subscribeForEpisodeStream(Subscription target){
        mStreamSubscription = mManager.getEpisodesStream(target)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(deliverLatestCache())
                .subscribe(itemListViewListDelivery -> itemListViewListDelivery.split(
                        (itemListView, episodes) -> {
                            itemListView.hideLoadingIndicator();
                            mAdapter.setData(episodes);
                        },
                        (itemListView, throwable) -> {
                            itemListView.hideLoadingIndicator();
                        }));
        this.add(mStreamSubscription);
    }

    public void unsubscribeFromEpisodeStream(){
        this.remove(mStreamSubscription);
        mStreamSubscription = null;
    }

    public NestedEpisodeAdapter getAdapter() {
        return mAdapter;
    }
}
