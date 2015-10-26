package com.neykov.podcastportal.model.entity.converter;

import android.content.ContentValues;
import android.database.Cursor;

import com.neykov.podcastportal.model.entity.PodcastSubscription;
import com.neykov.podcastportal.model.persistence.DatabaseContract;

import java.util.Date;

public class SubscriptionConverter implements Converter<PodcastSubscription> {
    @Override
    public PodcastSubscription convert(Cursor values) {
        long timeUpdatedUtc = values.getLong(values.getColumnIndex(DatabaseContract.Podcast.DATE_UPDATED));
        return new PodcastSubscription(
                values.getLong(values.getColumnIndex(DatabaseContract.Podcast.PODCAST_ID)),
                values.getString(values.getColumnIndex(DatabaseContract.Podcast.TITLE)),
                values.getString(values.getColumnIndex(DatabaseContract.Podcast.DESCRIPTION)),
                values.getString(values.getColumnIndex(DatabaseContract.Podcast.FEED_URL)),
                values.getString(values.getColumnIndex(DatabaseContract.Podcast.WEBSITE)),
                values.getInt(values.getColumnIndex(DatabaseContract.Podcast.SUBSCRIBERS)),
                values.getString(values.getColumnIndex(DatabaseContract.Podcast.LOGO_URL)),
                values.getString(values.getColumnIndex(DatabaseContract.Podcast.LOCAL_LOGO_URL)),
                new Date(timeUpdatedUtc));
    }

    @Override
    public ContentValues convert(PodcastSubscription entity) {
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Podcast.TITLE, entity.getTitle());
        values.put(DatabaseContract.Podcast.DESCRIPTION, entity.getDescription());
        values.put(DatabaseContract.Podcast.FEED_URL, entity.getUrl());
        values.put(DatabaseContract.Podcast.WEBSITE, entity.getWebsite());
        values.put(DatabaseContract.Podcast.SUBSCRIBERS, entity.getSubscribers());
        values.put(DatabaseContract.Podcast.LOGO_URL, entity.getLogoUrl());
        values.put(DatabaseContract.Podcast.LOCAL_LOGO_URL, entity.getLocalLogoUrl());
        values.put(DatabaseContract.Podcast.DATE_UPDATED, entity.getDateUpdatedUtc().getTime());
        return values;
    }
}
