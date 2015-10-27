package com.neykov.podcastportal.view.subscriptions.presenter;

import android.support.v7.internal.view.SupportMenuInflater;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.neykov.podcastportal.R;
import com.neykov.podcastportal.model.entity.Episode;
import com.neykov.podcastportal.view.base.adapter.BaseListenerViewHolder;
import com.neykov.podcastportal.view.base.adapter.BaseStateAdapter;
import com.neykov.podcastportal.view.base.adapter.OnItemClickListener;
import com.neykov.podcastportal.view.widget.SpaceItemDecoration;

import java.lang.ref.WeakReference;

public class MyPodcastsAdapter extends BaseStateAdapter<SubscriptionAdapterItem, MyPodcastsAdapter.SubscriptionViewHolder> {

    public interface ItemListener extends OnItemClickListener {
        void onUnsubscribeClick(int position);
        void onRefreshClick(int position);
    }

    private WeakReference<ItemListener> mOuterSubscriptionListenerRef;
    private WeakReference<SubscriptionAdapterItem.EpisodeItemListener> mOuterEpisodeListenerRef;

    public void setSubscriptionItemListener(ItemListener listener){
        mOuterSubscriptionListenerRef = new WeakReference<>(listener);
    }

    public void setEpisodeItemListener(SubscriptionAdapterItem.EpisodeItemListener listener){
        mOuterEpisodeListenerRef = new WeakReference<>(listener);
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getSubscription().getId();
    }

    @Override
    public SubscriptionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.list_item_subscription, parent, false);
        SubscriptionViewHolder holder = new SubscriptionViewHolder(itemView);
        holder.setListener(mProxySubscriptionItemListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(SubscriptionViewHolder holder, int position) {
        SubscriptionAdapterItem item = getItem(position);
        holder.onBind(item);
        item.setItemListener(mProxyEpisodeItemListener);
    }

    private final ItemListener mProxySubscriptionItemListener = new ItemListener() {
        @Override
        public void onUnsubscribeClick(int position) {
            ItemListener listener = mOuterSubscriptionListenerRef.get();
            if(listener != null){
                listener.onUnsubscribeClick(position);
            }
        }

        @Override
        public void onRefreshClick(int position) {
            ItemListener listener = mOuterSubscriptionListenerRef.get();
            if(listener != null){
                listener.onRefreshClick(position);
            }
        }

        @Override
        public void onItemClick(int position) {
            ItemListener listener = mOuterSubscriptionListenerRef.get();
            if(listener != null){
                listener.onItemClick(position);
            }
        }
    };

    private final SubscriptionAdapterItem.EpisodeItemListener mProxyEpisodeItemListener = new SubscriptionAdapterItem.EpisodeItemListener() {
        @Override
        public void onAddToPlaylistTop(Episode episode) {
            SubscriptionAdapterItem.EpisodeItemListener listener = mOuterEpisodeListenerRef.get();
            if(listener != null){
                listener.onAddToPlaylistTop(episode);
            }
        }

        @Override
        public void onAddToPlaylistEnd(Episode episode) {
            SubscriptionAdapterItem.EpisodeItemListener listener = mOuterEpisodeListenerRef.get();
            if(listener != null){
                listener.onAddToPlaylistEnd(episode);
            }
        }

        @Override
        public void onRemoveFromPlaylist(Episode episode) {
            SubscriptionAdapterItem.EpisodeItemListener listener = mOuterEpisodeListenerRef.get();
            if(listener != null){
                listener.onRemoveFromPlaylist(episode);
            }
        }

        @Override
        public void onDownload(Episode episode) {
            SubscriptionAdapterItem.EpisodeItemListener listener = mOuterEpisodeListenerRef.get();
            if(listener != null){
                listener.onDownload(episode);
            }
        }

        @Override
        public void onSelected(Episode episode) {
            SubscriptionAdapterItem.EpisodeItemListener listener = mOuterEpisodeListenerRef.get();
            if(listener != null){
                listener.onSelected(episode);
            }
        }
    };

    protected static class SubscriptionViewHolder extends BaseListenerViewHolder<ItemListener>{

        private TextView mTitleTextView;
        private RecyclerView mEpisodesRecyclerView;

        protected SubscriptionViewHolder(View itemView) {
            super(itemView);
            mTitleTextView = (TextView) itemView.findViewById(R.id.title);
            mEpisodesRecyclerView = (RecyclerView) itemView.findViewById(R.id.items);
            RecyclerView.LayoutManager manager = new LinearLayoutManager(mEpisodesRecyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false);
            RecyclerView.ItemDecoration decoration = new SpaceItemDecoration(
                    mEpisodesRecyclerView.getResources(),
                    R.dimen.activity_horizontal_margin,
                    R.dimen.rhythm_space_half,
                    R.dimen.activity_horizontal_margin,
                    SpaceItemDecoration.HORIZONTAL);
            mEpisodesRecyclerView.addItemDecoration(decoration);
            mEpisodesRecyclerView.setLayoutManager(manager);
            ActionMenuView menuView = ((ActionMenuView)itemView.findViewById(R.id.menu));
            new SupportMenuInflater(menuView.getContext()).inflate(R.menu.menu_subscription, menuView.getMenu());
            menuView.setOnMenuItemClickListener(mMenuItemListener);
        }

        private void onBind(SubscriptionAdapterItem item){
            mTitleTextView.setText(item.getSubscription().getTitle());
            mEpisodesRecyclerView.swapAdapter(item.getAdapter(), false);
        }

        private final View.OnClickListener mClickListener = view -> {
            int position = getAdapterPosition();
            ItemListener listener = getListener();
            if(listener != null && position != RecyclerView.NO_POSITION){
                listener.onItemClick(position);
            }
        };

        private final ActionMenuView.OnMenuItemClickListener mMenuItemListener = item -> {
            int position = getAdapterPosition();
            ItemListener listener = getListener();
            if(listener != null && position != RecyclerView.NO_POSITION){
                switch (item.getItemId()){
                    case R.id.unsubscribe:
                        listener.onUnsubscribeClick(position);
                        return true;
                    case R.id.refresh:
                        listener.onRefreshClick(position);
                        return true;
                }
            }
            return false;
        };
    }
}
