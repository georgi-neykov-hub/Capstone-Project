package com.neykov.podcastportal.view.subscriptions.presenter;

import com.neykov.podcastportal.model.entity.Episode;
import com.neykov.podcastportal.model.entity.PodcastSubscription;

import java.util.List;

class SubscriptionAdapterItem {

    public interface EpisodeItemListener{
        void onAddToPlaylistTop(Episode episode);
        void onAddToPlaylistEnd(Episode episode);
        void onRemoveFromPlaylist(Episode episode);
        void onDownload(Episode episode);
        void onSelected(Episode episode);
    }

    private PodcastSubscription mPodcastSubscription;
    private NestedEpisodeAdapter mNestedEpisodeAdapter;
    private EpisodeItemListener mListener;

    public SubscriptionAdapterItem(PodcastSubscription podcastSubscription) {
        this.mPodcastSubscription = podcastSubscription;
        this.mNestedEpisodeAdapter = new NestedEpisodeAdapter();
        mNestedEpisodeAdapter.setListener(mAdapterItemListener);
    }

    public NestedEpisodeAdapter getAdapter() {
        return mNestedEpisodeAdapter;
    }

    public PodcastSubscription getSubscription() {
        return mPodcastSubscription;
    }

    public void setItemListener(EpisodeItemListener listener){
        mListener = listener;
    }

    private final NestedEpisodeAdapter.EpisodeItemListener mAdapterItemListener = new NestedEpisodeAdapter.EpisodeItemListener() {
        @Override
        public void onItemClick(int position) {
            if(mListener != null){
                mListener.onSelected(getAdapter().getItem(position));
            }
        }

        @Override
        public void onDownloadClick(int position) {
            if(mListener != null){
                mListener.onDownload(getAdapter().getItem(position));
            }
        }

        @Override
        public void onAddPlaylistTopClick(int position) {
            if (mListener != null) {
                mListener.onAddToPlaylistTop(getAdapter().getItem(position));
            }
        }

        @Override
        public void onAddPlaylistEndClick(int position) {
            if (mListener != null) {
                mListener.onAddToPlaylistEnd(getAdapter().getItem(position));
            }
        }

        @Override
        public void onRemoveFromPlaylistClick(int position) {
            if (mListener != null) {
                mListener.onRemoveFromPlaylist(getAdapter().getItem(position));
            }
        }
    };
}
