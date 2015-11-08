package com.neykov.podcastportal.playback;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.KeyEvent;
import android.view.Surface;

import com.neykov.podcastportal.model.LogHelper;
import com.neykov.podcastportal.model.entity.Episode;
import com.neykov.podcastportal.model.entity.PlaylistEntry;
import com.neykov.podcastportal.model.playlist.PlaylistManager;
import com.neykov.podcastportal.model.utils.ComponentService;
import com.neykov.podcastportal.view.player.PlayerActivity;

import java.lang.ref.WeakReference;

public class PlaybackService extends ComponentService implements Player.Callback {

    public static final String EXTRA_START_FULLSCREEN = "com.neykov.podcastportal.model.EXTRA_START_FULLSCREEN";
    public static final String EXTRA_CURRENT_MEDIA_DESCRIPTION = "com.neykov.podcastportal.model.CURRENT_MEDIA_DESCRIPTION";

    // The action of the incoming Intent indicating that it contains a command
    // to be executed (see {@link #onStartCommand})
    public static final String ACTION_CMD = "com.example.android.uamp.ACTION_CMD";
    // The key in the extras of the incoming Intent indicating the command that
    // should be executed (see {@link #onStartCommand})
    public static final String CMD_NAME = "CMD_NAME";
    // A value of a CMD_NAME key in the extras of the incoming Intent that
    // indicates that the music playback should be paused (see {@link #onStartCommand})
    public static final String CMD_PAUSE = "CMD_PAUSE";

    private static final String TAG = LogHelper.makeLogTag(PlaybackService.class);

    // Music catalog manager
    private PlaylistManager mPlaylistManager;
    // "Now playing" queue:
    private PlaylistEntry mCurrentPlayingItem;
    private MediaNotificationManager mMediaNotificationManager;
    // Indicates whether the service was started.
    private boolean mServiceStarted;
    private DelayedStopHandler mDelayedStopHandler;
    private PlayerImpl mPlayback;

    private MediaSessionCompat mSession;
    private MediaSessionCompat.Token mSessionToken;
    private PlaybackSessionBinder mServiceBinder;

    private OnVideoSizeChangedListener mVideoSizeListener;

    @Override
    public void onCreate() {
        super.onCreate();
        LogHelper.d(TAG, "onCreate");

        mDelayedStopHandler = new DelayedStopHandler(this);
        mServiceBinder = new PlaybackSessionBinder(this);

        // Start a new MediaSession

        mSession = new MediaSessionCompat(this, "PlaybackService", new ComponentName(this, MediaButtonReceiver.class), null);
        mSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mSession.setCallback(mSessionCallback);
        mSessionToken = mSession.getSessionToken();

        mPlaylistManager = getModelComponent().getPlaylistManager();

        mPlayback = new PlayerImpl(this);
        mPlayback.setCallback(this);
        mPlayback.start();

        Context context = getApplicationContext();
        ComponentName playerComponentName = new ComponentName(context, PlayerActivity.class);
        Intent intent = new Intent().setComponent(playerComponentName);
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 99,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mSession.setSessionActivity(pi);
        mMediaNotificationManager = new MediaNotificationManager(this, playerComponentName);
        updatePlaybackState(null);
    }

    /**
     * (non-Javadoc)
     *
     * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
     */
    @Override
    public int onStartCommand(Intent startIntent, int flags, int startId) {
        if (startIntent != null) {
            String action = startIntent.getAction();
            String command = startIntent.getStringExtra(CMD_NAME);
            if (ACTION_CMD.equals(action)) {
                if (CMD_PAUSE.equals(command)) {
                    if (mPlayback != null && mPlayback.isPlaying()) {
                        handlePauseRequest();
                    }
                }
            } else if (Intent.ACTION_MEDIA_BUTTON.equals(action)) {
                MediaButtonReceiver.handleIntent(mSession, startIntent);
            }
        }
        // Reset the delay handler to enqueue a message to stop the service if
        // nothing is playing.
        mDelayedStopHandler.dispatchDelayedStop();
        return START_STICKY;
    }

