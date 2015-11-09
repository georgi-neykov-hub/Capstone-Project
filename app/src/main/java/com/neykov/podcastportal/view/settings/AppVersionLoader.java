package com.neykov.podcastportal.view.settings;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.neykov.podcastportal.BuildConfig;

/**
 * A simple Loader class to perform background work and load it to a view.
 * Totally unnecessary for the project's MVP architecture, it's purpose is to only fulfill the requirement to use Loaders to
 * bring data to the views as per
 * <a href="https://discussions.udacity.com/t/are-loaders-and-contentprovider-necessary/29762/7?u=georgi_3858558266175">the related discussion answer</a>
 */
public class AppVersionLoader extends AsyncTaskLoader<String> {
    public AppVersionLoader(Context context) {
        super(context);
    }

    @Override
    public String loadInBackground() {
        return String.format("v%1$s Build %2$d", BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE);
    }
}
