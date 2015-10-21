package com.neykov.podcastportal.view.subscriptions.presenter;

import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.internal.view.SupportMenuInflater;
import android.support.v7.widget.ActionMenuView;
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

import java.util.List;

public class NestedEpisodeAdapter extends BaseAdapter<Episode, NestedEpisodeAdapter.EpisodeViewHolder> {

    public interface EpisodeItemListener extends OnItemClickListener{
        void onDownloadClick(int position);
        void onPlaylistClick(int position);

    }

    private String mDefaultThumbnailUrl;

    public NestedEpisodeAdapter(List<Episode> data) {
        super(data);
    }

    public NestedEpisodeAdapter() {
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public EpisodeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.list_item_episode_lite, parent, false);
        return new EpisodeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(EpisodeViewHolder holder, int position) {
        holder.onBind(getItem(position), mDefaultThumbnailUrl);
    }

    public void setDefaultThumbnail(String url){
        mDefaultThumbnailUrl = url;
    }

    protected static class EpisodeViewHolder extends BaseListenerViewHolder<OnItemClickListener>{

        private ImageView mThumbnailView;
        private TextView mTitleTextView;
        private MenuItem mDownloadMenuItem;
        private MenuItem mPlaylistAddItem;

        protected EpisodeViewHolder(View itemView) {
            super(itemView);
            mThumbnailView = (ImageView) itemView.findViewById(R.id.thumbnail);
            mTitleTextView = (TextView) itemView.findViewById(R.id.title);

            ActionMenuView menuView = (ActionMenuView) itemView.findViewById(R.id.menu);
            Menu itemMenu = menuView.getMenu();
            new SupportMenuInflater(menuView.getContext()).inflate(R.menu.menu_podcast_listitem, itemMenu);
            mPlaylistAddItem = itemMenu.findItem(R.id.playlist_add);
            mDownloadMenuItem = itemMenu.findItem(R.id.download);

            //DrawableCompat.setTint(mPlaylistAddItem.getIcon(), mTitleTextView.getCurrentTextColor());
            //DrawableCompat.setTint(mDownloadMenuItem.getIcon(), mTitleTextView.getCurrentTextColor());
        }

        private void onBind(Episode episode, String fallbackThumbnailUrl){
            mTitleTextView.setText(episode.getTitle());
            Picasso.with(mThumbnailView.getContext())
                    .load(fallbackThumbnailUrl)
                    .fit()
                    .centerCrop()
                    .placeholder(R.color.photo_placeholder)
                    .into(mThumbnailView);
        }
    }
}
