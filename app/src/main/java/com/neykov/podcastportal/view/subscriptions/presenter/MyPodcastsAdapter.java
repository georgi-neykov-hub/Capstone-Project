package com.neykov.podcastportal.view.subscriptions.presenter;

import android.content.res.Resources;
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
import com.neykov.podcastportal.view.widget.SpaceItemDecoration;

import java.lang.ref.WeakReference;

public class MyPodcastsAdapter extends BaseStateAdapter<SubscriptionAdapterItem, MyPodcastsAdapter.SubscriptionViewHolder> {

    public interface ItemListener extends SubscriptionAdapterItem.EpisodeItemListener {
        void onUnsubscribeClick(int position);
        void onRefreshClick(int position);
    }

    private WeakReference<ItemListener> mOuterSubscriptionListenerRef;

    public void setSubscriptionItemListener(ItemListener listener) {
        mOuterSubscriptionListenerRef = new WeakReference<>(listener);
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
        return new SubscriptionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SubscriptionViewHolder holder, int position) {
        SubscriptionAdapterItem item = getItem(position);
        holder.onBind(item);
        holder.setListener(mProxySubscriptionItemListener);
    }

    @Override
    public void onViewRecycled(SubscriptionViewHolder holder) {
        super.onViewRecycled(holder);
        holder.setListener(null);
        holder.onRecycle();
    }

    private final ItemListener mProxySubscriptionItemListener = new ItemListener() {
        @Override
        public void onUnsubscribeClick(int position) {
            ItemListener listener = mOuterSubscriptionListenerRef.get();
            if (listener != null) {
                listener.onUnsubscribeClick(position);
            }
        }

        @Override
        public void onRefreshClick(int position) {
            ItemListener listener = mOuterSubscriptionListenerRef.get();
            if (listener != null) {
                listener.onRefreshClick(position);
            }
        }

        @Override
        public void onAddToPlaylistTop(Episode episode) {
            SubscriptionAdapterItem.EpisodeItemListener listener = mOuterSubscriptionListenerRef.get();
            if (listener != null) {
                listener.onAddToPlaylistTop(episode);
            }
        }

        @Override
        public void onAddToPlaylistEnd(Episode episode) {
            SubscriptionAdapterItem.EpisodeItemListener listener = mOuterSubscriptionListenerRef.get();
            if (listener != null) {
                listener.onAddToPlaylistEnd(episode);
            }
        }

        @Override
        public void onRemoveFromPlaylist(Episode episode) {
            SubscriptionAdapterItem.EpisodeItemListener listener = mOuterSubscriptionListenerRef.get();
            if (listener != null) {
                listener.onRemoveFromPlaylist(episode);
            }
        }

        @Override
        public void onDownload(Episode episode) {
            SubscriptionAdapterItem.EpisodeItemListener listener = mOuterSubscriptionListenerRef.get();
            if (listener != null) {
                listener.onDownload(episode);
            }
        }

        @Override
        public void onSelected(Episode episode) {
            SubscriptionAdapterItem.EpisodeItemListener listener = mOuterSubscriptionListenerRef.get();
            if (listener != null) {
                listener.onSelected(episode);
            }
        }
    };

    protected static class SubscriptionViewHolder extends BaseListenerViewHolder<ItemListener> {

        private TextView mTitleTextView;
        private TextView mEpisodeCountTextView;
        private RecyclerView mEpisodesRecyclerView;

        private SubscriptionAdapterItem mBoundItem;

        protected SubscriptionViewHolder(View itemView) {
            super(itemView);
            mTitleTextView = (TextView) itemView.findViewById(R.id.title);
            mEpisodeCountTextView = (TextView) itemView.findViewById(R.id.subTitle);
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
            ActionMenuView menuView = ((ActionMenuView) itemView.findViewById(R.id.menu));
            new SupportMenuInflater(menuView.getContext()).inflate(R.menu.menu_subscription, menuView.getMenu());
            menuView.setOnMenuItemClickListener(mMenuItemListener);
        }

        private void onBind(SubscriptionAdapterItem item) {
            mBoundItem = item;
            mTitleTextView.setText(item.getSubscription().getTitle());
            int episodeCount = item.getAdapter().getItemCount();
            bindEpisodeCountLabel(episodeCount);
            mEpisodesRecyclerView.swapAdapter(item.getAdapter(), false);
            mEpisodesRecyclerView.setVisibility(episodeCount > 0 ? View.VISIBLE : View.GONE);
        }

        @Override
        public void setListener(ItemListener listener) {
            super.setListener(listener);
            if (mBoundItem != null) {
                mBoundItem.setItemListener(listener);
            }
        }

        private void bindEpisodeCountLabel(int episodeCount){
            Resources resources = mEpisodeCountTextView.getResources();
            CharSequence countLabel;
            switch (episodeCount){
                case 0:
                countLabel = resources.getString(R.string.subscription_episode_count_empty);
                break;
                case 1:
                    countLabel = resources.getString(R.string.subscription_episode_count_single);
                    break;
                default:
                    countLabel = resources.getString(R.string.subscription_episode_count, episodeCount);
                    break;
            }
            mEpisodeCountTextView.setText(countLabel);
        }

        private void onRecycle() {
            mBoundItem.setItemListener(null);
            mBoundItem = null;
        }

        private final ActionMenuView.OnMenuItemClickListener mMenuItemListener = item -> {
            int position = getAdapterPosition();
            ItemListener listener = getListener();
            if (listener != null && position != RecyclerView.NO_POSITION) {
                switch (item.getItemId()) {
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
