package com.neykov.podcastportal.view.player.view;

import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.neykov.podcastportal.R;
import com.neykov.podcastportal.model.playback.OnVideoSizeChangedListener;
import com.neykov.podcastportal.model.playback.PlaybackSession;
import com.neykov.podcastportal.view.base.fragment.BaseViewFragment;
import com.neykov.podcastportal.view.player.presenter.PlayerSlidingViewPresenter;
import com.squareup.picasso.Picasso;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class PlayerSlidingFragment extends BaseViewFragment<PlayerSlidingViewPresenter> implements PlayerSlidingView {

    private static final long PROGRESS_UPDATE_INTERNAL = 1000;
    private static final long PROGRESS_UPDATE_INITIAL_INTERVAL = 100;

    private ImageButton mPlayPauseButton;
    private ImageButton mNextButton;
    private ImageButton mPreviousButton;
    private ImageButton mFastForwardButton;
    private ImageButton mRewindButton;
    private SeekBar mSeekBar;
    private View mLoadingView;
    private TextView mElapsedTextView;
    private TextView mRemainingTextView;
    private View mPlaybackControlsView;
    private TextureView mVideoView;
    private TextView mTitleTextView;
    private TextView mPodcastNameTextView;
    private ImageView mFullArtImageView;
    private ImageView mThumbnailArtImageView;

    private Drawable mPauseDrawable;
    private Drawable mPlayDrawable;
    private Drawable mVideoBackgroundDrawable;

    private PlaybackSession mPlaybackSession;
    private MediaControllerCompat mMediaController;
    private PlaybackStateCompat mLastPlaybackState;

    private ScheduledExecutorService mExecutorService;
    private ScheduledFuture<?> mScheduleFuture;
    private Handler mHandler;

    private boolean mVideoAttached;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mHandler = new Handler(Looper.getMainLooper());
        mExecutorService = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
        mExecutorService.shutdown();
        mExecutorService = null;
        mScheduleFuture = null;
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_player_sliding_panel, container, false);
        initializeViewReferences(rootView);
        setEventListeners();
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPlayPauseButton = null;
        mNextButton = null;
        mPreviousButton = null;
        mFastForwardButton = null;
        mRewindButton = null;
        mSeekBar = null;
        mLoadingView = null;
        mElapsedTextView = null;
        mRemainingTextView = null;
        mPlaybackControlsView = null;
        mVideoView.setSurfaceTextureListener(null);
        mVideoView = null;
        mPauseDrawable = null;
        mPlayDrawable = null;
        mVideoBackgroundDrawable = null;
    }

    private void initializeViewReferences(View rootView) {
        mPlayPauseButton = (ImageButton) rootView.findViewById(R.id.play);
        mNextButton = (ImageButton) rootView.findViewById(R.id.fastForwardEnd);
        mPreviousButton = (ImageButton) rootView.findViewById(R.id.rewindStart);
        mFastForwardButton = (ImageButton) rootView.findViewById(R.id.fastForward);
        mRewindButton = (ImageButton) rootView.findViewById(R.id.rewind);
        mSeekBar = (SeekBar) rootView.findViewById(R.id.seekBar);
        mLoadingView = rootView.findViewById(R.id.loadingIndicator);
        mElapsedTextView = (TextView) rootView.findViewById(R.id.elapsed);
        mRemainingTextView = (TextView) rootView.findViewById(R.id.remaining);
        mPlaybackControlsView = rootView.findViewById(R.id.playerControls);
        mVideoView = (TextureView) rootView.findViewById(R.id.videoContainer);
        mVideoView.setWillNotDraw(false);
        mTitleTextView = (TextView) rootView.findViewById(R.id.title);
        mPodcastNameTextView = (TextView) rootView.findViewById(R.id.podcastName);
        mFullArtImageView = (ImageView) rootView.findViewById(R.id.artwork);
        mThumbnailArtImageView = (ImageView) rootView.findViewById(R.id.thumbnail);
        mPauseDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_pause);
        mPlayDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_play);
        mVideoBackgroundDrawable = new ColorDrawable(Color.BLACK);
    }

    @NonNull
    @Override
    protected PlayerSlidingViewPresenter onCreatePresenter() {
        return getDependencyResolver().getPlayerComponent()
                .createPlayerSlidingViewPresenter();
    }

    @Override
    public void onConnected(PlaybackSession playbackSession) {
        mPlaybackSession = playbackSession;
        try {
            mMediaController = new MediaControllerCompat(getContext(), playbackSession.getMediaSessionToken());
        } catch (RemoteException e) {
            throw new RuntimeException("Cannot create MediaController.", e);
        }
        mMediaController.registerCallback(mMediaControllerCallback);
        PlaybackStateCompat state = mMediaController.getPlaybackState();
        updatePlaybackState(state);
        updateProgress();

        attachVideoSurface();

        MediaMetadataCompat metadata = mMediaController.getMetadata();
        if (metadata != null) {
            updateDuration(metadata);
            updateMediaDescription(metadata);
        }
    }

    @Override
    public void onDisconnected() {
        detachVideoSurface();
        mPlaybackSession = null;
        mMediaController.unregisterCallback(mMediaControllerCallback);
        mMediaController = null;
        mLastPlaybackState = null;
    }

    private void setEventListeners() {
        mPlayPauseButton.setOnClickListener(v -> {
            MediaControllerCompat.TransportControls controls = mMediaController.getTransportControls();
            @PlaybackStateCompat.State int state = mMediaController.getPlaybackState().getState();
            if (state == PlaybackStateCompat.STATE_PLAYING ||
                    state == PlaybackStateCompat.STATE_BUFFERING) {
                controls.pause();
            } else {
                controls.play();
            }
        });

        mPreviousButton.setOnClickListener(v -> mMediaController.getTransportControls().skipToPrevious());
        mNextButton.setOnClickListener(v -> mMediaController.getTransportControls().skipToNext());
        mRewindButton.setOnClickListener(v -> mMediaController.getTransportControls().rewind());
        mFastForwardButton.setOnClickListener(v -> mMediaController.getTransportControls().fastForward());

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                long remaining = seekBar.getMax() - progress;
                mElapsedTextView.setText(DateUtils.formatElapsedTime(progress / 1000));
                mRemainingTextView.setText(DateUtils.formatElapsedTime(remaining / 1000));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                stopSeekbarUpdate();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mMediaController.getTransportControls().seekTo(seekBar.getProgress());
                seekBar.setEnabled(false);
            }
        });

        mVideoView.setSurfaceTextureListener(mSurfaceListener);
    }

    private void toggleControls(boolean enabled) {
        mPlayPauseButton.setEnabled(enabled);
        mNextButton.setEnabled(enabled);
        mPreviousButton.setEnabled(enabled);
        mFastForwardButton.setEnabled(enabled);
        mRewindButton.setEnabled(enabled);
        mSeekBar.setEnabled(enabled);
    }

    private void stopSeekbarUpdate() {
        if (mScheduleFuture != null) {
            mScheduleFuture.cancel(false);
        }
    }

    private void scheduleSeekbarUpdate() {
        stopSeekbarUpdate();
        if (!mExecutorService.isShutdown()) {
            final Runnable mUpdateProgressTask = this::updateProgress;
            mScheduleFuture = mExecutorService.scheduleAtFixedRate(
                    () -> mHandler.post(mUpdateProgressTask), PROGRESS_UPDATE_INITIAL_INTERVAL,
                    PROGRESS_UPDATE_INTERNAL, TimeUnit.MILLISECONDS);
        }
    }

    private void attachVideoSurface() {
        if (!mVideoAttached && mPlaybackSession != null && mVideoView.getSurfaceTexture() != null) {
            mPlaybackSession.setOnVideoSizeChangedListener(mVideoSizeChangedListener);
            mPlaybackSession.setVideoPlaybackSurface(new Surface(mVideoView.getSurfaceTexture()));
            mVideoAttached = true;
        }
    }

    private void detachVideoSurface() {
        if (mVideoAttached && mPlaybackSession != null) {
            mPlaybackSession.setOnVideoSizeChangedListener(null);
            mPlaybackSession.setVideoPlaybackSurface(null);
            mVideoAttached = false;
        }
    }

    private void updateMediaDescription(MediaMetadataCompat metadata) {
        MediaDescriptionCompat description = metadata.getDescription();
        if (description == null) {
            return;
        }
        mTitleTextView.setText(description.getTitle());
        mPodcastNameTextView.setText(description.getSubtitle());
        Picasso.with(getContext()).cancelRequest(mThumbnailArtImageView);
        Picasso.with(getContext())
                .load(description.getIconUri())
                .fit()
                .centerCrop()
                .placeholder(R.color.photo_placeholder)
                .into(mThumbnailArtImageView);

        String mimeType = metadata.getString(MediaMetadataCompat.METADATA_KEY_GENRE);
        boolean mediaHasVideo = mimeType != null && mimeType.startsWith("video");
        if (mediaHasVideo) {
            mVideoView.setVisibility(View.VISIBLE);
            mFullArtImageView.setImageResource(android.R.color.black);
        } else {
            mVideoView.setVisibility(View.GONE);
            Picasso.with(getContext()).cancelRequest(mFullArtImageView);
            Picasso.with(getContext())
                    .load(description.getIconUri())
                    .fit()
                    .centerInside()
                    .placeholder(R.color.photo_placeholder)
                    .into(mFullArtImageView);
        }
    }

    private void updateDuration(MediaMetadataCompat metadata) {
        long duration = metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
        mSeekBar.setMax((int) duration);
    }

    private void updatePlaybackState(PlaybackStateCompat state) {
        if (state == null) {
            return;
        }
        mLastPlaybackState = state;
        updateProgress();

        switch (state.getState()) {
            case PlaybackStateCompat.STATE_PLAYING:
                mLoadingView.setVisibility(View.INVISIBLE);
                mPlayPauseButton.setEnabled(true);
                mPlayPauseButton.setImageDrawable(mPauseDrawable);
                mPlaybackControlsView.setVisibility(View.VISIBLE);
                mSeekBar.setEnabled(true);
                scheduleSeekbarUpdate();
                break;
            case PlaybackStateCompat.STATE_PAUSED:
                mPlaybackControlsView.setVisibility(View.VISIBLE);
                mLoadingView.setVisibility(View.INVISIBLE);
                mPlayPauseButton.setEnabled(true);
                mSeekBar.setEnabled(true);
                mPlayPauseButton.setImageDrawable(mPlayDrawable);
                stopSeekbarUpdate();
                break;
            case PlaybackStateCompat.STATE_ERROR:
            case PlaybackStateCompat.STATE_NONE:
            case PlaybackStateCompat.STATE_STOPPED:
                mLoadingView.setVisibility(View.INVISIBLE);
                mPlayPauseButton.setEnabled(true);
                mPlayPauseButton.setImageDrawable(mPlayDrawable);
                mSeekBar.setEnabled(false);
                mSeekBar.setProgress(0);
                stopSeekbarUpdate();
                break;
            case PlaybackStateCompat.STATE_BUFFERING:
                mPlayPauseButton.setEnabled(false);
                mPlayPauseButton.setImageDrawable(mPauseDrawable);
                mLoadingView.setVisibility(View.VISIBLE);
                mSeekBar.setEnabled(false);
                stopSeekbarUpdate();
                break;
        }
    }

    private void updateProgress() {
        if (mLastPlaybackState == null) {
            return;
        }
        long currentPosition = mLastPlaybackState.getPosition();
        if (mLastPlaybackState.getState() != PlaybackStateCompat.STATE_PAUSED) {
            // Calculate the elapsed time between the last position update and now and unless
            // paused, we can assume (delta * speed) + current position is approximately the
            // latest position. This ensure that we do not repeatedly call the getPlaybackState()
            // on MediaController.
            long timeDelta = SystemClock.elapsedRealtime() -
                    mLastPlaybackState.getLastPositionUpdateTime();
            currentPosition += (int) timeDelta * mLastPlaybackState.getPlaybackSpeed();
        }
        mSeekBar.setProgress((int) currentPosition);
    }

    private MediaControllerCompat.Callback mMediaControllerCallback = new MediaControllerCompat.Callback() {

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            updatePlaybackState(state);
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            if (metadata != null) {
                updateMediaDescription(metadata);
                updateDuration(metadata);
            }
        }
    };

    private OnVideoSizeChangedListener mVideoSizeChangedListener = new OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(int width, int height) {
            if (width != 0 || height != 0) {
                mFullArtImageView.setImageDrawable(mVideoBackgroundDrawable);
                mVideoView.setVisibility(View.VISIBLE);
                float aspectRatio = width / (float) height;
                int frameWidth = mVideoView.getWidth();
                int desiredHeight = (int) (frameWidth / aspectRatio);
                ViewGroup.LayoutParams params = mVideoView.getLayoutParams();
                params.height = desiredHeight;
                mVideoView.requestLayout();
            }
        }
    };

    private TextureView.SurfaceTextureListener mSurfaceListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
            attachVideoSurface();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            detachVideoSurface();
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };
}
