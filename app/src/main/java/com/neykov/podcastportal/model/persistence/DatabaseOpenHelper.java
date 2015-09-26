package com.neykov.podcastportal.model.persistence;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.neykov.podcastportal.model.persistence.DatabaseContract.Subscription;
import com.neykov.podcastportal.model.persistence.DatabaseContract.Episode;
import com.neykov.podcastportal.model.persistence.DatabaseContract.Download;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

    public DatabaseOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    public DatabaseOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the podcasts table.
        db.execSQL(SQL_CREATE_SUBSCRIPTIONS_TABLE);
        // Create the episodes table.
        db.execSQL(SQL_CREATE_EPISODES_TABLE);
        // Create the downloads table.
        db.execSQL(SQL_CREATE_DOWNLOADS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private static final String SQL_CREATE_SUBSCRIPTIONS_TABLE = "CREATE TABLE " + Subscription.TABLE_NAME + " (" +
            Subscription._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            Subscription.TITLE + " TEXT," +
            Subscription.DESCRIPTION + " TEXT," +
            Subscription.FEED_URL + " TEXT," +
            Subscription.SUBSCRIBERS + " INTEGER," +
            Subscription.DATE_UPDATED + " INTEGER," +
            Subscription.LOGO_URL + " TEXT," +
            Subscription.LOCAL_LOGO_URL + " TEXT," +
            Subscription.WEBSITE + " TEXT" +
            " );";

    private static final String SQL_CREATE_EPISODES_TABLE = "CREATE TABLE " + Episode.TABLE_NAME + " (" +
            Episode._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            Episode.TITLE + " TEXT," +
            Episode.DESCRIPTION + " TEXT," +
            Episode.CONTENT_URL + " TEXT," +
            Episode.PODCAST_URL + " TEXT," +
            Episode.PODCAST_ID + " INTEGER," +
            Episode.WEBSITE + " TEXT," +
            Episode.RELEASE_DATE + " INTEGER," +
            Episode.DOWNLOAD_ID + " INTEGER," +
            " FOREIGN KEY (" + Episode.PODCAST_ID + ") REFERENCES " + Subscription.TABLE_NAME + Subscription._ID + " ON UPDATE CASCADE DEFERRABLE INITIALLY DEFERRED" +
            " FOREIGN KEY (" + Episode.DOWNLOAD_ID + ") REFERENCES " + Download.TABLE_NAME + Download._ID + " ON UPDATE SET NULL DEFERRABLE INITIALLY DEFERRED" +
            " );";

    private static final String SQL_CREATE_DOWNLOADS_TABLE = "CREATE TABLE " + Download.TABLE_NAME + " (" +
            Download._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            Download.STATE + " INTEGER NOT NULL," +
            Download.FILE_URL + " TEXT," +
            Download.FILE_SIZE + " INTEGER," +
            Download.EPISODE_ID + " INTEGER," +
             "FOREIGN KEY (" + Download.EPISODE_ID + ") REFERENCES " + Download.TABLE_NAME + Download._ID + " ON UPDATE CASCADE DEFERRABLE INITIALLY DEFERRED" +
            " );";
}
