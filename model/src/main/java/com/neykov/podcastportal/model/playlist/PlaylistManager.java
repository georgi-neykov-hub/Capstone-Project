package com.neykov.podcastportal.model.playlist;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;

import com.neykov.podcastportal.model.BaseManager;
import com.neykov.podcastportal.model.entity.Episode;
import com.neykov.podcastportal.model.entity.PlaylistEntry;
import com.neykov.podcastportal.model.entity.converter.PlaylistConverter;
import com.neykov.podcastportal.model.persistence.DatabaseContract;
import com.squareup.sqlbrite.BriteContentResolver;
import com.squareup.sqlbrite.SqlBrite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import rx.Observable;
import rx.Single;

public class PlaylistManager extends BaseManager {

    private PlaylistConverter mPlaylistConverter;

    @Inject
    public PlaylistManager(Context mApplicationContext, BriteContentResolver resolver) {
        super(mApplicationContext, resolver);
        mPlaylistConverter = new PlaylistConverter();
    }

    public Observable<List<PlaylistEntry>> getPlaylistStream() {
        return getBriteResolver().createQuery(
                DatabaseContract.PlaylistEntry.CONTENT_URI,
                null, null, null, null, true)
                .map(this::convertToSortedList);
    }

    public Single<PlaylistEntry> addToTop(Episode episode) {
        return Single.create(subscriber -> {
            PlaylistEntry currentTopEntry = getItemAfter(null).toBlocking().singleOrDefault(null);
            ContentValues itemValues = new ContentValues();
            itemValues.put(DatabaseContract.PlaylistEntry.EPISODE_ID, episode.getId());
            if (currentTopEntry != null) {
                itemValues.put(DatabaseContract.PlaylistEntry.NEXT_ITEM_ID, currentTopEntry.getId());
            }
            ArrayList<ContentProviderOperation> ops = new ArrayList<>();
            ContentProviderOperation addOp = ContentProviderOperation
                    .newInsert(DatabaseContract.PlaylistEntry.CONTENT_URI)
                    .withValues(itemValues)
                    .build();
            ops.add(addOp);

            if (currentTopEntry != null) {
                ContentProviderOperation updateOp = ContentProviderOperation
                        .newUpdate(DatabaseContract.PlaylistEntry.buildItemUri(currentTopEntry.getId()))
                        .withValueBackReference(DatabaseContract.PlaylistEntry.PREVIOUS_ITEM_ID, 0)
                        .withExpectedCount(1)
                        .build();
                ops.add(updateOp);
            }

            ops.add(ContentProviderOperation.newUpdate(DatabaseContract.Episode.buildItemUri(episode.getId()))
                            .withValue(DatabaseContract.Episode.PODCAST_ID, episode.getPodcastId())
                            .withValueBackReference(DatabaseContract.Episode.PLAYLIST_ENTRY_ID, 0)
                            .withExpectedCount(1)
                            .build()
            );

            try {
                ContentProviderResult[] results = getApplicationContext().getContentResolver()
                        .applyBatch(DatabaseContract.CONTENT_AUTHORITY, ops);
                long newItemId = ContentUris.parseId(results[0].uri);
                PlaylistEntry result = new PlaylistEntry(newItemId,
                        null,
                        currentTopEntry == null ? null : currentTopEntry.getId(),
                        episode, null);
                subscriber.onSuccess(result);
            } catch (RemoteException | OperationApplicationException e) {
                subscriber.onError(e);
            }
        });
    }

