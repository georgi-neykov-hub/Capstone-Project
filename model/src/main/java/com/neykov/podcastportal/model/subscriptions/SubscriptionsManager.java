package com.neykov.podcastportal.model.subscriptions;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.RemoteException;
import android.support.annotation.WorkerThread;

import com.neykov.podcastportal.model.BaseManager;
import com.neykov.podcastportal.model.entity.Episode;
import com.neykov.podcastportal.model.entity.RemotePodcastData;
import com.neykov.podcastportal.model.entity.Subscription;
import com.neykov.podcastportal.model.entity.converter.EpisodesConverter;
import com.neykov.podcastportal.model.entity.converter.SubscriptionConverter;
import com.neykov.podcastportal.model.persistence.DatabaseContract;
import com.squareup.sqlbrite.BriteContentResolver;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;

public class SubscriptionsManager extends BaseManager {

    private BriteContentResolver mResolver;
    private SubscriptionConverter mSubscriptionConverter;
    private EpisodesConverter mEpisodesConverter;
    private SubscriptionDownloader mSubscriptionDownloader;

    @Inject
    public SubscriptionsManager(Context context, BriteContentResolver mResolver, SubscriptionDownloader downloader) {
        super(context);
        this.mResolver = mResolver;
        this.mSubscriptionDownloader = downloader;
        this.mSubscriptionConverter = new SubscriptionConverter();
        this.mEpisodesConverter = new EpisodesConverter();
    }

    public Observable<List<Episode>> getLatestEpisodes(Subscription subscription, int count) {
        return getQueryResolver().createQuery(DatabaseContract.Episode.CONTENT_URI,
                null,
                DatabaseContract.Episode.PODCAST_ID + " = ? LIMIT ?",
                new String[]{subscription.getId().toString(), String.valueOf(count)},
                DatabaseContract.Episode.RELEASE_DATE,
                false)
                .mapToList(mEpisodesConverter::convert);
    }

    public Observable<List<Episode>> getEpisodesStream(Subscription subscription) {
        return getQueryResolver().createQuery(DatabaseContract.Episode.CONTENT_URI,
                null,
                DatabaseContract.Episode.PODCAST_ID + " = ?",
                new String[]{subscription.getId().toString()},
                DatabaseContract.Episode.RELEASE_DATE,
                true)
                .mapToList(mEpisodesConverter::convert);
    }

    public Observable<List<Episode>> getEpisodesStream() {
        return getQueryResolver().createQuery(DatabaseContract.Episode.CONTENT_URI,
                null,
                null,
                null,
                DatabaseContract.Episode.RELEASE_DATE,
                true)
                .mapToList(mEpisodesConverter::convert);
    }

    public Observable<List<Subscription>> getSubscriptionsStream() {
        return getQueryResolver().createQuery(DatabaseContract.Subscription.CONTENT_URI, null, null, null, DatabaseContract.Subscription.DATE_UPDATED, false)
                .mapToList(mSubscriptionConverter::convert);
    }

    public Observable<Subscription> updateSubscription(Subscription subscription) {
        return mSubscriptionDownloader.fetchSubscriptionUpdates(subscription)
                .subscribeOn(Schedulers.io());
    }

    public Observable<RemotePodcastData> unsubscribeFromPodcast(Subscription subscription) {
        if (subscription == null || subscription.getId() == null) {
            throw new IllegalArgumentException("Null or invalid Subscription argument provided.");
        }

        return Observable.<RemotePodcastData>create(subscriber -> {
            try {
                Uri targetUri = DatabaseContract.Subscription.CONTENT_URI
                        .buildUpon()
                        .appendPath(subscription.getId().toString())
                        .build();
                ArrayList<ContentProviderOperation> opList = new ArrayList<>();
                opList.add(ContentProviderOperation.newDelete(targetUri)
                        .withExpectedCount(1)
                        .build());

                getApplicationContext().getContentResolver().applyBatch(DatabaseContract.CONTENT_AUTHORITY, opList);
                RemotePodcastData podcast = new RemotePodcastData(
                        subscription.getTitle(),
                        subscription.getDescription(),
                        subscription.getUrl(),
                        subscription.getWebsite(),
                        subscription.getSubscribers(),
                        subscription.getLogoUrl());
                subscriber.onNext(podcast);
                subscriber.onCompleted();
            } catch (RemoteException | OperationApplicationException e) {
                subscriber.onError(e);
            }
        }).subscribeOn(Schedulers.io());
    }

    public Observable<Subscription> subscribeForPodcast(RemotePodcastData podcast) {
        return mSubscriptionDownloader.fetchSubscriptionAndEpisodesData(podcast)
                .subscribeOn(Schedulers.io());
    }

    private BriteContentResolver getQueryResolver() {
        return mResolver;
    }
}
