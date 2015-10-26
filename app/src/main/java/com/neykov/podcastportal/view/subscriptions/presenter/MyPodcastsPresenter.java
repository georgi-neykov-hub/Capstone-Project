package com.neykov.podcastportal.view.subscriptions.presenter;

import android.os.Bundle;
import android.support.annotation.WorkerThread;

import com.neykov.podcastportal.model.entity.Episode;
import com.neykov.podcastportal.model.entity.PodcastSubscription;
import com.neykov.podcastportal.model.playlist.PlaylistManager;
import com.neykov.podcastportal.model.subscriptions.SubscriptionsManager;
import com.neykov.podcastportal.view.base.BasePresenter;
import com.neykov.podcastportal.view.base.ErrorDisplayView;
import com.neykov.podcastportal.view.subscriptions.view.MyPodcastsView;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.internal.util.SubscriptionList;
import rx.schedulers.Schedulers;

public class MyPodcastsPresenter extends BasePresenter<MyPodcastsView> {

    public static final int RESTARTABLE_ID_SUBSCRIPTIONS = 1;

    private SubscriptionsAdapter mAdapter;
    private SubscriptionsManager mManager;
    private PlaylistManager mPlaylistManager;
    private SubscriptionList mRowsSubscriptions;

    @Inject
    public MyPodcastsPresenter(SubscriptionsManager manager, PlaylistManager playlistManager) {
        this.mManager = manager;
        this.mPlaylistManager = playlistManager;
        this.mAdapter = new SubscriptionsAdapter();
        this.mRowsSubscriptions = new SubscriptionList();
        mAdapter.setSubscriptionItemListener(mSubscriptionItemListener);
        mAdapter.setEpisodeItemListener(mEpisodeItemListener);

        this.restartable(RESTARTABLE_ID_SUBSCRIPTIONS,
                () -> mManager.getSubscriptionsStream(false)
                        .doOnNext(podcastItems -> mRowsSubscriptions.clear())
                        .flatMap(subscriptions1 -> Observable.from(subscriptions1).map(this::getAdapterItem).toList())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(mAdapter::setData)
                        .map(List::size)
                        .compose(delayUntilViewAvailable())
                        .subscribe(delivery -> delivery.split(
                                (podcastsView, podcastCount) -> podcastsView.hideLoadingIndicator(),
                                (podcastsView, throwable) -> {
                                    podcastsView.hideLoadingIndicator();
                                    podcastsView.showError(ErrorDisplayView.ERROR_GENERAL);
                                })));

        this.start(RESTARTABLE_ID_SUBSCRIPTIONS);
    }

    public SubscriptionsAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        this.add(mRowsSubscriptions);
        start(RESTARTABLE_ID_SUBSCRIPTIONS);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.clearItems();
    }

    private SubscriptionAdapterItem getAdapterItem(PodcastSubscription podcastSubscription) {
        SubscriptionAdapterItem item = new SubscriptionAdapterItem(podcastSubscription);
        Subscription episodesSubscription = mManager.getLatestEpisodes(podcastSubscription, -1)
                .retry()
                .onErrorReturn(throwable -> null)
                .filter(episodes -> episodes != null)
                .subscribe(episodes1 -> item.getAdapter().setData(episodes1));
        mRowsSubscriptions.add(episodesSubscription);
        return item;
    }

    private void unsubscribe(PodcastSubscription podcastSubscription){
        this.add(mManager.unsubscribeFromPodcast(podcastSubscription)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(remotePodcastData -> {}, throwable -> {}));
    }


    private final SubscriptionsAdapter.ItemListener mSubscriptionItemListener = new SubscriptionsAdapter.ItemListener() {
        @Override
        public void onUnsubscribeClick(int position) {
           unsubscribe(getAdapter().getItem(position).getSubscription());
        }

        @Override
        public void onRefreshClick(int position) {
            mManager.updateSubscription(getAdapter().getItem(position).getSubscription());
        }

        @Override
        public void onItemClick(int position) {

        }
    };

    private final SubscriptionAdapterItem.EpisodeItemListener mEpisodeItemListener = new SubscriptionAdapterItem.EpisodeItemListener() {
        @Override
        public void onAddToPlaylistTop(Episode episode) {
            add(mPlaylistManager.addToTop(episode).subscribe());
        }

        @Override
        public void onAddToPlaylistEnd(Episode episode) {
            add(mPlaylistManager.addToEnd(episode).subscribe());
        }

        @Override
        public void onRemoveFromPlaylist(Episode episode) {
            add(mPlaylistManager.remove(episode).subscribe());
        }

        @Override
        public void onDownload(Episode episode) {

        }

        @Override
        public void onSelected(Episode episode) {

        }
    };
}

