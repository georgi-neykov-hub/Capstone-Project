package com.neykov.podcastportal.model.subscriptions;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.service.media.MediaBrowserService;

import com.neykov.podcastportal.model.BaseManager;
import com.neykov.podcastportal.model.entity.Episode;
import com.neykov.podcastportal.model.entity.RemotePodcastData;
import com.neykov.podcastportal.model.entity.PodcastSubscription;
import com.neykov.podcastportal.model.entity.converter.EpisodesConverter;
import com.neykov.podcastportal.model.entity.converter.SubscriptionConverter;
import com.neykov.podcastportal.model.persistence.DatabaseContract;
import com.neykov.podcastportal.model.utils.Global;
import com.squareup.sqlbrite.BriteContentResolver;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;

public class SubscriptionsManager extends BaseManager {

    private SubscriptionConverter mSubscriptionConverter;
    private EpisodesConverter mEpisodesConverter;
    private SubscriptionDownloader mSubscriptionDownloader;

    @Inject
    public SubscriptionsManager(@Global Context context, BriteContentResolver mResolver, SubscriptionDownloader downloader) {
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
                getApplicationContext().getContentResolver()
                        .delete(
                                DatabaseContract.Podcast.CONTENT_URI,
                                DatabaseContract.Podcast.PODCAST_ID + "=?",
                                new String[]{podcastSubscription.getId().toString()});
            } catch (Exception e) {
                subscriber.onError(e);
            }

            RemotePodcastData podcast = new RemotePodcastData(
                    podcastSubscription.getTitle(),
                    podcastSubscription.getDescription(),
                    podcastSubscription.getUrl(),
                    podcastSubscription.getWebsite(),
                    podcastSubscription.getSubscribers(),
                    podcastSubscription.getLogoUrl());
            subscriber.onNext(podcast);
            subscriber.onCompleted();
        }).subscribeOn(Schedulers.io());
    }

    public Observable<PodcastSubscription> subscribeForPodcast(RemotePodcastData podcast, boolean fetchEpisodesImmediately) {
        if (fetchEpisodesImmediately) {
            return subscribeForPodcast(podcast);
        }
        return Observable.<PodcastSubscription>create(subscriber -> {
            try {
                PodcastSubscription.Builder builder = new PodcastSubscription.Builder(podcast)
                        .setDateUpdated(new Date(0));

                ArrayList<ContentProviderOperation> ops = new ArrayList<>();
                ops.add(ContentProviderOperation.newInsert(DatabaseContract.Podcast.CONTENT_URI)
                        .withValues(mSubscriptionConverter.convert(builder.build()))
                        .build());

                ContentProviderResult[] results = getApplicationContext().getContentResolver().applyBatch(DatabaseContract.CONTENT_AUTHORITY, ops);
                long insertedItemId = ContentUris.parseId(results[0].uri);
                PodcastSubscription subscription = builder.setId(insertedItemId).build();
                subscriber.onNext(subscription);
                subscriber.onCompleted();
            } catch (RemoteException | OperationApplicationException e) {
                subscriber.onError(e);
            }
        })
                .subscribeOn(Schedulers.io())
                .doOnNext(podcastSubscription1 -> mSubscriptionDownloader.downloadSubscriptionThumbnail(podcastSubscription1)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn(throwable1 -> podcastSubscription1)
                        .flatMap(this::updateSubscription)
                        .onErrorReturn(throwable -> null)
                        .subscribeOn(Schedulers.io())
                        .subscribe());
    }

    private Observable<PodcastSubscription> subscribeForPodcast(RemotePodcastData podcast) {
        return mSubscriptionDownloader.fetchSubscriptionAndEpisodesData(podcast)
                .subscribeOn(Schedulers.io());
    }
}
