package com.neykov.podcastportal.model.subscriptions;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.neykov.podcastportal.model.entity.Episode;
import com.neykov.podcastportal.model.entity.RemotePodcastData;
import com.neykov.podcastportal.model.entity.PodcastSubscription;
import com.neykov.podcastportal.model.entity.converter.EpisodesConverter;
import com.neykov.podcastportal.model.entity.converter.SubscriptionConverter;
import com.neykov.podcastportal.model.persistence.DatabaseContract;
import com.neykov.podcastportal.model.rss.RSSFeed;
import com.neykov.podcastportal.model.rss.RssFeedParser;
import com.neykov.podcastportal.model.rss.RssItem;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

@Singleton
public class SubscriptionDownloader {

    private static final String TAG = SubscriptionDownloader.class.getSimpleName();
    public static final int UPDATE_TIME_TOLERANCE_MINUTES = 10;
    private Context mContext;
    private OkHttpClient mHttpClient;
    private RssFeedParser mFeedParser;
    private EpisodesConverter mEpisodesConverter;
    private SubscriptionConverter mSubscriptionConverter;

    @Inject
    public SubscriptionDownloader(Context context, OkHttpClient mHttpClient) {
        this.mContext = context;
        this.mHttpClient = mHttpClient;
        this.mEpisodesConverter = new EpisodesConverter();
        this.mSubscriptionConverter = new SubscriptionConverter();
        this.mFeedParser = new RssFeedParser();
    }

    public Observable<PodcastSubscription> fetchSubscriptionUpdates(PodcastSubscription podcastSubscription) {
        if (podcastSubscription.getId() == null) {
            throw new IllegalArgumentException("This method should only be used for existing, persisted subscriptions.");
        }

        return downloadFeedData(podcastSubscription.getUrl())
                .flatMap(updatedRssFeed -> {
                    Date timestampUtc = DateTime.now(DateTimeZone.UTC).minusMinutes(UPDATE_TIME_TOLERANCE_MINUTES).toDate();
                    PodcastSubscription updatedPodcastSubscription = new PodcastSubscription.Builder(podcastSubscription)
                            .setTitle(updatedRssFeed.getChannel().getTitle())
                            .setDescription(updatedRssFeed.getChannel().getDescription())
                            .setDateUpdated(timestampUtc)
                            .build();
                    return storeSubscriptionUpdates(updatedPodcastSubscription, updatedRssFeed);
                });
    }

    public Observable<PodcastSubscription> fetchSubscriptionAndEpisodesData(RemotePodcastData source) {
        return downloadFeedData(source.getUrl())
                .flatMap(rssFeed -> {
                    Date timestampUtc = rssFeed != null ?
                            DateTime.now(DateTimeZone.UTC).minusMinutes(UPDATE_TIME_TOLERANCE_MINUTES).toDate() : new Date(0);
                    return storeNewSubscriptionData(source, rssFeed, timestampUtc);
                });
    }

    private Observable<PodcastSubscription> storeSubscriptionUpdates(PodcastSubscription updatedPodcastSubscription, RSSFeed updatedRssFeed) {
        return Observable.<PodcastSubscription>create(subscriber -> {
            try {
                ContentProviderOperation subscriptionUpdateOp = ContentProviderOperation
                        .newUpdate(DatabaseContract.Podcast.buildItemUri(updatedPodcastSubscription.getId()))
                        .withExpectedCount(1)
                        .withValues(mSubscriptionConverter.convert(updatedPodcastSubscription))
                        .build();
                ArrayList<ContentProviderOperation> ops = new ArrayList<>();
                ops.add(subscriptionUpdateOp);

                int newEpisodes = 0;
                for (RssItem item : updatedRssFeed.getChannel().getItemList()) {
                    if (item.getPubDate().after(updatedPodcastSubscription.getDateUpdatedUtc()) &&
                            !episodeAlreadyStored(item, updatedPodcastSubscription.getId())) {
                        ops.add(buildEpisodeUpdateInsertOperation(item, updatedPodcastSubscription.getId()));
                        newEpisodes++;
                    }
                }

                Log.d(TAG, "Saving " + newEpisodes + " new episodes...");
                mContext.getContentResolver().applyBatch(DatabaseContract.CONTENT_AUTHORITY, ops);
                Log.d(TAG, "Success!");
                subscriber.onNext(updatedPodcastSubscription);
                subscriber.onCompleted();
            } catch (RemoteException | AssertionError | OperationApplicationException e) {
                subscriber.onError(e);
            }
        });
    }

