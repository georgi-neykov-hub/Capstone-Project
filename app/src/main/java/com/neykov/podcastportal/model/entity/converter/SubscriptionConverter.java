package com.neykov.podcastportal.model.entity.converter;

import android.content.ContentValues;
import android.database.Cursor;

import com.neykov.podcastportal.model.persistence.DatabaseContract;

import java.util.Date;

public class SubscriptionConverter implements Converter<com.neykov.podcastportal.model.entity.Subscription> {
    @Override
    public com.neykov.podcastportal.model.entity.Subscription convert(Cursor values) {
        long timeUpdatedUtc = values.getLong(values.getColumnIndex(DatabaseContract.Subscription.DATE_UPDATED));
        return new com.neykov.podcastportal.model.entity.Subscription(
                values.getString(values.getColumnIndex(DatabaseContract.Subscription.TITLE)),
                values.getString(values.getColumnIndex(DatabaseContract.Subscription.DESCRIPTION)),
                values.getString(values.getColumnIndex(DatabaseContract.Subscription.FEED_URL)),
                values.getString(values.getColumnIndex(DatabaseContract.Subscription.WEBSITE)),
                values.getInt(values.getColumnIndex(DatabaseContract.Subscription.SUBSCRIBERS)),
                values.getString(values.getColumnIndex(DatabaseContract.Subscription.LOGO_URL)),
                values.getString(values.getColumnIndex(DatabaseContract.Subscription.LOCAL_LOGO_URL)),
                new Date(timeUpdatedUtc));
    }

    @Override
    public ContentValues convert(com.neykov.podcastportal.model.entity.Subscription entity) {
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Subscription.TITLE, entity.getTitle());
        values.put(DatabaseContract.Subscription.DESCRIPTION, entity.getDescription());
        values.put(DatabaseContract.Subscription.FEED_URL, entity.getUrl());
        values.put(DatabaseContract.Subscription.WEBSITE, entity.getWebsite());
        values.put(DatabaseContract.Subscription.SUBSCRIBERS, entity.getSubscribers());
        values.put(DatabaseContract.Subscription.LOGO_URL, entity.getLogoUrl());
        values.put(DatabaseContract.Subscription.LOCAL_LOGO_URL, entity.getLocalLogoUrl());
        values.put(DatabaseContract.Subscription.DATE_UPDATED, entity.getDateUpdated().getTime());
        return values;
    }
}
