package com.neykov.podcastportal.view.subscriptions.presenter;

import android.os.Bundle;
import android.support.annotation.WorkerThread;

import com.neykov.podcastportal.model.entity.Episode;
import com.neykov.podcastportal.model.entity.Subscription;
import com.neykov.podcastportal.model.subscriptions.SubscriptionsManager;
import com.neykov.podcastportal.view.base.BasePresenter;
import com.neykov.podcastportal.view.base.ErrorDisplayView;
import com.neykov.podcastportal.view.subscriptions.view.MyPodcastsView;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MyPodcastsPresenter extends BasePresenter<MyPodcastsView> {

    public static final int RESTARTABLE_ID_SUBSCRIPTIONS = 1;

    private SubscriptionsAdapter mAdapter;
    private SubscriptionsManager mManager;

    @Inject
    public MyPodcastsPresenter(SubscriptionsManager manager) {
        this.mManager = manager;
        this.mAdapter = new SubscriptionsAdapter();

        this.restartableLatestCache(RESTARTABLE_ID_SUBSCRIPTIONS,
                () -> mManager.getSubscriptionsStream()
                        .flatMap(Observable::from)
                        .map(this::getAdapterItem)
                        .toList()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()),
                (podcastsView, subscriptions) -> {
                    mAdapter.setData(subscriptions);
                    podcastsView.hideLoadingIndicator();
                },
                (podcastsView, throwable) -> {
                    podcastsView.hideLoadingIndicator();
                    podcastsView.showError(ErrorDisplayView.ERROR_GENERAL);
                });
    }

    public SubscriptionsAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.clearItems();
    }

    @Override
    protected void onTakeView(MyPodcastsView itemListView) {
        super.onTakeView(itemListView);
        start(RESTARTABLE_ID_SUBSCRIPTIONS);
    }

    @Override
    protected void onDropView() {
        stop(RESTARTABLE_ID_SUBSCRIPTIONS);
        super.onDropView();
    }

    @WorkerThread
    private SubscriptionAdapterItem getAdapterItem(Subscription subscription){
        List<Episode> latestEpisodes = getLatestEpisodes(subscription);
        return new SubscriptionAdapterItem(subscription, latestEpisodes);
    }

    @WorkerThread
    private List<Episode> getLatestEpisodes(Subscription subscription){
        return mManager.getLatestEpisodes(subscription, 10)
                .first()
                .toBlocking()
                .single();
    }

}

