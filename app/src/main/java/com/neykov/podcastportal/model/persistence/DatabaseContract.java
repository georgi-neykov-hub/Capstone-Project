package com.neykov.podcastportal.model.persistence;

import android.net.Uri;
import android.provider.BaseColumns;

import com.neykov.podcastportal.BuildConfig;

public final class DatabaseContract {

    public static final String CONTENT_AUTHORITY = BuildConfig.APPLICATION_ID;
    public static final String DATABASE_NAME = "PodcastPortal";


    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static final class Subscription implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath("subscriptions").build();
        public static final String TABLE_NAME = "Subscriptions";

        public static final String TITLE = "Title";
        public static final String DESCRIPTION = "Description";
        public static final String FEED_URL = "FeedURL";
        public static final String SUBSCRIBERS = "Subscribers";
        public static final String DATE_UPDATED = "DateUpdate";
        public static final String LOGO_URL = "LogoURL";
        public static final String LOCAL_LOGO_URL = "LocalLogoURL";
        public static final String WEBSITE = "Website";
    }

    public static final class Episode implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath("episodes").build();
        public static final String TABLE_NAME = "Episodes";

        public static final String TITLE = "Title";
        public static final String DESCRIPTION = "Description";
        public static final String CONTENT_URL = "ContentURL";
        public static final String PODCAST_URL = "PodcastURL";
        public static final String PODCAST_TITLE = "PodcastTitle";
        public static final String PODCAST_ID = "PodcastID";
        public static final String WEBSITE = "Website";
        public static final String RELEASE_DATE = "ReleaseDate";
        public static final String DOWNLOAD_ID = "DownloadID";
    }

    public static final class Download implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath("downloads").build();
        public static final String TABLE_NAME = "Download";

        public static final String FILE_URL = "FileURL";
        public static final String FILE_SIZE = "FileSize";
        public static final String STATE = "State";
        public static final String EPISODE_ID = "EpisodeID";
    }
}
