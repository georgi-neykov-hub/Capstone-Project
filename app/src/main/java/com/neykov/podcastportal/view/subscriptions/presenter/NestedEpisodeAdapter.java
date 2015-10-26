package com.neykov.podcastportal.view.subscriptions.presenter;

import android.support.v7.internal.view.SupportMenuInflater;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.neykov.podcastportal.R;
import com.neykov.podcastportal.model.entity.Episode;
import com.neykov.podcastportal.view.base.adapter.BaseAdapter;
import com.neykov.podcastportal.view.base.adapter.BaseListenerViewHolder;
import com.neykov.podcastportal.view.base.adapter.OnItemClickListener;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.List;

public class NestedEpisodeAdapter extends BaseAdapter<Episode, NestedEpisodeAdapter.EpisodeViewHolder> {

    public interface EpisodeItemListener extends OnItemClickListener{
        void onDownloadClick(int position);
        void onAddPlaylistTopClick(int position);
        void onAddPlaylistEndClick(int position);
        void onRemoveFromPlaylistClick(int position);
    }

    private String mDefaultThumbnailUrl;
    private int mLastScrollPositionX;

    private WeakReference<EpisodeItemListener> mListenerRef;

    public void setListener(EpisodeItemListener listener){
        mListenerRef = new WeakReference<>(listener);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recyclerView.scrollTo(mLastScrollPositionX, 0);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        mLastScrollPositionX = recyclerView.getScrollX();
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public EpisodeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.list_item_episode_lite, parent, false);
        EpisodeViewHolder holder = new EpisodeViewHolder(itemView);
        holder.setListener(mProxyListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(EpisodeViewHolder holder, int position) {
        holder.onBind(getItem(position), mDefaultThumbnailUrl);
    }

    public void setDefaultThumbnail(String url){
        mDefaultThumbnailUrl = url;
    }

    private EpisodeItemListener mProxyListener = new EpisodeItemListener() {
        @Override
        public void onDownloadClick(int position) {
            EpisodeItemListener listener = mListenerRef.get();
            if(listener != null){
                listener.onDownloadClick(position);
            }
        }

        @Override
        public void onAddPlaylistTopClick(int position) {
            EpisodeItemListener listener = mListenerRef.get();
            if(listener != null){
                listener.onAddPlaylistTopClick(position);
            }
        }

        @Override
        public void onAddPlaylistEndClick(int position) {
            EpisodeItemListener listener = mListenerRef.get();
            if(listener != null){
                listener.onAddPlaylistEndClick(position);
            }
        }

        @Override
        public void onRemoveFromPlaylistClick(int position) {
            EpisodeItemListener listener = mListenerRef.get();
            if(listener != null){
                listener.onRemoveFromPlaylistClick(position);
            }
        }

        @Override
        public void onItemClick(int position) {
            EpisodeItemListener listener = mListenerRef.get();
            if(listener != null){
                listener.onItemClick(position);
            }
        }
    };

    protected static class EpisodeViewHolder extends BaseListenerViewHolder<EpisodeItemListener>{

        private ImageView mThumbnailView;
        private TextView mTitleTextView;
        private MenuItem mDownloadMenuItem;
        private MenuItem mPlaylistAddTopItem;
        private MenuItem mPlaylistAddEndItem;
        private MenuItem mPlaylistRemoveItem;

        protected EpisodeViewHolder(View itemView) {
            super(itemView);
            mThumbnailView = (ImageView) itemView.findViewById(R.id.thumbnail);
            mTitleTextView = (TextView) itemView.findViewById(R.id.title);

            ActionMenuView menuView = (ActionMenuView) itemView.findViewById(R.id.menu);
            menuView.setOnMenuItemClickListener(mMenuItemCLickListener);
            Menu itemMenu = menuView.getMenu();
            new SupportMenuInflater(menuView.getContext()).inflate(R.menu.menu_podcast_listitem_lite, itemMenu);
            mPlaylistAddTopItem = itemMenu.findItem(R.id.playlist_add_top);
            mPlaylistAddEndItem = itemMenu.findItem(R.id.playlist_add_end);
            mPlaylistRemoveItem = itemMenu.findItem(R.id.playlist_remove);
            mDownloadMenuItem = itemMenu.findItem(R.id.download);
        }

        private void onBind(Episode episode, String fallbackThumbnailUrl){
            mTitleTextView.setText(episode.getTitle());
            Picasso.with(mThumbnailView.getContext())
                    .load(fallbackThumbnailUrl)
                    .fit()
                    .centerCrop()
                    .placeholder(R.color.photo_placeholder)
                    .into(mThumbnailView);
            mDownloadMenuItem.setVisible(episode.getDownloadState() != Episode.DOWNLOADED);
            mDownloadMenuItem.setEnabled(episode.getDownloadState() == Episode.REMOTE);

            mPlaylistRemoveItem.setVisible(episode.getPlaylistEntryId() != null);
            mPlaylistAddEndItem.setVisible(!mPlaylistRemoveItem.isVisible());
            mPlaylistAddTopItem.setVisible(!mPlaylistRemoveItem.isVisible());
        }

        private final ActionMenuView.OnMenuItemClickListener mMenuItemCLickListener = item -> {
            int position = this.getAdapterPosition();
            if(position != RecyclerView.NO_POSITION && getListener() != null){
                switch (item.getItemId()){
                    case R.id.playlist_add_top:
                        getListener().onAddPlaylistTopClick(position);
                        break;
                    case R.id.playlist_add_end:
                        getListener().onAddPlaylistEndClick(position);
                        break;
                    case R.id.playlist_remove:
                        getListener().onRemoveFromPlaylistClick(position);
                        break;
                    case R.id.download:
                        getListener().onDownloadClick(position);
                        break;
                }
            }

            return true;
        };
    }
}
