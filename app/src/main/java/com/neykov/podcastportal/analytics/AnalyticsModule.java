package com.neykov.podcastportal.analytics;

import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.neykov.podcastportal.R;
import com.neykov.podcastportal.model.utils.Global;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AnalyticsModule {

    @Provides
    @Singleton
    public GoogleAnalytics provideAnalytics(@Global Context context){
        return GoogleAnalytics.getInstance(context);
    }

    @Provides
    @Singleton
    public Tracker provideTracker(GoogleAnalytics analytics){
        return analytics.newTracker(R.xml.analytics_tracker_config);
    }
}
