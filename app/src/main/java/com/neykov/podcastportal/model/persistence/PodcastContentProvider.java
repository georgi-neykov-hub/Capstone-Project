package com.neykov.podcastportal.model.persistence;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class PodcastContentProvider extends ContentProvider {

    private static final String PLAYLIST_QUERY_SELECT_STATEMENT = "SELECT " + DatabaseContract.PlaylistEntry.TABLE_NAME +
            ".*, " + DatabaseContract.Episode.TABLE_NAME +
            ".*, " + DatabaseContract.Podcast.TABLE_NAME + "." + DatabaseContract.Podcast.TITLE +
            " FROM " + DatabaseContract.PlaylistEntry.TABLE_NAME +
            " JOIN " + DatabaseContract.Episode.TABLE_NAME + " ON " +
            DatabaseContract.PlaylistEntry.TABLE_NAME + "." + DatabaseContract.PlaylistEntry.EPISODE_ID + "=" +
            DatabaseContract.Episode.TABLE_NAME + "." + DatabaseContract.Episode.EPISODE_ID +
            " JOIN " + DatabaseContract.Podcast.TABLE_NAME + " ON " +
            DatabaseContract.Podcast.TABLE_NAME+ "." + DatabaseContract.Podcast.PODCAST_ID + "=" +
            DatabaseContract.Episode.TABLE_NAME + "." + DatabaseContract.Episode.PODCAST_ID;

    private DatabaseOpenHelper mDatabaseOpenHelper;
    private UriMatcher mURIMatcher;

    public PodcastContentProvider() {
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int match = mURIMatcher.match(uri);
        String tableName;
        switch (match) {
            case SUBSCRIPTIONS:
            case SUBSCRIPTION_BY_ID:
                tableName = DatabaseContract.Podcast.TABLE_NAME;
                break;
            case EPISODE_BY_ID:
            case EPISODES:
                tableName = DatabaseContract.Episode.TABLE_NAME;
                break;
            case PLAYLIST_BY_ID:
            case PLAYLIST:
                tableName = DatabaseContract.PlaylistEntry.TABLE_NAME;
                break;
            default:
                throw new UnsupportedOperationException("Cannot handle URI.");
        }

        String whereStatement;
        switch (match) {
            case SUBSCRIPTION_BY_ID:
                whereStatement = DatabaseContract.Podcast.PODCAST_ID + "=" + ContentUris.parseId(uri);
                if (!TextUtils.isEmpty(selection)) {
                    whereStatement += " AND " + selection;
                }
                break;
            case EPISODE_BY_ID:
                whereStatement = DatabaseContract.Episode.EPISODE_ID + "=" + ContentUris.parseId(uri);
                if (!TextUtils.isEmpty(selection)) {
                    whereStatement += " AND " + selection;
                }
                break;
            case PLAYLIST_BY_ID:
                whereStatement = DatabaseContract.PlaylistEntry.PLAYLIST_ENTRY_ID + "=" + ContentUris.parseId(uri);
                if (!TextUtils.isEmpty(selection)) {
                    whereStatement += " AND " + selection;
                }
                break;
            default:
                whereStatement = selection;
        }

        int deletedRowsCount = mDatabaseOpenHelper.getWritableDatabase()
                .delete(tableName, whereStatement, selectionArgs);
        if (deletedRowsCount > 0) {
            //noinspection ConstantConditions
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deletedRowsCount;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public boolean onCreate() {
        mDatabaseOpenHelper = new DatabaseOpenHelper(getContext(), DatabaseContract.DATABASE_NAME, null, 1);
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        String targetTable;
        String whereStatement = selection;
        String[] whereArguments = selectionArgs;
        switch (getMatcher().match(uri)) {
            case SUBSCRIPTIONS:
                targetTable = DatabaseContract.Podcast.TABLE_NAME;
                break;
            case EPISODES:
                targetTable = DatabaseContract.Episode.TABLE_NAME;
                break;
            case PLAYLIST:
                return queryPlaylistEntries(whereStatement, whereArguments, sortOrder);
            case EPISODES_FOR_SUBSCRIPTION:
                targetTable = DatabaseContract.Episode.TABLE_NAME;
                List<String> segments = uri.getPathSegments();
                String podcastId = segments.get(segments.size() - 2);
                if (!TextUtils.isEmpty(whereStatement)) {
                    whereStatement += " AND " + DatabaseContract.Episode.PODCAST_ID + "=" + podcastId;
                } else {
                    whereStatement = DatabaseContract.Episode.PODCAST_ID + "=" + podcastId;
                }
                break;
            default:
                throw new UnsupportedOperationException("Cannot handle URI.");
        }

        return mDatabaseOpenHelper.getReadableDatabase().query(
                targetTable,
                projection,
                whereStatement,
                whereStatement == null ? null : whereArguments,
                null,
                null,
                sortOrder
        );
    }

    private Cursor queryPlaylistEntries(String whereStatement, String[] whereArguments, String sortOrder) {
        StringBuilder builder = new StringBuilder(PLAYLIST_QUERY_SELECT_STATEMENT);
        if (!TextUtils.isEmpty(whereStatement)) {
            builder.append(" WHERE ").append(whereStatement);
        }

        if (!TextUtils.isEmpty(sortOrder)) {
            builder.append(" ORDER BY ").append(sortOrder);
        }
        String rawQuery = builder.toString();
        return mDatabaseOpenHelper.getReadableDatabase().rawQuery(rawQuery, whereArguments);
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int match = mURIMatcher.match(uri);
        String tableName;
        switch (match) {
            case SUBSCRIPTIONS:
            case SUBSCRIPTION_BY_ID:
                tableName = DatabaseContract.Podcast.TABLE_NAME;
                break;
            case EPISODE_BY_ID:
            case EPISODES:
                tableName = DatabaseContract.Episode.TABLE_NAME;
                break;
            case PLAYLIST_BY_ID:
            case PLAYLIST:
                tableName = DatabaseContract.PlaylistEntry.TABLE_NAME;
                break;
            default:
                throw new UnsupportedOperationException("Cannot handle URI.");
        }

        String whereStatement;
        switch (match) {
            case SUBSCRIPTION_BY_ID:
                whereStatement = DatabaseContract.Podcast.PODCAST_ID + "=" + uri.getLastPathSegment();
                if (!TextUtils.isEmpty(selection)) {
                    whereStatement += " AND " + selection;
                }
                break;
            case EPISODE_BY_ID:
                whereStatement = DatabaseContract.Episode.EPISODE_ID + "=" + uri.getLastPathSegment();
                if (!TextUtils.isEmpty(selection)) {
                    whereStatement += " AND " + selection;
                }
                break;
            case PLAYLIST_BY_ID:
                whereStatement = DatabaseContract.PlaylistEntry.PLAYLIST_ENTRY_ID + "=" + uri.getLastPathSegment();
                if (!TextUtils.isEmpty(selection)) {
                    whereStatement += " AND " + selection;
                }
                break;
            default:
                whereStatement = selection;
        }

        int updatedRowsCount = mDatabaseOpenHelper.getWritableDatabase()
                .update(tableName, values, whereStatement, selectionArgs);
        //noinspection ConstantConditions
        ContentResolver resolver = getContext().getContentResolver();
        if (updatedRowsCount > 0) {
            if (match == EPISODE_BY_ID) {
                long podcastId = values.getAsLong(DatabaseContract.Episode.PODCAST_ID);
                resolver.notifyChange(DatabaseContract.Episode.buildSubscriptionEpisodesUri(podcastId), null);
            }
            resolver.notifyChange(uri, null);
        }
        return updatedRowsCount;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        String tableName;
        Uri baseUri;
        int match = getMatcher().match(uri);
        switch (match) {
            case SUBSCRIPTIONS:
                tableName = DatabaseContract.Podcast.TABLE_NAME;
                baseUri = DatabaseContract.Podcast.CONTENT_URI;
                break;
            case EPISODES:
                tableName = DatabaseContract.Episode.TABLE_NAME;
                baseUri = DatabaseContract.Episode.CONTENT_URI;
                break;
            case PLAYLIST:
                tableName = DatabaseContract.PlaylistEntry.TABLE_NAME;
                baseUri = DatabaseContract.PlaylistEntry.CONTENT_URI;
                break;
            default:
                throw new IllegalArgumentException("Cannot insert, Unknown URI.");
        }
        SQLiteDatabase database = mDatabaseOpenHelper.getWritableDatabase();
        long insertedRowId = database.insert(tableName, null, values);
        Uri newItemUri = ContentUris.withAppendedId(baseUri, insertedRowId);
        //noinspection ConstantConditions
        ContentResolver resolver = getContext().getContentResolver();
        if (match == EPISODES) {
            long podcastId = values.getAsLong(DatabaseContract.Episode.PODCAST_ID);
            resolver.notifyChange(DatabaseContract.Episode.buildSubscriptionEpisodesUri(podcastId), null);
        }
        resolver.notifyChange(baseUri, null);
        return newItemUri;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        SQLiteDatabase database = mDatabaseOpenHelper.getWritableDatabase();
        try {
            database.beginTransaction();
            String tableName;
            Uri baseUri;
            int match = getMatcher().match(uri);
            switch (match) {
                case SUBSCRIPTIONS:
                    tableName = DatabaseContract.Podcast.TABLE_NAME;
                    baseUri = DatabaseContract.Podcast.CONTENT_URI;
                    break;
                case EPISODES:
                    tableName = DatabaseContract.Episode.TABLE_NAME;
                    baseUri = DatabaseContract.Episode.CONTENT_URI;
                    break;
                case PLAYLIST:
                    tableName = DatabaseContract.PlaylistEntry.TABLE_NAME;
                    baseUri = DatabaseContract.PlaylistEntry.CONTENT_URI;
                    break;
                default:
                    throw new IllegalArgumentException("Cannot insert, Unknown URI.");
            }

            long insertedRowId;
            int insertedRowCount = 0;
            final int valuesCount = values.length;
            //noinspection ForLoopReplaceableByForEach
            for (int index = 0; index < valuesCount; index++) {
                insertedRowId = database.insert(tableName, null, values[index]);
                if (insertedRowId != -1) {
                    insertedRowCount++;
                }
            }
            database.setTransactionSuccessful();
            //noinspection ConstantConditions
            if (insertedRowCount > 0) {
                getContext().getContentResolver().notifyChange(baseUri, null);
            }
            return insertedRowCount;
        } finally {
            database.endTransaction();
        }
    }

    @NonNull
    @Override
    public ContentProviderResult[] applyBatch(@NonNull ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
        ContentProviderResult[] results;
        SQLiteDatabase database = mDatabaseOpenHelper.getWritableDatabase();
        try {
            database.beginTransaction();
            results = super.applyBatch(operations);
            database.setTransactionSuccessful();
            return results;
        } finally {
            database.endTransaction();
        }
    }

    private synchronized UriMatcher getMatcher() {
        if (mURIMatcher == null) {
            mURIMatcher = createMatcher();
        }

        return mURIMatcher;
    }

    private UriMatcher createMatcher() {
        UriMatcher instance = new UriMatcher(UriMatcher.NO_MATCH);
        instance.addURI(DatabaseContract.CONTENT_AUTHORITY, DatabaseContract.Podcast.CONTENT_URI.getPath(), SUBSCRIPTIONS);
        instance.addURI(DatabaseContract.CONTENT_AUTHORITY, DatabaseContract.Podcast.CONTENT_URI.buildUpon().appendPath("#").build().getPath(), SUBSCRIPTION_BY_ID);
        instance.addURI(DatabaseContract.CONTENT_AUTHORITY, DatabaseContract.Podcast.CONTENT_URI.buildUpon().appendPath("#").appendPath(DatabaseContract.EPISODES_PATH).build().getPath(), EPISODES_FOR_SUBSCRIPTION);
        instance.addURI(DatabaseContract.CONTENT_AUTHORITY, DatabaseContract.Episode.CONTENT_URI.getPath(), EPISODES);
        instance.addURI(DatabaseContract.CONTENT_AUTHORITY, DatabaseContract.Episode.CONTENT_URI.buildUpon().appendPath("#").build().getPath(), EPISODE_BY_ID);
        instance.addURI(DatabaseContract.CONTENT_AUTHORITY, DatabaseContract.PlaylistEntry.CONTENT_URI.getPath(), PLAYLIST);
        instance.addURI(DatabaseContract.CONTENT_AUTHORITY, DatabaseContract.PlaylistEntry.CONTENT_URI.buildUpon().appendPath("#").build().getPath(), PLAYLIST_BY_ID);
        return instance;
    }

    // URI Matching constants.
    private static final int SUBSCRIPTIONS = 0x1000;
    private static final int SUBSCRIPTION_BY_ID = 0x1001;
    private static final int EPISODES = 0x2000;
    private static final int EPISODES_FOR_SUBSCRIPTION = 0x2002;
    private static final int EPISODE_BY_ID = 0x2001;
    private static final int PLAYLIST = 0x3000;
    private static final int PLAYLIST_BY_ID = 0x3001;
}
