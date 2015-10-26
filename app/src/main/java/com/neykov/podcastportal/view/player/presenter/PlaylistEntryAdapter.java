package com.neykov.podcastportal.view.player.presenter;

import android.support.v7.widget.helper.ItemTouchHelper;
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

public class PlaylistEntryAdapter extends BaseAdapter<PlaylistEntry, PlaylistEntryAdapter.EntryViewHolder> {


    @Override
    public EntryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_playlist, parent, false);
        return new EntryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(EntryViewHolder holder, int position) {
        holder.onBind(getItem(position));
    }


    protected static class EntryViewHolder extends BaseListenerViewHolder<Void>{

        private static final String NULL_LENGTH_LABEL = "--:--";

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
            mLengthTextView  = (TextView) itemView.findViewById(R.id.length);
        }

        protected void onBind(PlaylistEntry entry){
            Episode episode = entry.getEpisode();
            mTitleTextView.setText(episode.getTitle());
            mPodcastNameView.setText(entry.getPodcastTitle());
            mWatchedIconView.setVisibility(View.GONE);
            mLengthTextView.setText(episode.getMediaLength() != null ?
                    episode.getMediaLength() : NULL_LENGTH_LABEL);
        }
    }
}