    public Single<PlaylistEntry> addToEnd(Episode episode) {
        return Single.create(subscriber -> {
            PlaylistEntry currentBottomEntry = getItemBefore(null).toBlocking().singleOrDefault(null);
            ContentValues itemValues = new ContentValues();
            itemValues.put(DatabaseContract.PlaylistEntry.EPISODE_ID, episode.getId());
            if (currentBottomEntry != null) {
                itemValues.put(DatabaseContract.PlaylistEntry.PREVIOUS_ITEM_ID, currentBottomEntry.getId());
            }
            ArrayList<ContentProviderOperation> ops = new ArrayList<>();
            ContentProviderOperation addOp = ContentProviderOperation
                    .newInsert(DatabaseContract.PlaylistEntry.CONTENT_URI)
                    .withValues(itemValues)
                    .build();
            ops.add(addOp);


            if (currentBottomEntry != null) {
                ContentProviderOperation updateOp = ContentProviderOperation
                        .newUpdate(DatabaseContract.PlaylistEntry.buildItemUri(currentBottomEntry.getId()))
                        .withValues(mPlaylistConverter.convert(currentBottomEntry))
                        .withValueBackReference(DatabaseContract.PlaylistEntry.NEXT_ITEM_ID, 0)
                        .withExpectedCount(1)
                        .build();
                ops.add(updateOp);
            }

            ops.add(ContentProviderOperation.newUpdate(DatabaseContract.Episode.buildItemUri(episode.getId()))
                            .withValue(DatabaseContract.Episode.PODCAST_ID, episode.getPodcastId())
                            .withValueBackReference(DatabaseContract.Episode.PLAYLIST_ENTRY_ID, 0)
                            .withExpectedCount(1)
                            .build()
            );

            try {
                ContentProviderResult[] results = getApplicationContext().getContentResolver()
                        .applyBatch(DatabaseContract.CONTENT_AUTHORITY, ops);
                long newItemId = ContentUris.parseId(results[0].uri);
                PlaylistEntry result = new PlaylistEntry(newItemId,
                        currentBottomEntry == null ? null : currentBottomEntry.getId(),
                        null,
                        episode, null);
                subscriber.onSuccess(result);
            } catch (RemoteException | OperationApplicationException e) {
                subscriber.onError(e);
            }
        });
    }

    public Single<PlaylistEntry> getFirstItem() {
        return getItemAfter(null).toSingle();
    }

    public Single<PlaylistEntry> getLastItem() {
        return getItemBefore(null).toSingle();
    }

    public Single<Void> remove(PlaylistEntry entry) {
        return remove(entry.getEpisode());
    }

    public Single<Void> remove(Episode episode) {
        return Single.create(singleSubscriber -> {
            ArrayList<ContentProviderOperation> ops = new ArrayList<>(1);
            ops.add(ContentProviderOperation.newDelete(DatabaseContract.PlaylistEntry.buildItemUri(episode.getPlaylistEntryId()))
                    .withExpectedCount(1)
                    .build());
            try {
                getApplicationContext().getContentResolver()
                        .applyBatch(DatabaseContract.CONTENT_AUTHORITY, ops);
                getApplicationContext().getContentResolver().notifyChange(DatabaseContract.Episode.buildItemUri(episode.getId()), null);
                getApplicationContext().getContentResolver().notifyChange(DatabaseContract.Episode.buildSubscriptionEpisodesUri(episode.getPodcastId()), null);
                singleSubscriber.onSuccess(null);
            } catch (RemoteException | OperationApplicationException e) {
                singleSubscriber.onError(e);
            }
        });
    }

