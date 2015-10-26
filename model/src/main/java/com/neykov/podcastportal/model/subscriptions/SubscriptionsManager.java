package com.neykov.podcastportal.model.subscriptions;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.RemoteException;

import com.neykov.podcastportal.model.BaseManager;
import com.neykov.podcastportal.model.entity.Episode;
import com.neykov.podcastportal.model.entity.RemotePodcastData;
import com.neykov.podcastportal.model.entity.PodcastSubscription;
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

    private SubscriptionConverter mSubscriptionConverter;
    private EpisodesConverter mEpisodesConverter;
    private SubscriptionDownloader mSubscriptionDownloader;

    @Inject
    public SubscriptionsManager(Context context, BriteContentResolver mResolver, SubscriptionDownloader downloader) {
        super(context, mResolver);
        this.mSubscriptionDownloader = downloader;
        this.mSubscriptionConverter = new SubscriptionConverter();
        this.mEpisodesConverter = new EpisodesConverter();
    }

    public Observable<List<Episode>> getLatestEpisodes(PodcastSubscription podcastSubscription, int count) {
        return getBriteResolver().createQuery(
                DatabaseContract.Episode.buildSubscriptionEpisodesUri(podcastSubscription.getId()),
                null,
                null,
                null,
                count > -1 ? DatabaseContract.Episode.RELEASE_DATE :
                        DatabaseContract.Episode.RELEASE_DATE + " LIMIT " + String.valueOf(count),
                true)
                .mapToList(mEpisodesConverter::convert);
    }

    public Observable<List<Episode>> getEpisodesStream(PodcastSubscription podcastSubscription) {
        return getLatestEpisodes(podcastSubscription, -1);
    }

    public Observable<List<Episode>> getEpisodesStream() {
        return getBriteResolver().createQuery(DatabaseContract.Episode.CONTENT_URI,
                null,
                null,
                null,
                DatabaseContract.Episode.RELEASE_DATE,
                true)
                .mapToList(mEpisodesConverter::convert);
    }

    public Observable<List<PodcastSubscription>> getSubscriptionsStream(boolean notifyForDescendants) {
        return getBriteResolver().createQuery(DatabaseContract.Podcast.CONTENT_URI, null, null, null, DatabaseContract.Podcast.DATE_UPDATED, notifyForDescendants)
                .mapToList(mSubscriptionConverter::convert);
    }

    public Observable<PodcastSubscription> updateSubscription(PodcastSubscription podcastSubscription) {
        return mSubscriptionDownloader.fetchSubscriptionUpdates(podcastSubscription)
                .subscribeOn(Schedulers.io());
    }

    public Observable<RemotePodcastData> unsubscribeFromPodcast(PodcastSubscription podcastSubscription) {
        if (podcastSubscription == null || podcastSubscription.getId() == null) {
            throw new IllegalArgumentException("Null or invalid Subscription argument provided.");
        }

        return Observable.<RemotePodcastData>create(subscriber -> {
            try {
                Uri targetUri = DatabaseContract.Podcast.CONTENT_URI
                        .buildUpon()
                        .appendPath(podcastSubscription.getId().toString())
                        .build();
                ArrayList<ContentProviderOperation> opList = new ArrayList<>();
                opList.add(ContentProviderOperation.newDelete(targetUri)
                        .withExpectedCount(1)
                        .build());

                getApplicationContext().getContentResolver().applyBatch(DatabaseContract.CONTENT_AUTHORITY, opList);
                RemotePodcastData podcast = new RemotePodcastData(
                        podcastSubscription.getTitle(),
                        podcastSubscription.getDescription(),
                        podcastSubscription.getUrl(),
                        podcastSubscription.getWebsite(),
                        podcastSubscription.getSubscribers(),
                        podcastSubscription.getLogoUrl());
                subscriber.onNext(podcast);
                subscriber.onCompleted();
            } catch (RemoteException | OperationApplicationException e) {
                subscriber.onError(e);
            }
        }).subscribeOn(Schedulers.io());
    }

    public Observable<PodcastSubscription> subscribeForPodcast(RemotePodcastData podcast) {
        return mSubscriptionDownloader.fetchSubscriptionAndEpisodesData(podcast)
                .subscribeOn(Schedulers.io());
    }
}
