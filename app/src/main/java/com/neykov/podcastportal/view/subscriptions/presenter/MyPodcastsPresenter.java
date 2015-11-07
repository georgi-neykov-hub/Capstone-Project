package com.neykov.podcastportal.view.subscriptions.presenter;

import android.os.Bundle;

import com.neykov.podcastportal.model.entity.Episode;
import com.neykov.podcastportal.model.entity.PodcastSubscription;
import com.neykov.podcastportal.model.playlist.PlaylistManager;
import com.neykov.podcastportal.model.subscriptions.SubscriptionsManager;
import com.neykov.podcastportal.view.base.BasePresenter;
import com.neykov.podcastportal.view.base.ErrorDisplayView;
import com.neykov.podcastportal.view.subscriptions.view.MyPodcastsView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.internal.util.SubscriptionList;
import rx.schedulers.Schedulers;

public class MyPodcastsPresenter extends BasePresenter<MyPodcastsView> {

    public static final int RESTARTABLE_ID_SUBSCRIPTIONS = 1;

    private MyPodcastsAdapter mAdapter;
    private SubscriptionsManager mManager;
    private PlaylistManager mPlaylistManager;
    private SubscriptionList mRowsSubscriptions;

    @Inject
    public MyPodcastsPresenter(SubscriptionsManager manager, PlaylistManager playlistManager) {
        this.mManager = manager;
        this.mPlaylistManager = playlistManager;
        this.mAdapter = new MyPodcastsAdapter();
        this.mRowsSubscriptions = new SubscriptionList();
        mAdapter.setSubscriptionItemListener(mSubscriptionItemListener);

        this.restartable(RESTARTABLE_ID_SUBSCRIPTIONS,
                () -> mManager.getSubscriptionsStream(false)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(this::createAttachRowAdapterItems)
                        .compose(delayUntilViewAvailable())
                        .subscribe(delivery -> delivery.split(
                                (podcastsView, podcastCount) -> podcastsView.hideLoadingIndicator(),
                                (podcastsView, throwable) -> {
                                    podcastsView.hideLoadingIndicator();
                                    podcastsView.showError(ErrorDisplayView.ERROR_GENERAL);
                                })));

        this.start(RESTARTABLE_ID_SUBSCRIPTIONS);
    }

    public MyPodcastsAdapter getAdapter() {
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
        mRowsSubscriptions.clear();
        mAdapter.clearItems();
    }

    private int createAttachRowAdapterItems(List<PodcastSubscription> podcasts){
        mRowsSubscriptions.clear();
        List<SubscriptionAdapterItem> items = new ArrayList<>(podcasts.size());
        PodcastSubscription currentSubscription;
        for (int position = 0; position < podcasts.size(); position++){
            currentSubscription = podcasts.get(position);
            SubscriptionAdapterItem item = new SubscriptionAdapterItem(currentSubscription);
            items.add(item);

            final int currentPosition = position;
            Subscription episodesSubscription = mManager.getLatestEpisodes(currentSubscription, -1)
                    .retry()
                    .onErrorReturn(throwable -> null)
                    .filter(episodes -> episodes != null)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(episodes1 -> {
                        item.getAdapter().setData(episodes1);
                        mAdapter.notifyDataSetChanged();
                    });
            mRowsSubscriptions.add(episodesSubscription);
        }
        mAdapter.setData(items);
        return mAdapter.getItemCount();
    }

    private void unsubscribe(PodcastSubscription podcastSubscription){
        this.add(mManager.unsubscribeFromPodcast(podcastSubscription)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(remotePodcastData -> {
                }, throwable -> {
                }));
    }

    private void startDownload(Episode episode){
        this.add(mManager.requestDownload(episode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(remotePodcastData -> {
                }, throwable -> {
                }));
    }

    @SuppressWarnings("FieldCanBeLocal")
    private final MyPodcastsAdapter.ItemListener mSubscriptionItemListener = new MyPodcastsAdapter.ItemListener() {
        @Override
        public void onUnsubscribeClick(int position) {
           unsubscribe(getAdapter().getItem(position).getSubscription());
        }

        @Override
        public void onRefreshClick(int position) {
            mManager.updateSubscription(getAdapter().getItem(position).getSubscription());
        }

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
            startDownload(episode);
        }

        @Override
        public void onSelected(Episode episode) {

        }
    };
}

