package com.neykov.podcastportal.model.entity.converter;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.database.Cursor;

import com.neykov.podcastportal.model.entity.Episode;
import com.neykov.podcastportal.model.entity.PlaylistEntry;
import com.neykov.podcastportal.model.persistence.DatabaseContract;

public class PlaylistConverter implements Converter<PlaylistEntry>, TransactionConverter<PlaylistEntry> {

    private EpisodesConverter mEpisodesConverter = new EpisodesConverter();

    public ContentValues convert(long id, Long previousId, long episodeId) {
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.PlaylistEntry.PLAYLIST_ENTRY_ID, id == 0 ? null : id);
        values.put(DatabaseContract.PlaylistEntry.PREVIOUS_ITEM_ID, previousId);
        values.put(DatabaseContract.PlaylistEntry.EPISODE_ID, episodeId);
        return values;
    }

    @Override
    public ContentValues convert(PlaylistEntry entity) {
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.PlaylistEntry.PLAYLIST_ENTRY_ID, entity.getId() == 0 ? null : entity.getId());
        values.put(DatabaseContract.PlaylistEntry.PREVIOUS_ITEM_ID, entity.getPreviousItemId());
        values.put(DatabaseContract.PlaylistEntry.EPISODE_ID, entity.getEpisode().getId());
        return values;
    }

    @Override
    public PlaylistEntry convert(Cursor valueCursor) {
        Episode episode = mEpisodesConverter.convert(valueCursor);
        int prevColumnIndex = valueCursor.getColumnIndex(DatabaseContract.PlaylistEntry.PREVIOUS_ITEM_ID);
        int nextItemIndex = valueCursor.getColumnIndex(DatabaseContract.PlaylistEntry.NEXT_ITEM_ID);
        return new PlaylistEntry(
                valueCursor.getLong(valueCursor.getColumnIndex(DatabaseContract.PlaylistEntry.PLAYLIST_ENTRY_ID)),
                valueCursor.isNull(prevColumnIndex) ? null : valueCursor.getLong(prevColumnIndex),
                valueCursor.isNull(nextItemIndex) ? null : valueCursor.getLong(nextItemIndex),
                episode,
                valueCursor.getString(valueCursor.getColumnIndex(DatabaseContract.Podcast.TITLE)));
    }

    @Override
    public ContentProviderOperation convertToInsertOperation(PlaylistEntry value) {
        return ContentProviderOperation.newInsert(DatabaseContract.PlaylistEntry.CONTENT_URI)
                .withValues(convert(value)).build();
    }

    @Override
    public ContentProviderOperation convertToDeleteOperation(PlaylistEntry value) {
        return ContentProviderOperation.newDelete(DatabaseContract.PlaylistEntry.buildItemUri(value.getId()))
                .withValues(convert(value)).build();
    }

    @Override
    public ContentProviderOperation convertToUpdateOperation(PlaylistEntry value) {
        return ContentProviderOperation.newUpdate(DatabaseContract.PlaylistEntry.buildItemUri(value.getId()))
                .withValues(convert(value)).build();
    }
}
