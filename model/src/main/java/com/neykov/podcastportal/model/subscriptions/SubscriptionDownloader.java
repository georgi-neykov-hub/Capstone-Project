package com.neykov.podcastportal.model.subscriptions;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadata;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.webkit.MimeTypeMap;

import com.neykov.podcastportal.model.entity.Episode;
import com.neykov.podcastportal.model.entity.RemotePodcastData;
import com.neykov.podcastportal.model.entity.PodcastSubscription;
import com.neykov.podcastportal.model.entity.converter.EpisodesConverter;
import com.neykov.podcastportal.model.entity.converter.SubscriptionConverter;
import com.neykov.podcastportal.model.persistence.DatabaseContract;
import com.neykov.podcastportal.model.rss.Content;
import com.neykov.podcastportal.model.rss.RSSFeed;
import com.neykov.podcastportal.model.rss.RssChannel;
import com.neykov.podcastportal.model.rss.RssFeedParser;
import com.neykov.podcastportal.model.rss.RssItem;
import com.neykov.podcastportal.model.utils.Global;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

import okio.BufferedSink;
import okio.Okio;
import retrofit.mime.MimeUtil;
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
    public SubscriptionDownloader(@Global Context context, OkHttpClient mHttpClient) {
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
                .flatMap(updatedRssFeed -> storeSubscriptionUpdates(podcastSubscription, updatedRssFeed));
    }

    public Observable<PodcastSubscription> fetchSubscriptionAndEpisodesData(RemotePodcastData source) {
        return downloadFeedData(source.getUrl())
                .map(rssFeed1 -> {
                    File imageFile = downloadPodcastThumbnail(mContext, source.getLogoUrl());
                    return new Pair<>(rssFeed1, imageFile);
                })
                .flatMap(rssFeedImageFilePair -> {
                    Date timestampUtc = rssFeedImageFilePair.first != null ?
                            DateTime.now(DateTimeZone.UTC).minusMinutes(UPDATE_TIME_TOLERANCE_MINUTES).toDate() : new Date(0);
                    return storeNewSubscriptionData(source, rssFeedImageFilePair.first, rssFeedImageFilePair.second, timestampUtc);
                });
    }

    private Observable<PodcastSubscription> storeSubscriptionUpdates(final PodcastSubscription podcast, final RSSFeed updatedRssFeed) {
        List<RssItem> rssItems = updatedRssFeed.getChannel().getItemList();
        if (rssItems.isEmpty()) {
            return Observable.just(podcast);
        }

        return Observable.<PodcastSubscription>create(subscriber -> {
                    int newEpisodes = 0;
                    Date latestPubDate = new Date(0);
                    try {
                        for (RssItem item : updatedRssFeed.getChannel().getItemList()) {
                            if (item.getPubDate().after(podcast.getDateUpdatedUtc()) &&
                                    !episodeAlreadyStored(item, podcast.getId())) {
                                if (item.getPubDate().after(latestPubDate)) {
                                    latestPubDate = item.getPubDate();
                                }
                                String defaultThumbnail = podcast.getLocalLogoUrl() != null ?
                                        podcast.getLocalLogoUrl() : podcast.getLogoUrl();
                                Episode newEpisode = createEpisodeFromRssItem(item, podcast.getId(), defaultThumbnail);
                                mContext.getContentResolver().insert(DatabaseContract.Episode.CONTENT_URI, mEpisodesConverter.convert(newEpisode));
                                newEpisodes++;
                            }
                        }
                    } catch (Exception e) {
                        Log.w(TAG, "Error while storing new episode.", e);
                    }

                    if (newEpisodes == 0) {
                        subscriber.onNext(podcast);
                        subscriber.onCompleted();
                        return;
                    }

                    try {
                        Date timestampUtc = new DateTime(latestPubDate.getTime(), DateTimeZone.UTC).minusMinutes(UPDATE_TIME_TOLERANCE_MINUTES).toDate();
                        PodcastSubscription updatedPodcastSubscription = new PodcastSubscription.Builder(podcast)
                                .setTitle(updatedRssFeed.getChannel().getTitle())
                                .setDescription(updatedRssFeed.getChannel().getDescription())
                                .setDateUpdated(timestampUtc)
                                .build();
                        Log.d(TAG, "Saving " + newEpisodes + " new episodes...");
                        mContext.getContentResolver().update(DatabaseContract.Podcast.buildItemUri(podcast.getId()), null, null, null);
                        Log.d(TAG, "Success!");
                        subscriber.onNext(updatedPodcastSubscription);
                        subscriber.onCompleted();
                    } catch (Exception e) {
                        subscriber.onError(e);
                    }
                }
        );
    }

    public Observable<PodcastSubscription> downloadSubscriptionThumbnail(PodcastSubscription subscription) {
        if (subscription.getLocalLogoUrl() != null) {
            return Observable.just(subscription);
        }

        return Observable.create(subscriber -> {
            File localImageFile = downloadPodcastThumbnail(mContext, subscription.getLogoUrl());
            if (localImageFile == null) {
                subscriber.onError(new RuntimeException("Failed To download the image file."));
                return;
            }
            try {
                String filePath = localImageFile.toURI().toString();
                ContentValues values = new ContentValues(1);
                values.put(DatabaseContract.Podcast.LOCAL_LOGO_URL, filePath);
                mContext.getContentResolver().update(
                        DatabaseContract.Podcast.buildItemUri(subscription.getId()),
                        values,
                        null,
                        null);
                PodcastSubscription updatedPodcast = new PodcastSubscription.Builder(subscription)
                        .setLocalLogoUrl(filePath)
                        .build();
                subscriber.onNext(updatedPodcast);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    private Observable<PodcastSubscription> storeNewSubscriptionData(RemotePodcastData source, @Nullable RSSFeed feed, @Nullable File imageFile, Date timestampUtc) {
        return Observable.<PodcastSubscription>create(subscriber -> {
            try {
                PodcastSubscription podcastSubscription = new PodcastSubscription.Builder(source)
                        .setDateUpdated(timestampUtc)
                        .setLocalLogoUrl(imageFile != null ? imageFile.toURI().toString() : null)
                        .build();

                ArrayList<ContentProviderOperation> ops = new ArrayList<>();
                ops.add(buildSubscriptionInsertOperation(podcastSubscription));
                int newEpisodes = 0;

                if (feed != null && !feed.getChannel().getItemList().isEmpty()) {
                    for (RssItem item : feed.getChannel().getItemList()) {

                        Episode newEpisode = createEpisodeFromRssItem(item, 0, source.getLogoUrl());
                        ops.add(buildEpisodeInsertOperations(newEpisode, 0));
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

    private ContentProviderOperation buildEpisodeInsertOperations(Episode newEpisode, int backReferenceIndex) {
        return ContentProviderOperation
                .newInsert(DatabaseContract.Episode.CONTENT_URI)
                .withValues(mEpisodesConverter.convert(newEpisode))
                .withValueBackReference(DatabaseContract.Episode.PODCAST_ID, backReferenceIndex)
                .build();
    }


    private Episode createEpisodeFromRssItem(RssItem source, long parentId, String defautThumbnailUrl) {
        ContentMetaData data = extractMediaMetaData(source);
        return new Episode(0,
                parentId,
                source.getTitle(),
                source.getDescription(),
                source.getContent().getContentUrl(),
                data.getMimeType() != null ? data.getMimeType() : source.getContent().getMimeType(),
                null,
                source.getContent().getContentLength(),
                Episode.REMOTE,
                data.getDuration(),
                data.getThumbnailFrame() != null ? data.getThumbnailFrame().toURI().toString() : defautThumbnailUrl,
                false,
                null,
                source.getPubDate());
    }

    private boolean episodeAlreadyStored(RssItem item, long parentId) {
        Cursor queryResults = null;
        try {
            queryResults = mContext.getContentResolver().query(DatabaseContract.Episode.CONTENT_URI,
                    new String[]{DatabaseContract.Episode.PODCAST_ID, DatabaseContract.Episode.CONTENT_URL},
                    DatabaseContract.Episode.PODCAST_ID + "=? AND "
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

    private ContentMetaData extractMediaMetaData(RssItem item) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            Uri episodeUri = Uri.parse(item.getContent().getContentUrl());
            retriever.setDataSource(episodeUri.toString(), Collections.emptyMap());
            return buildMetaDataWithRetriever(retriever, item);
        } catch (IllegalArgumentException e) {
            return buildFallbackFromRssItem(item);
        } finally {
            retriever.release();
        }
    }

    private ContentMetaData buildFallbackFromRssItem(RssItem item) {
        return new ContentMetaData(null, item.getContent().getMimeType(), null, false, false);
    }

    private ContentMetaData buildMetaDataWithRetriever(MediaMetadataRetriever retriever, RssItem item) {
        String mimeType = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
        if (TextUtils.isEmpty(mimeType)) {
            mimeType = item.getContent().getMimeType();
        }
        boolean hasVideo = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO) != null;
        File thumbnailFile = null;
        if ((mimeType != null && mimeType.startsWith("video")) || hasVideo) {
            Bitmap thumbBitmap = retriever.getFrameAtTime();
            if (thumbBitmap != null) {
                try {
                    thumbnailFile = saveThumbnail(mContext, thumbBitmap, Bitmap.CompressFormat.PNG);
                } finally {
                    thumbBitmap.recycle();
                }
            }
        }
        String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        return new ContentMetaData(
                !TextUtils.isEmpty(duration) ? Long.parseLong(duration) : null,
                mimeType,
                thumbnailFile,
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO) != null,
                hasVideo);
    }

    private File downloadPodcastThumbnail(Context context, String url) {
        Response imageResponse;
        try {
            Request imageRequest = new Request.Builder().get().url(url).build();
            imageResponse = mHttpClient.newCall(imageRequest).execute();
            if (!imageResponse.isSuccessful()) {
                return null;
            }
        } catch (IOException e) {
            return null;
        }

        BufferedSink sink = null;
        try {
            String extension = MimeTypeMap.getFileExtensionFromUrl(url);
            String filename = String.format("%s.%s", UUID.randomUUID(), extension);
            File dataFile = new File(context.getDir("thumbnails", Context.MODE_PRIVATE), filename);
            sink = Okio.buffer(Okio.sink(dataFile));
            sink.writeAll(imageResponse.body().source());
            return dataFile;
        } catch (Exception e) {
            return null;
        } finally {
            try {
                if (sink != null) {
                    sink.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private File saveThumbnail(Context context, Bitmap thumbnail, Bitmap.CompressFormat compressFormat) {
        if (thumbnail == null) {
            throw new IllegalArgumentException("Null bitmap argument.");
        }

        FileOutputStream out = null;
        try {
            String filename = String.format("%s.%s", UUID.randomUUID(), compressFormat.toString().toLowerCase());
            File dataFile = new File(context.getDir("thumbnails", Context.MODE_PRIVATE) + filename);
            out = new FileOutputStream(dataFile);
            boolean bitmapCompressed = thumbnail.compress(compressFormat, 100, out);
            return bitmapCompressed ? dataFile : null;
        } catch (Exception e) {
            return null;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class ContentMetaData {
        private final Long duration;
        private final String mimeType;
        private final File thumbnailFrame;
        private final boolean hasAudio;
        private final boolean hasVideo;

        private ContentMetaData(Long duration, String mimeType, File thumbnailFrame, boolean hasAudio, boolean hasVideo) {
            this.duration = duration;
            this.mimeType = mimeType;
            this.thumbnailFrame = thumbnailFrame;
            this.hasAudio = hasAudio;
            this.hasVideo = hasVideo;
        }

        public Long getDuration() {
            return duration;
        }

        public String getMimeType() {
            return mimeType;
        }

        public File getThumbnailFrame() {
            return thumbnailFrame;
        }

        public boolean isHasAudio() {
            return hasAudio;
        }

        public boolean isHasVideo() {
            return hasVideo;
        }
    }
}
