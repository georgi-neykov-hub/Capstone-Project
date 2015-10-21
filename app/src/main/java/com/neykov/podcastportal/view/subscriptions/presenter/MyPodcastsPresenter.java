package com.neykov.podcastportal.view.subscriptions.presenter;

import android.os.Bundle;

import com.neykov.podcastportal.model.subscriptions.SubscriptionsManager;
import com.neykov.podcastportal.view.base.BasePresenter;
import com.neykov.podcastportal.view.subscriptions.view.MyPodcastsView;

import javax.inject.Inject;

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
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()),
                (itemListView, subscriptions) -> {
                    mAdapter.setData(subscriptions);
                    itemListView.hideLoadingIndicator();
                },
                (itemListView, throwable) -> {
                    itemListView.hideLoadingIndicator();
                });
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

    public SubscriptionsAdapter getAdapter() {
        return mAdapter;
    }

}