    /**
     * (non-Javadoc)
     *
     * @see android.app.Service#onDestroy()
     */
    @Override
    public void onDestroy() {
        LogHelper.d(TAG, "onDestroy");
        // Service is being killed, so make sure we release our resources
        handleStopRequest(null);

        mDelayedStopHandler.clearDispatchedStops();
        // Always release the MediaSession to clean up resources
        // and notify associated MediaController(s).
        mSession.release();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mServiceBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        setVideoPlaybackSurface(null);
        return super.onUnbind(intent);
    }

    public MediaSessionCompat.Token getSessionToken() {
        return mSessionToken;
    }

    public MediaSessionCompat getMediaSession() {
        return mSession;
    }

    private void setVideoPlaybackSurface(Surface surface) {
        mPlayback.setVideoPlaybackSurface(surface);
    }

    private void setOnVideoSizeChangedListener(OnVideoSizeChangedListener listener) {
        mVideoSizeListener = listener;
    }

    /**
     * Handle a request to play music
     */
    private void handlePlayRequest(PlaylistEntry entry) {
        LogHelper.d(TAG, "handlePlayRequest: mState=" + mPlayback.getPlaybackState());

        mDelayedStopHandler.clearDispatchedStops();
        if (!mServiceStarted) {
            LogHelper.v(TAG, "Starting service");
            // The PlaybackService needs to keep running even after the calling MediaBrowser
            // is disconnected. Call startService(Intent) and then stopSelf(..) when we no longer
            // need to play media.
            startService(new Intent(getApplicationContext(), PlaybackService.class));
            mServiceStarted = true;
        }

        if (!mSession.isActive()) {
            mSession.setActive(true);
        }

        mCurrentPlayingItem = entry;
        updateSessionMetadata(entry);
        mPlayback.play(entry.getEpisode());
    }

    /**
     * Handle a request to pause music
     */
    private void handlePauseRequest() {
        LogHelper.d(TAG, "handlePauseRequest: mState=" + mPlayback.getPlaybackState());
        mPlayback.pause();
        // reset the delayed stop handler.
        mDelayedStopHandler.dispatchDelayedStop();
    }

    /**
     * Handle a request to stop music
     */
    private void handleStopRequest(String withError) {
        LogHelper.d(TAG, "handleStopRequest: mState=" + mPlayback.getPlaybackState() + " error=", withError);
        mPlayback.stop(true);
        // reset the delayed stop handler.
        mDelayedStopHandler.dispatchDelayedStop();

        updatePlaybackState(withError);

        // service is no longer necessary. Will be started again if needed.
        stopSelf();
        mServiceStarted = false;
    }

