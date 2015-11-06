package com.neykov.podcastportal.model.playlist;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Pair;

import com.neykov.podcastportal.model.BaseManager;
import com.neykov.podcastportal.model.entity.Episode;
import com.neykov.podcastportal.model.entity.PlaylistEntry;
import com.neykov.podcastportal.model.entity.converter.PlaylistConverter;
import com.neykov.podcastportal.model.persistence.DatabaseContract;
import com.neykov.podcastportal.model.utils.Global;
import com.squareup.sqlbrite.BriteContentResolver;
import com.squareup.sqlbrite.SqlBrite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import rx.Observable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subjects.Subject;

public class PlaylistManager extends BaseManager {

    private PlaylistConverter mPlaylistConverter;
    private BehaviorSubject<Pair<Long, Map<Long, PlaylistEntry>>> mDataSubject;

    @Inject
    public PlaylistManager(@Global Context mApplicationContext, BriteContentResolver resolver) {
        super(mApplicationContext, resolver);
        mPlaylistConverter = new PlaylistConverter();
        mDataSubject = BehaviorSubject.create();
        subscribeForPlaylistChanges();
    }

    private void subscribeForPlaylistChanges() {
        Observable<Pair<Long, Map<Long, PlaylistEntry>>> mDataObservable = getBriteResolver().createQuery(
                DatabaseContract.PlaylistEntry.CONTENT_URI,
                null, null, null, null, true)
                .map(this::buildData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        mDataObservable.subscribe(mDataSubject);
    }

    public
    @Nullable
    PlaylistEntry getItem(long itemId) {
        return mDataSubject.map(longMapPair -> longMapPair.second.get(itemId))
                .toBlocking()
                .first();
    }

    public
    @Nullable
    PlaylistEntry getFirstItem() {
        return mDataSubject.map(longMapPair -> {
            PlaylistEntry firstEntry = null;
            Long firstItemId = longMapPair.first;
            if (firstItemId != null) {
                firstEntry = longMapPair.second.get(firstItemId);
            }
            return firstEntry;
        }).toBlocking()
                .first();
    }


    public Observable<List<PlaylistEntry>> getPlaylistStream() {
        return mDataSubject.map(longMapPair -> buildSortedList(longMapPair.first, longMapPair.second))
                .subscribeOn(Schedulers.computation());
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

    public Single<Void> remove(PlaylistEntry entry) {
        return remove(entry.getEpisode());
    }

    public Single<Void> remove(Episode episode) {
        return Single.create(singleSubscriber -> {
            ArrayList<ContentProviderOperation> ops = new ArrayList<>(1);
            ops.add(ContentProviderOperation.newDelete(DatabaseContract.PlaylistEntry.buildItemUri(episode.getPlaylistEntryId()))
                    .build());
            ops.add(ContentProviderOperation.newUpdate(DatabaseContract.Episode.buildItemUri(episode.getId()))
            .withExpectedCount(1)
            .withValue(DatabaseContract.Episode.PODCAST_ID, episode.getPodcastId())
            .withValue(DatabaseContract.Episode.PLAYLIST_ENTRY_ID, null)
            .build());
            try {
                getApplicationContext().getContentResolver()
                        .applyBatch(DatabaseContract.CONTENT_AUTHORITY, ops);
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

    private List<PlaylistEntry> buildSortedList(final long firstItemId, Map<Long, PlaylistEntry> idToItemMap) {
        if (idToItemMap.isEmpty()) {
            return new ArrayList<>(0);
        }

        PlaylistEntry entryValue;
        final int itemCоunt = idToItemMap.size();
        List<PlaylistEntry> sortedEntries = new ArrayList<>(itemCоunt);
        int itemsProcessed = 0;
        Long currentItemId = firstItemId;
        while (itemsProcessed < itemCоunt) {
            entryValue = idToItemMap.get(currentItemId);
            currentItemId = entryValue.getNextItemId();
            sortedEntries.add(entryValue);
            itemsProcessed++;
        }

        return sortedEntries;
    }

    private Pair<Long, Map<Long, PlaylistEntry>> buildData(SqlBrite.Query playlistEntriesQuery) {
        Map<Long, PlaylistEntry> idToEntryMap = new HashMap<>();
        Cursor results = playlistEntriesQuery.run();
        Long fisrtItemId = null;
        try {
            PlaylistEntry entry;
            while (results.moveToNext()) {
                entry = mPlaylistConverter.convert(results);
                if (entry.getPreviousItemId() == null) {
                    fisrtItemId = entry.getId();
                }
                idToEntryMap.put(entry.getId(), entry);
            }
        } finally {
            results.close();
        }

        if (fisrtItemId == null && idToEntryMap.size() > 0) {
            throw new AssertionError("Entry map is not empty, but the ID of the first item is null.");
        }

        return new Pair<>(fisrtItemId, idToEntryMap);
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
