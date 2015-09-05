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
    }

    public static final class Episode implements BaseColumns {


    }

}
