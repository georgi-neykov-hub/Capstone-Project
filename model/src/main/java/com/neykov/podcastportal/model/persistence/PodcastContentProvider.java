package com.neykov.podcastportal.model.persistence;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.text.TextUtils;

public class PodcastContentProvider extends ContentProvider {

    private DatabaseOpenHelper mDatabaseOpenHelper;
    private UriMatcher mURIMatcher;

    public PodcastContentProvider() {
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int deletedRowsCount;
        SQLiteDatabase db = mDatabaseOpenHelper.getWritableDatabase();
        switch (getMatcher().match(uri)) {
            case SUBSCRIPTIONS:
                deletedRowsCount = db.delete(DatabaseContract.Subscription.TABLE_NAME, selection, selectionArgs);
                break;
            case SUBSCRIPTION_BY_ID:
                String idString = uri.getLastPathSegment();
                String whereStatement = DatabaseContract.Subscription._ID + " = " + idString;
                if (!TextUtils.isEmpty(selection)) {
                    whereStatement += " AND " + selection;
                }
                deletedRowsCount = db.delete(
                        DatabaseContract.Subscription.TABLE_NAME, whereStatement, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        if (deletedRowsCount > 0) {
            // Notify all listeners about the changes.
            //noinspection ConstantConditions
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deletedRowsCount;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public boolean onCreate() {
        mDatabaseOpenHelper = new DatabaseOpenHelper(getContext(), DatabaseContract.DATABASE_NAME, null, 1);
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String targetTable;
        switch (getMatcher().match(uri)) {
            case SUBSCRIPTIONS:
                targetTable = DatabaseContract.Subscription.TABLE_NAME;
                break;
            case EPISODES:
                targetTable = DatabaseContract.Episode.TABLE_NAME;
                break;
            case DOWNLOADS:
                targetTable = DatabaseContract.Download.TABLE_NAME;
                break;
            default:
                throw new UnsupportedOperationException("Cannot handle URI.");
        }

        return mDatabaseOpenHelper.getReadableDatabase().query(
                targetTable,
                projection,
                selection,
                selection == null ? null : selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int match = mURIMatcher.match(uri);
        String tableName;
        switch (match){
            case SUBSCRIPTIONS:
            case SUBSCRIPTION_BY_ID:
                tableName = DatabaseContract.Subscription.TABLE_NAME;
                break;
            case EPISODE_BY_ID:
            case EPISODES:
                tableName = DatabaseContract.Episode.TABLE_NAME;
                break;
            case DOWNLOAD_BY_ID:
            case DOWNLOADS:
                tableName = DatabaseContract.Download.TABLE_NAME;
                break;
            default:
                throw new UnsupportedOperationException("Cannot handle URI.");
        }

        switch (match){
            case SUBSCRIPTION_BY_ID:
            case EPISODE_BY_ID:
            case DOWNLOAD_BY_ID:
                selection = BaseColumns._ID + "=?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
        }

        int udpatedRows = mDatabaseOpenHelper.getWritableDatabase()
                .update(tableName, values, selection, selectionArgs);
        if(udpatedRows > 0){
            //noinspection ConstantConditions
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return udpatedRows;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        Uri newItemUri;
        switch (getMatcher().match(uri)) {
            case SUBSCRIPTIONS:
                newItemUri = insertSubscription(mDatabaseOpenHelper.getWritableDatabase(), values);
                break;
            case EPISODES:
                newItemUri = insertEpisode(mDatabaseOpenHelper.getWritableDatabase(), values);
                break;
            case DOWNLOADS:
                newItemUri = insertDownload(mDatabaseOpenHelper.getWritableDatabase(), values);
                break;
            default:
                throw new IllegalArgumentException("Cannot insert, Unknown URI.");
        }
        //noinspection ConstantConditions
        getContext().getContentResolver().notifyChange(newItemUri, null);
        return newItemUri;
    }

    private Uri insertSubscription(SQLiteDatabase db, ContentValues values) {
        long row = db.insertOrThrow(DatabaseContract.Subscription.TABLE_NAME, null, values);
        return DatabaseContract.Subscription.CONTENT_URI.buildUpon().appendPath(String.valueOf(row)).build();
    }

    private Uri insertEpisode(SQLiteDatabase db, ContentValues values) {
        long row = db.insertOrThrow(DatabaseContract.Episode.TABLE_NAME, null, values);
        return DatabaseContract.Episode.CONTENT_URI.buildUpon().appendPath(String.valueOf(row)).build();
    }

    private Uri insertDownload(SQLiteDatabase db, ContentValues values) {
        long row = db.insertOrThrow(DatabaseContract.Download.TABLE_NAME, null, values);
        return DatabaseContract.Episode.CONTENT_URI.buildUpon().appendPath(String.valueOf(row)).build();
    }

    private synchronized UriMatcher getMatcher() {
        if (mURIMatcher == null) {
            mURIMatcher = createMatcher();
        }

        return mURIMatcher;
    }

    private UriMatcher createMatcher() {
        UriMatcher instance = new UriMatcher(UriMatcher.NO_MATCH);
        instance.addURI(DatabaseContract.CONTENT_AUTHORITY, DatabaseContract.Subscription.CONTENT_URI.getPath(), SUBSCRIPTIONS);
        instance.addURI(DatabaseContract.CONTENT_AUTHORITY, DatabaseContract.Episode.CONTENT_URI.getPath(), EPISODES);
        instance.addURI(DatabaseContract.CONTENT_AUTHORITY, DatabaseContract.Download.CONTENT_URI.getPath(), DOWNLOADS);
        instance.addURI(DatabaseContract.CONTENT_AUTHORITY, DatabaseContract.Subscription.CONTENT_URI.buildUpon().appendPath("#").build().getPath(), SUBSCRIPTION_BY_ID);
        instance.addURI(DatabaseContract.CONTENT_AUTHORITY, DatabaseContract.Episode.CONTENT_URI.buildUpon().appendPath("#").build().getPath(), EPISODE_BY_ID);
        instance.addURI(DatabaseContract.CONTENT_AUTHORITY, DatabaseContract.Download.CONTENT_URI.buildUpon().appendPath("#").build().getPath(), DOWNLOAD_BY_ID);
        return instance;
    }

    // URI Matching constants.
    private static final int SUBSCRIPTIONS = 0x1000;
    private static final int EPISODES = 0x2000;
    private static final int DOWNLOADS = 0x3000;
    private static final int SUBSCRIPTION_BY_ID = 0x1001;
    private static final int EPISODE_BY_ID = 0x2001;
    private static final int DOWNLOAD_BY_ID = 0x3001;
}