    private Observable<PodcastSubscription> storeNewSubscriptionData(RemotePodcastData source, @Nullable RSSFeed feed, Date timestampUtc) {
        return Observable.<PodcastSubscription>create(subscriber -> {
            try {
                PodcastSubscription podcastSubscription = new PodcastSubscription.Builder(source)
                        .setDateUpdated(timestampUtc)
                        .build();

                ArrayList<ContentProviderOperation> ops = new ArrayList<>();
                ops.add(buildSubscriptionInsertOperation(podcastSubscription));
                int newEpisodes = 0;

                if (feed != null && !feed.getChannel().getItemList().isEmpty()) {
                    for (RssItem item : feed.getChannel().getItemList()) {
                        ops.add(buildEpisodeInsertOperations(item, 0));
                        newEpisodes++;
                    }
                }

                Log.d(TAG, "Saving subscription and " + newEpisodes + " new episodes...");
                mContext.getContentResolver().applyBatch(DatabaseContract.CONTENT_AUTHORITY, ops);
                Log.d(TAG, "Success!");
                subscriber.onNext(podcastSubscription);
                subscriber.onCompleted();
            } catch (RemoteException | OperationApplicationException e) {
                subscriber.onError(e);
            }
        });
    }

    private Observable<RSSFeed> downloadFeedData(String feedUrl) {
        return Observable.create(subscriber -> {
            Request request = new Request.Builder()
                    .url(feedUrl)
                    .get()
                    .build();
            try {
                Log.d(TAG, "Downloading RSS feed data from " + feedUrl);
                Response response = mHttpClient.newCall(request).execute();
                RSSFeed feedData = mFeedParser.parse(response.body().byteStream());
                Log.d(TAG, "Success, total items in the channel:" + feedData.getChannel().getItemList().size());
                subscriber.onNext(feedData);
                subscriber.onCompleted();
            } catch (Exception e) {
                Log.w(TAG, "Failed, details:", e);
                subscriber.onError(e);
            }
        });
    }

    private ContentProviderOperation buildSubscriptionInsertOperation(PodcastSubscription podcastSubscription) {
        return ContentProviderOperation.newInsert(DatabaseContract.Podcast.CONTENT_URI)
                .withValues(mSubscriptionConverter.convert(podcastSubscription))
                .build();
    }

    private ContentProviderOperation buildEpisodeInsertOperations(RssItem item, int backReferenceIndex) {
        Episode newEpisode = createEpisodeFromRssItem(item, 0);
        return ContentProviderOperation
                .newInsert(DatabaseContract.Episode.CONTENT_URI)
                .withValues(mEpisodesConverter.convert(newEpisode))
                .withValueBackReference(DatabaseContract.Episode.PODCAST_ID, backReferenceIndex)
                .build();
    }

    private ContentProviderOperation buildEpisodeUpdateInsertOperation(RssItem item, long parentId) {
        Episode newEpisode = createEpisodeFromRssItem(item, parentId);
        return ContentProviderOperation
                .newInsert(DatabaseContract.Episode.CONTENT_URI)
                .withExpectedCount(1)
                .withValues(mEpisodesConverter.convert(newEpisode))
                .build();
    }

    private Episode createEpisodeFromRssItem(RssItem source, long parentId) {
        return new Episode(0,
                parentId,
                source.getTitle(),
                source.getDescription(),
                source.getContent().getContentUrl(),
                source.getContent().getMimeType(),
                null,
                source.getContent().getContentLength(),
                Episode.REMOTE,
                null,
                false,
                null,
                source.getPubDate());
    }

    private boolean episodeAlreadyStored(RssItem item, long parentId) {
        Cursor queryResults = null;
        try {
            queryResults = mContext.getContentResolver().query(DatabaseContract.Episode.CONTENT_URI,
                    new String[]{DatabaseContract.Episode.PODCAST_ID, DatabaseContract.Episode.CONTENT_URL},
                    DatabaseContract.Episode.PODCAST_ID + "=? "
                            + DatabaseContract.Episode.CONTENT_URL + " =?",
                    new String[]{String.valueOf(parentId), item.getContent().getContentUrl()},
                    null);
            if (queryResults == null) {
                throw new AssertionError("Could not execute the query.");
            }
            return queryResults.getCount() > 0;
        } finally {
            if (queryResults != null) {
                queryResults.close();
            }
        }
    }
}
