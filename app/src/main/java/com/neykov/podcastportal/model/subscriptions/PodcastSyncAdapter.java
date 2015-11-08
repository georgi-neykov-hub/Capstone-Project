package com.neykov.podcastportal.model.subscriptions;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import com.neykov.podcastportal.model.LogHelper;
import com.neykov.podcastportal.model.ModelComponent;
import com.neykov.podcastportal.model.ModelComponentProvider;
import com.neykov.podcastportal.model.entity.PodcastSubscription;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

public class PodcastSyncAdapter extends AbstractThreadedSyncAdapter {

    public static final String KEY_PODCAST_IDS = "com.neykov.podcastportal.PodcastSyncAdapter.PODCAST_IDS";

    private SubscriptionsManager mSubscriptionsManager;

    public PodcastSyncAdapter(Context context, boolean autoInitialize) {
        this(context, autoInitialize, false);
    }

    public PodcastSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        ModelComponent mModelComponent = ((ModelComponentProvider) context.getApplicationContext()).getModelComponent();
        mSubscriptionsManager = mModelComponent.getSubscriptionsManager();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        int updatedItemsCount = 0;
        List<PodcastSubscription> subscriptions;
        try {
            /*
            * Get the sync targets.
            * */
            subscriptions = resolveSyncTargets(extras);

        } catch (Throwable throwable) {
            collectSyncErrors(throwable, syncResult);
            return;
        }


        /*
        * Try to fetch updates for each found podcast subscription.
        * */
        for (PodcastSubscription podcastSubscription : subscriptions) {
            try {
                PodcastSubscription updated = mSubscriptionsManager.updateSubscription(podcastSubscription)
                        .toBlocking().single();
                if(updated != podcastSubscription){
                    updatedItemsCount++;
                }
            } catch (Throwable e) {
                collectSyncErrors(e, syncResult);
            }
        }

        syncResult.stats.numEntries = updatedItemsCount;
    }

    private void collectSyncErrors(Throwable error, SyncResult errorContainer) {
        if(error instanceof IOException){
            errorContainer.stats.numIoExceptions++;
        } else if (error instanceof XmlPullParserException){
            errorContainer.stats.numParseExceptions++;
        } else if (error instanceof android.database.SQLException){
            errorContainer.databaseError = true;
        }
    }

    private List<PodcastSubscription> resolveSyncTargets(Bundle syncExtras){
        if (syncExtras != null && !syncExtras.containsKey(KEY_PODCAST_IDS)) {
            long[] podcastIds = syncExtras.getLongArray(KEY_PODCAST_IDS);
            return mSubscriptionsManager.getSyncTargets(podcastIds);
        }else {
            return mSubscriptionsManager.getSubscriptionsStream(false)
                    .toBlocking()
                    .first();
        }
    }
}
