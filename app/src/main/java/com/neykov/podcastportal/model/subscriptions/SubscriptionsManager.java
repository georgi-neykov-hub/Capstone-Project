package com.neykov.podcastportal.model.subscriptions;

import android.accounts.Account;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.Bundle;
import android.os.RemoteException;

import com.neykov.podcastportal.model.BaseManager;
import com.neykov.podcastportal.model.entity.Episode;
import com.neykov.podcastportal.model.entity.RemotePodcastData;
import com.neykov.podcastportal.model.entity.PodcastSubscription;
import com.neykov.podcastportal.model.entity.converter.EpisodesConverter;
import com.neykov.podcastportal.model.entity.converter.SubscriptionConverter;
import com.neykov.podcastportal.model.persistence.DatabaseContract;
import com.neykov.podcastportal.model.utils.Global;
import com.neykov.podcastportal.model.utils.PreferencesHelper;
import com.squareup.sqlbrite.BriteContentResolver;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

public class SubscriptionsManager extends BaseManager {

    private SubscriptionConverter mSubscriptionConverter;
    private EpisodesConverter mEpisodesConverter;
    private SubscriptionDownloader mSubscriptionDownloader;
    private Account mSyncAccount;
    private PreferencesHelper mPreferencesHelper;

    public enum SyncState {
        RUNNING, PENDING, IDLE
    }

    private Subject<SyncState, SyncState> mSyncStateSubject;

    @Inject
    public SubscriptionsManager(@Global Context context, BriteContentResolver mResolver, SubscriptionDownloader downloader, Account syncAccount, PreferencesHelper helper) {
        super(context, mResolver);
        this.mSubscriptionDownloader = downloader;
        this.mSubscriptionConverter = new SubscriptionConverter();
        this.mEpisodesConverter = new EpisodesConverter();
        this.mSyncAccount = syncAccount;
        this.mPreferencesHelper = helper;
        mSyncStateSubject = new SerializedSubject<>(BehaviorSubject.create(getSyncState()));
        ContentResolver.addStatusChangeListener(
                ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE | ContentResolver.SYNC_OBSERVER_TYPE_PENDING,
                which -> {
                    SyncState last = mSyncStateSubject.getValue();
                    SyncState latest = getSyncState();
                    if (last != latest) {
                        mSyncStateSubject.onNext(latest);
                    }
                });
    }

    public Observable<SyncState> getSyncActiveObservable() {
        return mSyncStateSubject.asObservable();
    }

    public SyncState getSyncState() {
        if (ContentResolver.isSyncActive(mSyncAccount, DatabaseContract.CONTENT_AUTHORITY)) {
            return SyncState.RUNNING;
        } else if (ContentResolver.isSyncPending(mSyncAccount, DatabaseContract.CONTENT_AUTHORITY)) {
            return SyncState.PENDING;
        } else {
            return SyncState.IDLE;
        }
    }

    public boolean isSyncRunning() {
        return ContentResolver.isSyncActive(mSyncAccount, DatabaseContract.CONTENT_AUTHORITY);
    }

    public boolean isAutomaticSyncEnabled(){
        return ContentResolver.getSyncAutomatically(mSyncAccount, DatabaseContract.CONTENT_AUTHORITY);
    }

    public void setSyncAutomatically(boolean enabled){
        ContentResolver.setSyncAutomatically(mSyncAccount, DatabaseContract.CONTENT_AUTHORITY, enabled);
    }

    public void requestImmediateSync() {
        Bundle syncOptions = new Bundle();
        syncOptions.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);

        ContentResolver.requestSync(mSyncAccount, DatabaseContract.CONTENT_AUTHORITY, new Bundle());
    }

    public void schedulePeriodicSync(int intervalMinutes) {
        boolean syncIsTurnedOn = ContentResolver.getIsSyncable(mSyncAccount, DatabaseContract.CONTENT_AUTHORITY) > 0;
        if (syncIsTurnedOn) {
            long pollFrequncy = intervalMinutes * 60L;
            ContentResolver.addPeriodicSync(mSyncAccount, DatabaseContract.CONTENT_AUTHORITY, new Bundle(), pollFrequncy);
        }
    }

    public void removePeriodicSync(int intervalMinutes) {
        ContentResolver.removePeriodicSync(mSyncAccount, DatabaseContract.CONTENT_AUTHORITY, new Bundle());
    }

    public Observable<PodcastSubscription> getPodcastStream(long podcastId, boolean notifyForEpisodeChanges) {
        return getBriteResolver().createQuery(
                DatabaseContract.Podcast
                        .buildItemUri(podcastId),
                null,
                null,
                null,
                null,
                notifyForEpisodeChanges)
                .mapToOne(mSubscriptionConverter::convert);
    }

    public Observable<List<Episode>> getLatestEpisodes(PodcastSubscription podcastSubscription, int count) {
        return getBriteResolver().createQuery(
                DatabaseContract.Episode.buildSubscriptionEpisodesUri(podcastSubscription.getId()),
                null,
                null,
                null,
                count > -1 ? DatabaseContract.Episode.RELEASE_DATE + " DESC" :
                        DatabaseContract.Episode.RELEASE_DATE + " DESC LIMIT " + String.valueOf(count),
                false)
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
                .doOnSubscribe(() -> {
                })
                .subscribeOn(Schedulers.io());
    }

    public Observable<Episode> requestDownload(Episode episode) {
        return mSubscriptionDownloader.scheduleDownload(episode, mPreferencesHelper.isDownloadOverMeteredEnabled());
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
                this.requestImmediateSync();
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

    /*package*/ List<PodcastSubscription> getSyncTargets(long[] podcastIds) {

        if (podcastIds == null) {
            throw new IllegalArgumentException("Null Podcast Id array provided.");
        }

        if (podcastIds.length == 0) {
            return Collections.emptyList();
        }

        final int parameterCount = podcastIds.length;
        String whereStatement;
        if (parameterCount == 1) {
            whereStatement = DatabaseContract.Podcast.PODCAST_ID + "=?";
        } else {

            StringBuilder sb = new StringBuilder(parameterCount * 2 - 1);
            sb.append(DatabaseContract.Podcast.PODCAST_ID)
                    .append(" IN (");
            for (int i = 1; i < parameterCount; i++) {
                sb.append(",?");
            }

            sb.append(')');
            whereStatement = sb.toString();
        }

        String[] whereArgs = new String[parameterCount];
        for (int index = 0; index < parameterCount; index++) {
            whereArgs[index] = String.valueOf(podcastIds[index]);
        }

        return getBriteResolver()
                .createQuery(DatabaseContract.Podcast.CONTENT_URI, null, whereStatement, whereArgs, null, false)
                .mapToList(mSubscriptionConverter::convert)
                .toBlocking()
                .first();
    }


    private Observable<PodcastSubscription> subscribeForPodcast(RemotePodcastData podcast) {
        return mSubscriptionDownloader.fetchSubscriptionAndEpisodesData(podcast)
                .subscribeOn(Schedulers.io());
    }
}
