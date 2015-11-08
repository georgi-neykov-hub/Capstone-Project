package com.neykov.podcastportal.model.persistence;

import android.content.ContentUris;
import android.net.Uri;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class DatabaseContract {

    public static final String CONTENT_AUTHORITY = "com.neykov.podcastportal";
    public static final String DATABASE_NAME = "PodcastPortal";


    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String EPISODES_PATH = "episodes";

    public static final class Podcast {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath("subscriptions").build();
        public static final String TABLE_NAME = "Subscriptions";

        public static final String PODCAST_ID = "PodcastId";
        public static final String TITLE = "Title";
        public static final String DESCRIPTION = "Description";
        public static final String FEED_URL = "FeedURL";
        public static final String SUBSCRIBERS = "Subscribers";
        public static final String DATE_UPDATED = "DateUpdate";
        public static final String LOGO_URL = "LogoURL";
        public static final String LOCAL_LOGO_URL = "LocalLogoURL";
        public static final String WEBSITE = "Website";

        public static Uri buildItemUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    public static final class Episode {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(EPISODES_PATH).build();
        public static final String TABLE_NAME = "Episodes";

        public static final String EPISODE_ID = "EpisodeId";
        public static final String PODCAST_ID = Podcast.PODCAST_ID;
        public static final String TITLE = "PodcastTitle";
        public static final String DESCRIPTION = "Description";
        public static final String CONTENT_URL = "ContentURL";
        public static final String THUMBNAIL = "ThumbnailURL";
        public static final String MIME_TYPE = "MimeType";
        public static final String FILE_URL = "FileURL";
        public static final String FILE_SIZE = "FileSize";
        public static final String DOWNLOAD_STATE = "DownloadState";
        public static final String DOWNLOAD_ID = "DownloadId";
        public static final String DURATION = "Duration";
        public static final String WATCHED = "Watched";
        public static final String WEBSITE = "Website";
        public static final String PLAYLIST_ENTRY_ID = PlaylistEntry.PLAYLIST_ENTRY_ID;
        public static final String RELEASE_DATE = "ReleaseDate";

        @Retention(RetentionPolicy.SOURCE)
        @IntDef({REMOTE, DOWNLOADED, DOWNLOADING})
        public @interface DownloadState {
        }

        public final static int REMOTE = 0;
        public final static int DOWNLOADING = 1;
        public final static int DOWNLOADED = 2;

        public static Uri buildItemUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildSubscriptionEpisodesUri(long subscriptionId){
            return Podcast.buildItemUri(subscriptionId).buildUpon().appendPath(EPISODES_PATH).build();
        }

        public static Uri buildSubscriptionEpisodesUri(long subscriptionId, long episodeId){
            return ContentUris.withAppendedId(buildSubscriptionEpisodesUri(subscriptionId), episodeId);
        }
    }

    public static final class PlaylistEntry {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath("playlist").build();
        public static final String TABLE_NAME = "Playlist";

        public static final String PLAYLIST_ENTRY_ID = "EntryId";
        public static final String PREVIOUS_ITEM_ID = "PreviousItemId";
        public static final String NEXT_ITEM_ID = "NextItemId";
        public static final String EPISODE_ID = Episode.EPISODE_ID;

        public static Uri buildItemUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }


}
