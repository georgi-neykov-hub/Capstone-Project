package com.neykov.podcastportal.model.persistence;

import android.provider.BaseColumns;

/**
 * Created by Georgi on 5.9.2015 г..
 */
public final class DatabaseContract {

    public static final class Podcast implements BaseColumns {
        public static final String TITLE = "Title";
        public static final String DESCRIPTION = "Description";
        public static final String FEED_URL = "Feed URL";
        public static final String SUBSCRIBERS = "Subscribers";
        public static final String DATE_UPDATED = "Date Update";
        public static final String LOGO_URL = "Logo URL";
        public static final String LOCAL_LOGO_URL = "Local Logo URL";
    }

    public static final class Episode implements BaseColumns {
        public static final String TITLE = "Title";
        public static final String DESCRIPTION = "Description";
        public static final String CONTENT_URL = "Content URL";
        public static final String PODCAST_URL = "Podcast URL";
        public static final String PODCAST_TITLE = "Podcast Title";
        public static final String PODCAST_ID = "Podcast ID";
        public static final String WEBSITE = "Website";
        public static final String RELEASE_DATE = "Release Date";
        public static final String DOWNLOAD_ID = "Download ID";
    }

    public static final class Download implements BaseColumns {
        public static final String FILE_URL = "File URL";
        public static final String FILE_SIZE = "File Size";
        public static final String STATE = "File URL";
        public static final String EPISODE_ID = "Episode ID";
    }
}