    public Single<PlaylistEntry> moveBefore(PlaylistEntry target, PlaylistEntry anchorItem) {
        return Single.create(singleSubscriber -> {
            ArrayList<ContentProviderOperation> ops = new ArrayList<>();

            // Detach item from current position.
            if (target.getPreviousItemId() != null) {
                ContentProviderOperation detachOp1 = ContentProviderOperation
                        .newUpdate(DatabaseContract.PlaylistEntry.buildItemUri(target.getPreviousItemId()))
                        .withSelection(DatabaseContract.PlaylistEntry.NEXT_ITEM_ID + "=?",
                                new String[]{String.valueOf(target.getId())})
                        .withValue(DatabaseContract.PlaylistEntry.NEXT_ITEM_ID, target.getNextItemId())
                        .withExpectedCount(1)
                        .build();
                ops.add(detachOp1);
            }

            if (target.getNextItemId() != null) {
                ContentProviderOperation detachOp2 = ContentProviderOperation
                        .newUpdate(DatabaseContract.PlaylistEntry.buildItemUri(target.getNextItemId()))
                        .withValue(DatabaseContract.PlaylistEntry.PREVIOUS_ITEM_ID, target.getPreviousItemId())
                        .withSelection(DatabaseContract.PlaylistEntry.PREVIOUS_ITEM_ID + "=?",
                                new String[]{String.valueOf(target.getId())})
                        .withExpectedCount(1)
                        .build();
                ops.add(detachOp2);
            }

            // Update the item before the anchor, if any.
            if (anchorItem.getPreviousItemId() != null) {
                ContentProviderOperation attachOp1 = ContentProviderOperation
                        .newUpdate(DatabaseContract.PlaylistEntry.buildItemUri(anchorItem.getPreviousItemId()))
                        .withSelection(DatabaseContract.PlaylistEntry.NEXT_ITEM_ID + "=?",
                                new String[]{String.valueOf(anchorItem.getId())})
                        .withValue(DatabaseContract.PlaylistEntry.NEXT_ITEM_ID, target.getId())
                        .withExpectedCount(1)
                        .build();
                ops.add(attachOp1);
            }
            //Update the anchor item itself.
            ContentProviderOperation op = ContentProviderOperation
                    .newUpdate(DatabaseContract.PlaylistEntry.buildItemUri(anchorItem.getId()))
                    .withValue(DatabaseContract.PlaylistEntry.PREVIOUS_ITEM_ID, target.getId())
                    .withExpectedCount(1)
                    .build();
            ops.add(op);

            // Update the target itself.
            ContentProviderOperation targetUpdateOp = ContentProviderOperation
                    .newUpdate(DatabaseContract.PlaylistEntry.buildItemUri(target.getId()))
                    .withValue(DatabaseContract.PlaylistEntry.PREVIOUS_ITEM_ID, anchorItem.getPreviousItemId())
                    .withValue(DatabaseContract.PlaylistEntry.NEXT_ITEM_ID, anchorItem.getId())
                    .withExpectedCount(1)
                    .build();
            ops.add(targetUpdateOp);

            try {
                getApplicationContext().getContentResolver().applyBatch(DatabaseContract.CONTENT_AUTHORITY, ops);
                PlaylistEntry result = new PlaylistEntry(target.getId(),
                        anchorItem.getPreviousItemId(),
                        anchorItem.getId(),
                        target.getEpisode(), null);
                singleSubscriber.onSuccess(result);
            } catch (RemoteException | OperationApplicationException e) {
                singleSubscriber.onError(e);
            }
        });
    }

    public Single<PlaylistEntry> moveAfter(PlaylistEntry target, PlaylistEntry anchorItem) {
        return Single.create(singleSubscriber -> {
            ArrayList<ContentProviderOperation> ops = new ArrayList<>();

            // Detach item from current position.
            if (target.getPreviousItemId() != null) {
                ContentProviderOperation detachOp1 = ContentProviderOperation
                        .newUpdate(DatabaseContract.PlaylistEntry.buildItemUri(target.getPreviousItemId()))
                        .withSelection(DatabaseContract.PlaylistEntry.NEXT_ITEM_ID + "=?",
                                new String[]{String.valueOf(target.getId())})
                        .withValue(DatabaseContract.PlaylistEntry.NEXT_ITEM_ID, target.getNextItemId())
                        .withExpectedCount(1)
                        .build();
                ops.add(detachOp1);
            }

            if (target.getNextItemId() != null) {
                ContentProviderOperation detachOp2 = ContentProviderOperation
                        .newUpdate(DatabaseContract.PlaylistEntry.buildItemUri(target.getNextItemId()))
                        .withValue(DatabaseContract.PlaylistEntry.PREVIOUS_ITEM_ID, target.getPreviousItemId())
                        .withSelection(DatabaseContract.PlaylistEntry.PREVIOUS_ITEM_ID + "=?",
                                new String[]{String.valueOf(target.getId())})
                        .withExpectedCount(1)
                        .build();
                ops.add(detachOp2);
            }

            // Update the item after the anchor, if any.
            if (anchorItem.getNextItemId() != null) {
                ContentProviderOperation attachOp1 = ContentProviderOperation
                        .newUpdate(DatabaseContract.PlaylistEntry.buildItemUri(anchorItem.getNextItemId()))
                        .withSelection(DatabaseContract.PlaylistEntry.PREVIOUS_ITEM_ID + "=?",
                                new String[]{String.valueOf(anchorItem.getId())})
                        .withValue(DatabaseContract.PlaylistEntry.PREVIOUS_ITEM_ID, target.getId())
                        .withExpectedCount(1)
                        .build();
                ops.add(attachOp1);
            }
            //Update the anchor item itself.
            ContentProviderOperation op = ContentProviderOperation
                    .newUpdate(DatabaseContract.PlaylistEntry.buildItemUri(anchorItem.getId()))
                    .withValue(DatabaseContract.PlaylistEntry.NEXT_ITEM_ID, target.getId())
                    .withExpectedCount(1)
                    .build();
            ops.add(op);

            // Update the target itself.
            ContentProviderOperation targetUpdateOp = ContentProviderOperation
                    .newUpdate(DatabaseContract.PlaylistEntry.buildItemUri(target.getId()))
                    .withValue(DatabaseContract.PlaylistEntry.PREVIOUS_ITEM_ID, anchorItem.getId())
                    .withValue(DatabaseContract.PlaylistEntry.NEXT_ITEM_ID, anchorItem.getNextItemId())
                    .withExpectedCount(1)
                    .build();
            ops.add(targetUpdateOp);

            try {
                getApplicationContext().getContentResolver().applyBatch(DatabaseContract.CONTENT_AUTHORITY, ops);
                PlaylistEntry result = new PlaylistEntry(target.getId(),
                        anchorItem.getId(),
                        anchorItem.getNextItemId(),
                        target.getEpisode(), null);
                singleSubscriber.onSuccess(result);
            } catch (RemoteException | OperationApplicationException e) {
                singleSubscriber.onError(e);
            }
        });
    }

