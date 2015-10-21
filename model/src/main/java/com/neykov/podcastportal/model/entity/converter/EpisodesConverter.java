package com.neykov.podcastportal.model.entity.converter;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.database.Cursor;

import com.neykov.podcastportal.model.entity.Episode;
import com.neykov.podcastportal.model.persistence.DatabaseContract;

import java.util.Date;

public class EpisodesConverter implements Converter<Episode>, TransactionConverter<Episode> {

    @Override
    public ContentValues convert(Episode entity) {
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Episode.PODCAST_ID, entity.getPodcastId());
        values.put(DatabaseContract.Episode.TITLE, entity.getTitle());
        values.put(DatabaseContract.Episode.CONTENT_URL, entity.getUrl());
        values.put(DatabaseContract.Episode.MIME_TYPE, entity.getMimeType());
        values.put(DatabaseContract.Episode.DESCRIPTION, entity.getDescription());
        values.put(DatabaseContract.Episode.RELEASE_DATE, entity.getReleased().getTime());
        return values;
    }

    @Override
    public Episode convert(Cursor valueCursor) {
        return new Episode(
                valueCursor.getLong(valueCursor.getColumnIndex(DatabaseContract.Episode._ID)),
                valueCursor.getLong(valueCursor.getColumnIndex(DatabaseContract.Episode.PODCAST_ID)),
                valueCursor.getString(valueCursor.getColumnIndex(DatabaseContract.Episode.TITLE)),
                valueCursor.getString(valueCursor.getColumnIndex(DatabaseContract.Episode.CONTENT_URL)),
                valueCursor.getString(valueCursor.getColumnIndex(DatabaseContract.Episode.MIME_TYPE)),
                valueCursor.getString(valueCursor.getColumnIndex(DatabaseContract.Episode.DESCRIPTION)),
                new Date(valueCursor.getLong(valueCursor.getColumnIndex(DatabaseContract.Episode.RELEASE_DATE))));
    }

    @Override
    public ContentProviderOperation convertToInsertOperation(Episode value) {
        return ContentProviderOperation.newInsert(DatabaseContract.Episode.CONTENT_URI)
                .withValues(convert(value)).build();
    }

    @Override
    public ContentProviderOperation convertToDeleteOperation(Episode value) {
        return null;
    }

    @Override
    public ContentProviderOperation convertToUpdateOperation(Episode value) {
        return null;
    }
}
