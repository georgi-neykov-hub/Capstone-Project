package com.neykov.podcastportal.model.subscriptions;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

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

    public Observable<Subscription> subscribeForPodcast(Podcast podcast){
        return Observable.create(subscriber -> {
            //Insert the values.
            Subscription newSubscription = new Subscription.Builder(podcast)
                    .setDateUpdated(getCurrentUTCDate())
                    .setLocalLogoUrl(podcast.getLogoUrl())
                    .build();
            ContentValues values = mConverter.convert(newSubscription);
            getContext().getContentResolver()
                    .insert(DatabaseContract.Subscription.CONTENT_URI, values);
            subscriber.onNext(newSubscription);
            subscriber.onCompleted();
        });
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
