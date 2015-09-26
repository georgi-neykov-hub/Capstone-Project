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
import com.neykov.podcastportal.view.discover.view.DiscoverPodcastsView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit.RetrofitError;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public abstract class BaseDiscoverPodcastsPresenter extends BasePresenter<DiscoverPodcastsView> {

    private static final String KEY_REMOTE_ITEMS = "PodcastSearchPresenter.KEY_REMOTE_ITEMS";

    protected PodcastsAdapter mAdapter;
    protected SubscriptionsManager mSubscriptionsManager;
    private ArrayList<Podcast> mRemoteItems;

    public BaseDiscoverPodcastsPresenter(SubscriptionsManager manager) {
        this.mSubscriptionsManager = manager;
        mAdapter = new PodcastsAdapter();
    }

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        if (savedState != null) {
            ArrayList<Podcast> savedItems = savedState.getParcelableArrayList(KEY_REMOTE_ITEMS);
            if (savedItems != null) {
                mRemoteItems.addAll(savedItems);
            }
        }
    }

    @Override
    protected void onSave(Bundle state) {
        super.onSave(state);
        state.putParcelableArrayList(KEY_REMOTE_ITEMS, mRemoteItems);
    }

    public PodcastsAdapter getAdapter() {
        return mAdapter;
    }

    public void refreshData() {
        mRemoteItems = null;
        mAdapter.clearItems();
        fetchPodcastItems();
    }

    public void loadItems(ItemListView view) {
        view.showLoadingIndicator();
        fetchPodcastItems();
    }

    public void unsubscribeFromPodcast(int position, Subscription subscription) {
        //noinspection ConstantConditions
        getView().showLoadingIndicator();
        mSubscriptionsManager.unsubscribeFromPodcast(subscription)
                .compose(delayUntilViewAvailable())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(itemListViewSubscriptionDelivery -> itemListViewSubscriptionDelivery.split(
                        (itemListView, podcast) -> {
                            itemListView.hideLoadingIndicator();
                            mAdapter.swapItem(position, podcast);
                            itemListView.onPodcastUnsubscribed(podcast);
                        }, (popularPodcastsView, throwable) -> {
                            popularPodcastsView.hideLoadingIndicator();
                            popularPodcastsView.showListLoadError(DiscoverPodcastsView.ERROR_SUBSCRIBING);
                        }));
    }

    public void subscribeForPodcast(int position, Podcast podcast) {
        //noinspection ConstantConditions
        getView().showLoadingIndicator();
        mSubscriptionsManager.subscribeForPodcast(podcast)
                .compose(delayUntilViewAvailable())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(itemListViewSubscriptionDelivery -> itemListViewSubscriptionDelivery.split(
                        (itemListView, subscription) -> {
                            itemListView.hideLoadingIndicator();
                            mAdapter.swapItem(position, subscription);
                            itemListView.onPodcastSubcribed(subscription);
                        }, (popularPodcastsView, throwable) -> {
                            popularPodcastsView.hideLoadingIndicator();
                            popularPodcastsView.showListLoadError(DiscoverPodcastsView.ERROR_SUBSCRIBING);
                        }));
    }

    protected abstract @NonNull Observable<List<Podcast>> getRemotePodcastsObservable();

    protected void onRemoteDataLoaded(List<Podcast> remoteItems){
        mRemoteItems = (ArrayList<Podcast>) remoteItems;
    }

    private Observable<Podcast> resolveRemotePodcastsObservable(){
        Observable<List<Podcast>> observable;
        if (mRemoteItems != null) {
            observable = Observable.defer(() -> Observable.just(mRemoteItems));
        } else {
            observable = getRemotePodcastsObservable();
        }

        return observable.doOnNext(this::onRemoteDataLoaded)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(Observable::from);
    }

    private void fetchPodcastItems() {
        Observable<Map<String, Subscription>> subscriptionsMapObservable = mSubscriptionsManager.getSubscriptions()
                .firstOrDefault(new ArrayList<>(0))
                .subscribeOn(Schedulers.io())
                .flatMap(Observable::from)
                .toMap(Podcast::getUrl);

        Observable<Podcast> remoteResultsObservable = resolveRemotePodcastsObservable();

        remoteResultsObservable.withLatestFrom(subscriptionsMapObservable, (podcast, stringSubscriptionMap) -> {
            Podcast matchedSubscription = stringSubscriptionMap.get(podcast.getUrl());
            return matchedSubscription != null ? matchedSubscription : podcast;
        })
                .toSortedList((podcast1, podcast2) -> - podcast1.compareTo(podcast2))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(delayUntilViewAvailable())
                .subscribe(baseListViewListDelivery -> {
                    baseListViewListDelivery.split((baseListView, podcasts) -> {
                        baseListView.hideLoadingIndicator();
                        mAdapter.clearItems();
                        mAdapter.addItems(podcasts);
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