    public Single<Void> clear() {
        return Single.create(singleSubscriber -> {
            try {
                getApplicationContext().getContentResolver().delete(DatabaseContract.PlaylistEntry.CONTENT_URI, null, null);
                singleSubscriber.onSuccess(null);
            } catch (Exception e) {
                singleSubscriber.onError(e);
            }
        });
    }

    private List<PlaylistEntry> convertToSortedList(SqlBrite.Query playlistEntriesQuery) {
        Map<Long, PlaylistEntry> prevItemIdToEntryMap = new HashMap<>();
        Cursor results = playlistEntriesQuery.run();
        Long currentPrevId = null;
        final int itemCоunt = results.getCount();
        List<PlaylistEntry> sortedEntries = new ArrayList<>(itemCоunt);
        try {
            PlaylistEntry entry;
            while (results.moveToNext()) {
                entry = mPlaylistConverter.convert(results);
                if (entry.getPreviousItemId() == null){
                    currentPrevId = entry.getId();
                    sortedEntries.add(entry);
                } else {
                    prevItemIdToEntryMap.put(entry.getPreviousItemId(), entry);
                }
            }
        } finally {
            results.close();
        }


        PlaylistEntry entryValue;
        int itemsProcessed = sortedEntries.size();

        while (itemsProcessed < itemCоunt) {
            entryValue = prevItemIdToEntryMap.get(currentPrevId);
            currentPrevId = entryValue.getId();
            sortedEntries.add(entryValue);
            itemsProcessed++;
        }

        return sortedEntries;
    }

    private Observable<PlaylistEntry> getItemBefore(Long itemId) {
        String whereClause = itemId == null ?
                DatabaseContract.PlaylistEntry.NEXT_ITEM_ID + " is null" :
                DatabaseContract.PlaylistEntry.NEXT_ITEM_ID + "=?";
        String[] whereParameters = itemId == null ? null :
                new String[]{itemId.toString()};
        return getBriteResolver()
                .createQuery(
                        DatabaseContract.PlaylistEntry.CONTENT_URI,
                        null,
                        whereClause,
                        whereParameters,
                        null,
                        false)
                .mapToOneOrNull(mPlaylistConverter::convert)
                .first();
    }

    private Observable<PlaylistEntry> getItemAfter(Long itemId) {
        String whereClause = itemId == null ?
                DatabaseContract.PlaylistEntry.PREVIOUS_ITEM_ID + " is null" :
                DatabaseContract.PlaylistEntry.PREVIOUS_ITEM_ID + "=?";
        String[] whereParameters = itemId == null ? null :
                new String[]{itemId.toString()};
        return getBriteResolver()
                .createQuery(
                        DatabaseContract.PlaylistEntry.CONTENT_URI,
                        null,
                        whereClause,
                        whereParameters,
                        null,
                        false)
                .mapToOneOrNull(mPlaylistConverter::convert)
                .first();
    }
}
