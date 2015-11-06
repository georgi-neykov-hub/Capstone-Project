package com.neykov.podcastportal.model.downloads;

import android.app.DownloadManager;
import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;
import android.util.Log;

import com.neykov.podcastportal.model.entity.Episode;
import com.neykov.podcastportal.model.entity.converter.EpisodesConverter;
import com.neykov.podcastportal.model.persistence.DatabaseContract;

import java.util.ArrayList;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class DownloadIntentService extends IntentService {

    private static final String TAG = DownloadIntentService.class.getSimpleName();

    public DownloadIntentService() {
        super(TAG);
    }

    private DownloadManager mDownloadManager;
    private EpisodesConverter mEpisodeConverter;

    @Override
    public void onCreate() {
        super.onCreate();
        mDownloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        mEpisodeConverter = new EpisodesConverter();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                handleDownloadStatusUpdate(downloadId);
            }
        }
    }

    private void handleDownloadStatusUpdate(long downloadId) {
        try {
            Download downloadData = getDownloadData(downloadId);
            Episode episode = matchEpisodeForDownload(downloadId);
            updateEpisodeDownloadState(downloadData, episode);
        } catch (IllegalArgumentException | RemoteException | OperationApplicationException e) {
            Log.w(TAG, "Failed to handle download status update.", e);
        }
    }

    private Episode matchEpisodeForDownload(long downloadId) {
        Cursor episodeCursor = null;
        try {
            episodeCursor = getApplicationContext()
                    .getContentResolver()
                    .query(
                            DatabaseContract.Episode.CONTENT_URI, null,
                            DatabaseContract.Episode.DOWNLOAD_ID + "=?"
                            , new String[]{String.valueOf(downloadId)}, null);
            if (episodeCursor == null || episodeCursor.getCount() == 0) {
                throw new IllegalArgumentException("No matching episode with this download id was found.");
            }
            episodeCursor.moveToFirst();
            return mEpisodeConverter.convert(episodeCursor);
        } finally {
            if (episodeCursor != null) {
                episodeCursor.close();
            }
        }
    }

    private void updateEpisodeDownloadState(Download download, Episode episode) throws RemoteException, OperationApplicationException {
        long episodeId = episode.getId();
        int lastDownloadedState = episode.getDownloadState();
        ContentProviderOperation.Builder builder = ContentProviderOperation
                .newUpdate(DatabaseContract.Episode.buildItemUri(episodeId))
                .withValue(DatabaseContract.Episode.PODCAST_ID, episode.getPodcastId())
                .withExpectedCount(1);
        int newDownloadState;
        switch (download.getStatus()) {
            case DownloadManager.STATUS_PENDING:
            case DownloadManager.STATUS_RUNNING:
            case DownloadManager.STATUS_PAUSED:
                newDownloadState = DatabaseContract.Episode.DOWNLOADING;
                break;
            case DownloadManager.STATUS_FAILED:
                newDownloadState = DatabaseContract.Episode.REMOTE;
                builder.withValue(DatabaseContract.Episode.DOWNLOAD_ID, null);
                break;
            case DownloadManager.STATUS_SUCCESSFUL:
                newDownloadState = DatabaseContract.Episode.DOWNLOADED;
                builder.withValue(DatabaseContract.Episode.FILE_URL, download.getLocalFilePath());
                builder.withValue(DatabaseContract.Episode.FILE_SIZE, download.getTotalSizeBytes());
                break;
            default:
                throw new AssertionError("Unknown DownloadManager download state.");
        }
        if (lastDownloadedState != newDownloadState) {
            builder.withValue(DatabaseContract.Episode.DOWNLOAD_STATE, newDownloadState);
        }

        ArrayList<ContentProviderOperation> ops = new ArrayList<>(1);
        ops.add(builder.build());
        getApplicationContext().getContentResolver()
                .applyBatch(DatabaseContract.CONTENT_AUTHORITY, ops);
    }

    private Download getDownloadData(long downloadId) {
        Cursor dataCursor = null;
        try {
            dataCursor = mDownloadManager.query(new DownloadManager.Query().setFilterById(downloadId));
            dataCursor.moveToFirst();
            return createFromCursor(dataCursor);
        } finally {
            if (dataCursor != null) {
                dataCursor.close();
            }
        }
    }

    private Download createFromCursor(Cursor downloadCursor) {
        return new Download(
                downloadCursor.getLong(downloadCursor.getColumnIndex(DownloadManager.COLUMN_ID)),
                downloadCursor.getString(downloadCursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)),
                downloadCursor.getInt(downloadCursor.getColumnIndex(DownloadManager.COLUMN_STATUS)),
                downloadCursor.getLong(downloadCursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)),
                downloadCursor.getLong(downloadCursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)));
    }

}
