package com.neykov.podcastportal.view.settings;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.neykov.podcastportal.R;
import com.neykov.podcastportal.view.base.BaseActivity;

public class AboutActivity extends BaseActivity {

    private static final int QUERY_LOADER_ID = 1;

    private AdView mAdsView;
    private TextView mBuildDataTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAdsView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdsView.loadAd(adRequest);
        mBuildDataTextView = (TextView) findViewById(R.id.version);
        // Do the time consuming, potentially UI thread-blocking operation.
        getSupportLoaderManager().restartLoader(QUERY_LOADER_ID, null, mQueryCallbacks).forceLoad();

        //Fire a simple Analytics screen event.
        Tracker tracker = getDependencyResolver().getTracker();
        tracker.setScreenName(this.getClass().getSimpleName());
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAdsView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdsView.resume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private final LoaderManager.LoaderCallbacks<String> mQueryCallbacks = new LoaderManager.LoaderCallbacks<String>() {

        @Override
        public Loader<String> onCreateLoader(int id, Bundle args) {
            return new AppVersionLoader(AboutActivity.this);
        }

        @Override
        public void onLoadFinished(Loader<String> loader, String data) {
            mBuildDataTextView.setText(data);
        }

        @Override
        public void onLoaderReset(Loader<String> loader) {
            // Ignore.
        }
    };

}
