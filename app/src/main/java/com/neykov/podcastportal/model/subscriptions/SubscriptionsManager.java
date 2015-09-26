package com.neykov.podcastportal.model.subscriptions;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.neykov.podcastportal.model.entity.Episode;
import com.neykov.podcastportal.model.entity.Podcast;
import com.neykov.podcastportal.model.entity.Subscription;
import com.neykov.podcastportal.model.entity.converter.SubscriptionConverter;
import com.neykov.podcastportal.model.persistence.DatabaseContract;
import com.squareup.sqlbrite.BriteContentResolver;
import com.squareup.sqlbrite.SqlBrite;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SubscriptionsManager {

    private Context mContext;
    private BriteContentResolver mResolver;
    private SubscriptionConverter mConverter;

    @Inject
    public SubscriptionsManager(Context mContext, BriteContentResolver resolver) {
        this.mContext = mContext;
        this.mResolver = resolver;
        this.mConverter = new SubscriptionConverter();
    }

    public Observable<List<Subscription>> getSubscriptions() {
        return getQueryResolver().createQuery(DatabaseContract.Subscription.CONTENT_URI, null, null, null, null, true)
                .mapToList(mConverter::convert);
    }

    public Observable<Podcast> unsubscribeFromPodcast(Subscription subscription){
        if(subscription == null || subscription.getId() == null){
            throw new IllegalArgumentException("Null or invalid Subscription argument provided.");
        }

        return Observable.<Podcast>create(subscriber -> {
            Uri targetUri = DatabaseContract.Subscription.CONTENT_URI
                    .buildUpon()
                    .appendPath(subscription.getId().toString())
                    .build();
            getContext().getContentResolver().delete(targetUri, null, null);
            Podcast podcast = new Podcast(
                    subscription.getTitle(),
                    subscription.getDescription(),
                    subscription.getUrl(),
                    subscription.getWebsite(),
                    subscription.getSubscribers(),
                    subscription.getLogoUrl());
            subscriber.onNext(podcast);
            subscriber.onCompleted();
        }).subscribeOn(Schedulers.io());
    }

    public Observable<Subscription> subscribeForPodcast(Podcast podcast){
        return Observable.<Subscription>create(subscriber -> {
            //Insert the values.
            Subscription.Builder builder = new Subscription.Builder(podcast)
                    .setDateUpdated(getCurrentUTCDate())
                    .setLocalLogoUrl(podcast.getLogoUrl());
            Subscription newSubscription = builder.build();
            ContentValues values = mConverter.convert(newSubscription);
            Uri rowItemUri = getContext().getContentResolver()
                    .insert(DatabaseContract.Subscription.CONTENT_URI, values);
            if(rowItemUri == null){
                throw new IllegalStateException("Could not get the inserted row's URI.");
            }
            long subscipriptionId = Long.parseLong(rowItemUri.getLastPathSegment());
            newSubscription = new Subscription.Builder(newSubscription).setId(subscipriptionId).build();
            subscriber.onNext(newSubscription);
            subscriber.onCompleted();
        }).subscribeOn(Schedulers.io());
    }

    private Date getCurrentUTCDate(){
        return Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime();
    }

    private Context getContext(){
        return mContext;
    }

    private BriteContentResolver getQueryResolver(){
        return mResolver;
    }
}
