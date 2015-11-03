package com.neykov.podcastportal.model.entity.converter;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.database.Cursor;

import com.neykov.podcastportal.model.entity.Episode;
import com.neykov.podcastportal.model.persistence.DatabaseContract;

import org.joda.time.format.PeriodFormat;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.Date;

public class EpisodesConverter implements Converter<Episode>, TransactionConverter<Episode> {

    @Override
    public ContentValues convert(Episode entity) {
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Episode.EPISODE_ID, entity.getId() == 0? null: entity.getId());
        values.put(DatabaseContract.Episode.PODCAST_ID, entity.getPodcastId());
        values.put(DatabaseContract.Episode.TITLE, entity.getTitle());
        values.put(DatabaseContract.Episode.DESCRIPTION, entity.getDescription());
        values.put(DatabaseContract.Episode.CONTENT_URL, entity.getContentUrl());
        values.put(DatabaseContract.Episode.MIME_TYPE, entity.getMimeType());
        values.put(DatabaseContract.Episode.FILE_URL, entity.getFileUrl());
        values.put(DatabaseContract.Episode.FILE_SIZE, entity.getFileSize());
        values.put(DatabaseContract.Episode.DOWNLOAD_STATE, entity.getDownloadState());
        values.put(DatabaseContract.Episode.DURATION, entity.getDuration());
        values.put(DatabaseContract.Episode.THUMBNAIL, entity.getThumbnail());
        values.put(DatabaseContract.Episode.WATCHED, entity.isWatched() ? 1 : 0);
        values.put(DatabaseContract.Episode.PLAYLIST_ENTRY_ID, entity.getPlaylistEntryId());
        values.put(DatabaseContract.Episode.RELEASE_DATE, entity.getReleased().getTime());
        return values;
    }

    @Override
    public Episode convert(Cursor valueCursor) {
        int playlistIdIndex = valueCursor.getColumnIndex(DatabaseContract.Episode.PLAYLIST_ENTRY_ID);
        int durationIndex = valueCursor.getColumnIndex(DatabaseContract.Episode.DURATION);
        Long playlistEntryId = valueCursor.isNull(playlistIdIndex) ? null : valueCursor.getLong(playlistIdIndex);
        Long duration = valueCursor.isNull(durationIndex) ? null : valueCursor.getLong(durationIndex);
        return new Episode(
                valueCursor.getLong(valueCursor.getColumnIndex(DatabaseContract.Episode.EPISODE_ID)),
                valueCursor.getLong(valueCursor.getColumnIndex(DatabaseContract.Episode.PODCAST_ID)),
                valueCursor.getString(valueCursor.getColumnIndex(DatabaseContract.Episode.TITLE)),
                valueCursor.getString(valueCursor.getColumnIndex(DatabaseContract.Episode.DESCRIPTION)),
                valueCursor.getString(valueCursor.getColumnIndex(DatabaseContract.Episode.CONTENT_URL)),
                valueCursor.getString(valueCursor.getColumnIndex(DatabaseContract.Episode.MIME_TYPE)),
                valueCursor.getString(valueCursor.getColumnIndex(DatabaseContract.Episode.FILE_URL)),
                valueCursor.getLong(valueCursor.getColumnIndex(DatabaseContract.Episode.FILE_SIZE)),
                valueCursor.getInt(valueCursor.getColumnIndex(DatabaseContract.Episode.DOWNLOAD_STATE)),
                duration,
                valueCursor.getString(valueCursor.getColumnIndex(DatabaseContract.Episode.THUMBNAIL)),
                valueCursor.getInt(valueCursor.getColumnIndex(DatabaseContract.Episode.WATCHED))> 0,
                playlistEntryId,
                new Date(valueCursor.getLong(valueCursor.getColumnIndex(DatabaseContract.Episode.RELEASE_DATE))));
    }

    @Override
    public ContentProviderOperation convertToInsertOperation(Episode value) {
        return ContentProviderOperation.newInsert(DatabaseContract.Episode.CONTENT_URI)
                .withValues(convert(value)).build();
    }

    @Override
    public ContentProviderOperation convertToDeleteOperation(Episode value) {
        return ContentProviderOperation.newDelete(DatabaseContract.Episode.buildItemUri(value.getId()))
                .withExpectedCount(1)
                .build();
    }

    @Override
    public ContentProviderOperation convertToUpdateOperation(Episode value) {
        return ContentProviderOperation.newUpdate(DatabaseContract.Episode.buildItemUri(value.getId()))
                .withExpectedCount(1)
                .build();
    }
}