    private void updateSessionMetadata(PlaylistEntry entry) {
        Episode episode = entry.getEpisode();
        MediaMetadataCompat mediaData = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, episode.getTitle())
                .putString(MediaMetadataCompat.METADATA_KEY_AUTHOR, entry.getPodcastTitle())
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, episode.getDuration())
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, String.valueOf(entry.getId()))
                .putString(MediaMetadataCompat.METADATA_KEY_ART_URI, episode.getThumbnail())
                .putString(MediaMetadataCompat.METADATA_KEY_GENRE, episode.getMimeType())
                .build();
        mSession.setMetadata(mediaData);
    }

    /**
     * Update the current media player state, optionally showing an error message.
     *
     * @param error if not null, error message to present to the user.
     */
    private void updatePlaybackState(String error) {
        LogHelper.d(TAG, "updatePlaybackState, playback state=" + mPlayback.getPlaybackState());
        long position = PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN;
        if (mPlayback != null) {
            position = mPlayback.getCurrentStreamPosition();
        }

        //noinspection ResourceType
        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(getAvailableActions());

        @PlaybackStateCompat.State final int state;
        // If there is an error message, send it to the playback state:
        if (error != null) {
            // Error states are really only supposed to be used for errors that cause playback to
            // stop unexpectedly and persist until the user takes action to fix it.
            stateBuilder.setErrorMessage(error);
            state = PlaybackStateCompat.STATE_ERROR;
        } else {
            state = mPlayback.getPlaybackState();
        }
        //noinspection ResourceType
        stateBuilder.setState(state, position, 1.0f, SystemClock.elapsedRealtime());
        stateBuilder.setActiveQueueItemId(getCurrentQueueItemId());
        mSession.setPlaybackState(stateBuilder.build());

        if (state == PlaybackStateCompat.STATE_PLAYING || state == PlaybackStateCompat.STATE_PAUSED) {
            mMediaNotificationManager.startNotification();
        }
    }

    private
    @PlaybackStateCompat.Actions
    long getAvailableActions() {
        long actions = PlaybackStateCompat.ACTION_PLAY |
                PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID |
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM;

        if (mPlayback.isPlaying()) {
            actions |= PlaybackStateCompat.ACTION_PAUSE;
        }
        return actions;
    }


    private long getCurrentQueueItemId() {
        return mCurrentPlayingItem != null ?
                mCurrentPlayingItem.getId() : MediaSessionCompat.QueueItem.UNKNOWN_ID;
    }

    /**
     * Implementation of the Playback.Callback interface
     */
    @Override
    public void onCompletion() {
        if (mCurrentPlayingItem != null) {
            mSessionCallback.onSkipToNext();
        } else {
            handleStopRequest(null);
        }
    }

    @Override
    public void onPlaybackStatusChanged(int state) {
        updatePlaybackState(null);
    }

    @Override
    public void onError(String error) {
        updatePlaybackState(error);
    }

    @Override
    public void onMetadataChanged(String mediaId) {
    }

    @Override
    public void onVideoSizeChanged(int width, int height) {
        if (mVideoSizeListener != null) {
            mVideoSizeListener.onVideoSizeChanged(width, height);
        }
    }

    /**
     * A simple handler that stops the service if playback is not active (playing)
     */
    private static class DelayedStopHandler extends Handler {

        // Delay stopSelf by using a handler.
        private static final int STOP_DELAY_MS = 30000;

        private final WeakReference<PlaybackService> mWeakReference;

        private DelayedStopHandler(PlaybackService service) {
            mWeakReference = new WeakReference<>(service);
        }

        private void clearDispatchedStops() {
            removeCallbacksAndMessages(null);
        }

        private void dispatchDelayedStop() {
            clearDispatchedStops();
            sendEmptyMessageDelayed(0, STOP_DELAY_MS);
        }

        @Override
        public void handleMessage(Message msg) {
            PlaybackService service = mWeakReference.get();
            if (service != null && service.mPlayback != null) {
                if (service.mPlayback.isPlaying()) {
                    LogHelper.d(TAG, "Ignoring delayed stop since the media player is in use.");
                    return;
                }
                LogHelper.d(TAG, "Stopping service with delay handler.");
                service.stopSelf();
                service.mServiceStarted = false;
            }
        }

    }

    private static class PlaybackSessionBinder extends Binder implements com.neykov.podcastportal.playback.PlaybackSession {

        private WeakReference<PlaybackService> mServiceRef;

        private PlaybackSessionBinder(@NonNull PlaybackService service) {
            mServiceRef = new WeakReference<>(service);
        }

        @Override
        public MediaSessionCompat.Token getMediaSessionToken() {
            return mServiceRef.get().getMediaSession().getSessionToken();
        }

        @Override
        public void setVideoPlaybackSurface(Surface surface) {
            mServiceRef.get().setVideoPlaybackSurface(surface);
        }

        @Override
        public void setOnVideoSizeChangedListener(OnVideoSizeChangedListener listener) {
            mServiceRef.get().setOnVideoSizeChangedListener(listener);
        }
    }

    private final MediaSessionCompat.Callback mSessionCallback = new MediaSessionCompat.Callback() {

        @Override
        public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
            if (Intent.ACTION_MEDIA_BUTTON.equals(mediaButtonEvent.getAction())) {
                final KeyEvent event = mediaButtonEvent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                if (event == null) return super.onMediaButtonEvent(mediaButtonEvent);
                final int keycode = event.getKeyCode();
                final int action = event.getAction();
                if (event.getRepeatCount() == 0 && action == KeyEvent.ACTION_DOWN) {
                    MediaControllerCompat.TransportControls controls = mSession.getController().getTransportControls();
                    switch (keycode) {
                        case KeyEvent.KEYCODE_MEDIA_STOP:
                            controls.stop();
                            break;
                        case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                            if (mPlayback.isPlaying()) {
                                controls.pause();
                            } else {
                                controls.play();
                            }
                            break;
                        case KeyEvent.KEYCODE_MEDIA_NEXT:
                            controls.skipToNext();
                            break;
                        case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                            controls.skipToPrevious();
                            break;
                        case KeyEvent.KEYCODE_MEDIA_PAUSE:
                            controls.pause();
                            break;
                        case KeyEvent.KEYCODE_MEDIA_PLAY:
                            controls.play();
                            break;
                        default:
                            return false;
                    }

                    return true;
                }
            }
            return super.onMediaButtonEvent(mediaButtonEvent);
        }

        @Override
        public void onPlay() {
            LogHelper.d(TAG, "play");

            if (mCurrentPlayingItem != null) {
                handlePlayRequest(mCurrentPlayingItem);
            } else {
                PlaylistEntry firstPLaylistEntry = mPlaylistManager.getFirstItem();
                if (firstPLaylistEntry != null) {
                    handlePlayRequest(firstPLaylistEntry);
                }
            }
        }

        @Override
        public void onSkipToQueueItem(long queueId) {
            LogHelper.d(TAG, "OnSkipToQueueItem:" + queueId);

            PlaylistEntry playlistEntry = mPlaylistManager.getItem(queueId);
            if (playlistEntry != null) {
                handlePlayRequest(playlistEntry);
            }
        }

        @Override
        public void onSeekTo(long position) {
            LogHelper.d(TAG, "onSeekTo:", position);
            mPlayback.seekTo((int) position);
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            LogHelper.d(TAG, "playFromMediaId mediaId:", mediaId, "  extras=", extras);

            long playlistId = Long.parseLong(mediaId);
            PlaylistEntry playlistEntry = mPlaylistManager.getItem(playlistId);
            if (playlistEntry != null) {
                handlePlayRequest(playlistEntry);
            }
        }

        @Override
        public void onPause() {
            LogHelper.d(TAG, "pause. current state=" + mPlayback.getPlaybackState());
            handlePauseRequest();
        }

        @Override
        public void onStop() {
            LogHelper.d(TAG, "stop. current state=" + mPlayback.getPlaybackState());
            handleStopRequest(null);
        }

        @Override
        public void onSkipToNext() {
            LogHelper.d(TAG, "skipToNext");
            PlaylistEntry itemToPlay;
            if (mCurrentPlayingItem != null && mCurrentPlayingItem.getNextItemId() != null) {
                itemToPlay = mPlaylistManager.getItem(mCurrentPlayingItem.getNextItemId());
            } else {
                itemToPlay = mPlaylistManager.getFirstItem();
            }

            if (itemToPlay != null) {
                handlePlayRequest(itemToPlay);
            } else {
                handleStopRequest("Cannot skip");
            }
        }

        @Override
        public void onSkipToPrevious() {
            LogHelper.d(TAG, "skipToPrevious");
            PlaylistEntry itemToPlay;
            if (mCurrentPlayingItem != null && mCurrentPlayingItem.getPreviousItemId() != null) {
                itemToPlay = mPlaylistManager.getItem(mCurrentPlayingItem.getPreviousItemId());
            } else {
                itemToPlay = mPlaylistManager.getFirstItem();
            }

            if (itemToPlay != null) {
                handlePlayRequest(itemToPlay);
            } else {
                handleStopRequest("Cannot skip");
            }
        }
    };
}
