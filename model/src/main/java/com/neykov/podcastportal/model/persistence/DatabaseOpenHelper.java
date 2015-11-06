package com.neykov.podcastportal.model.persistence;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.neykov.podcastportal.model.persistence.DatabaseContract.Podcast;
import com.neykov.podcastportal.model.persistence.DatabaseContract.Episode;
import com.neykov.podcastportal.model.persistence.DatabaseContract.PlaylistEntry;

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
        // Create the playlist table.
        db.execSQL(SQL_CREATE_PLAYLIST_TABLE);
        db.execSQL(SQL_TRIGGERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private static final String SQL_CREATE_SUBSCRIPTIONS_TABLE = "CREATE TABLE " + Podcast.TABLE_NAME + " (" +
            Podcast.PODCAST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            Podcast.TITLE + " TEXT," +
            Podcast.DESCRIPTION + " TEXT," +
            Podcast.FEED_URL + " TEXT," +
            Podcast.SUBSCRIBERS + " INTEGER," +
            Podcast.DATE_UPDATED + " INTEGER," +
            Podcast.LOGO_URL + " TEXT," +
            Podcast.LOCAL_LOGO_URL + " TEXT," +
            Podcast.WEBSITE + " TEXT" +
            " );";

    private static final String SQL_CREATE_EPISODES_TABLE = "CREATE TABLE " + Episode.TABLE_NAME + " (" +
            Episode.EPISODE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            Episode.TITLE + " TEXT NOT NULL," +
            Episode.DESCRIPTION + " TEXT NOT NULL, " +
            Episode.CONTENT_URL + " TEXT NOT NULL, " +
            Episode.THUMBNAIL + " TEXT NOT NULL," +
            Episode.MIME_TYPE + " TEXT NOT NULL," +
            Episode.DURATION + " INTEGER," +
            Episode.PODCAST_ID + " INTEGER NOT NULL," +
            Episode.PLAYLIST_ENTRY_ID + " INTEGER DEFAULT NULL," +
            Episode.WATCHED + " INTEGER NOT NULL," +
            Episode.WEBSITE + " TEXT," +
            Episode.RELEASE_DATE + " INTEGER," +
            Episode.DOWNLOAD_ID + " INTEGER, " +
            Episode.DOWNLOAD_STATE + " INTEGER NOT NULL, " +
            Episode.FILE_URL + " TEXT," +
            Episode.FILE_SIZE + " INTEGER," +
            "FOREIGN KEY (" + Episode.PODCAST_ID + ") REFERENCES " + Podcast.TABLE_NAME + "(" + Podcast.PODCAST_ID + ") ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED," +
            "FOREIGN KEY (" + Episode.PLAYLIST_ENTRY_ID + ") REFERENCES " + PlaylistEntry.TABLE_NAME + "(" + PlaylistEntry.PLAYLIST_ENTRY_ID + ") ON DELETE SET NULL DEFERRABLE INITIALLY DEFERRED" +
            " );";

    private static final String SQL_CREATE_PLAYLIST_TABLE = "CREATE TABLE " + PlaylistEntry.TABLE_NAME + " (" +
            PlaylistEntry.PLAYLIST_ENTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            PlaylistEntry.EPISODE_ID + " INTEGER NOT NULL," +
            PlaylistEntry.NEXT_ITEM_ID + " INTEGER," +
            PlaylistEntry.PREVIOUS_ITEM_ID + " INTEGER," +
            "FOREIGN KEY (" + PlaylistEntry.EPISODE_ID + ") REFERENCES " + Episode.TABLE_NAME + "(" + Episode.EPISODE_ID + ") ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, " +
            "FOREIGN KEY (" + PlaylistEntry.NEXT_ITEM_ID + ") REFERENCES " + PlaylistEntry.TABLE_NAME + "(" + PlaylistEntry.PLAYLIST_ENTRY_ID + ") ON DELETE NO ACTION, " +
            "FOREIGN KEY (" + PlaylistEntry.PREVIOUS_ITEM_ID + ") REFERENCES " + PlaylistEntry.TABLE_NAME + "(" + PlaylistEntry.PLAYLIST_ENTRY_ID + ") ON DELETE NO ACTION" +
            " );";

    private static final String SQL_TRIGGERS =
            "CREATE TRIGGER updatePlaylistLinksOnDelete AFTER DELETE ON " + PlaylistEntry.TABLE_NAME + " " +
                    "BEGIN \n" +
                    "UPDATE " + PlaylistEntry.TABLE_NAME + " SET " + PlaylistEntry.PREVIOUS_ITEM_ID + " = old." + PlaylistEntry.PREVIOUS_ITEM_ID +
                    " WHERE " + PlaylistEntry.PLAYLIST_ENTRY_ID + " = old." + PlaylistEntry.NEXT_ITEM_ID + ";\n" +
                    " UPDATE " + PlaylistEntry.TABLE_NAME + " SET " + PlaylistEntry.NEXT_ITEM_ID + " = old." + PlaylistEntry.NEXT_ITEM_ID +
                    " WHERE " + PlaylistEntry.PLAYLIST_ENTRY_ID + " = old." + PlaylistEntry.PREVIOUS_ITEM_ID + ";\n" +
                    " END;";
}
