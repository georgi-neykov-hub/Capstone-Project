package com.neykov.podcastportal.view.player.presenter;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.neykov.podcastportal.R;
import com.neykov.podcastportal.model.entity.Episode;
import com.neykov.podcastportal.model.entity.PlaylistEntry;
import com.neykov.podcastportal.view.base.adapter.BaseAdapter;
import com.neykov.podcastportal.view.base.adapter.BaseListenerViewHolder;
import com.neykov.podcastportal.view.base.adapter.OnItemClickListener;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;

public class PlaylistEntryAdapter extends BaseAdapter<PlaylistEntry, PlaylistEntryAdapter.EntryViewHolder> {

    private WeakReference<OnItemClickListener> mListenerRef;

    public void setListener(OnItemClickListener listener) {
        mListenerRef = new WeakReference<>(listener);
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public EntryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_playlist, parent, false);
        EntryViewHolder holder = new EntryViewHolder(itemView);
        holder.setListener(mProxyListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(EntryViewHolder holder, int position) {
        holder.onBind(getItem(position));
    }

    private final OnItemClickListener mProxyListener = new OnItemClickListener() {

        @Override
        public void onItemClick(int position) {
            OnItemClickListener listener = mListenerRef.get();
            if (listener != null) {
                listener.onItemClick(position);
            }
        }
    };

    protected static class EntryViewHolder extends BaseListenerViewHolder<OnItemClickListener> {

        private static final String NULL_DURATION_LABEL = "--:--";

        private ImageView mThumbnailView;
        private ImageView mWatchedIconView;
        private TextView mTitleTextView;
        private TextView mPodcastNameView;
        private TextView mLengthTextView;

        protected EntryViewHolder(View itemView) {
            super(itemView);
            mThumbnailView = (ImageView) itemView.findViewById(R.id.thumbnail);
            mWatchedIconView = (ImageView) itemView.findViewById(R.id.watchedIcon);
            mTitleTextView = (TextView) itemView.findViewById(R.id.title);
            mPodcastNameView = (TextView) itemView.findViewById(R.id.podcastName);
            mLengthTextView = (TextView) itemView.findViewById(R.id.length);
            itemView.findViewById(R.id.row).setOnClickListener(v -> {
                int position = getAdapterPosition();
                OnItemClickListener listener = getListener();
                if(position != RecyclerView.NO_POSITION && listener != null){
                    listener.onItemClick(position);
                }
            });
        }

        protected void onBind(PlaylistEntry entry) {
            Episode episode = entry.getEpisode();
            mTitleTextView.setText(episode.getTitle());
            mPodcastNameView.setText(entry.getPodcastTitle());
            mWatchedIconView.setVisibility(View.GONE);
            mLengthTextView.setText(getDurationLabel(episode));
            Picasso.with(mThumbnailView.getContext())
                    .load(episode.getThumbnail())
                    .fit()
                    .centerCrop()
                    .placeholder(R.color.photo_placeholder)
                    .into(mThumbnailView);
        }

        protected String getDurationLabel(Episode episode) {
            if (episode.getDuration() != null) {
                return DateUtils.formatElapsedTime(episode.getDuration() / 1000);
            } else {
                return NULL_DURATION_LABEL;
            }
        }
    }
}
