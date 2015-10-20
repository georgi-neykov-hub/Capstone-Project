package com.neykov.podcastportal.view.discover.view;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.neykov.podcastportal.R;
import com.neykov.podcastportal.model.entity.RemotePodcastData;
import com.neykov.podcastportal.model.entity.Subscription;
import com.neykov.podcastportal.view.base.adapter.BaseAdapter;
import com.neykov.podcastportal.view.base.adapter.BaseListenerViewHolder;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;

public class PodcastsAdapter extends BaseAdapter<RemotePodcastData, PodcastsAdapter.PodcastViewHolder> {

    public interface PodcastItemListener {
        void onItemClick(int position);
        void onItemSubscribeClick(int position);
        void onItemShareClick(int position);
    }

    private static final int TYPE_SUBSCRIPTION = 1;
    private static final int TYPE_PODCAST = 2;

    private WeakReference<PodcastItemListener> mListenerRef = new WeakReference<>(null);

    public void setListener(PodcastItemListener listener) {
        mListenerRef = new WeakReference<>(listener);
    }

    @Override
    public PodcastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_PODCAST) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_podcast, parent, false);
            return new PodcastViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_subscribed_podcast, parent, false);
            return new SubscriptionViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(PodcastViewHolder holder, int position) {
        holder.onBind(getItem(position));
        holder.setListener(mProxyListener);
    }

    @Override
    public void onViewRecycled(PodcastViewHolder holder) {
        super.onViewRecycled(holder);
        holder.setListener(null);
    }

    @Override
    public int getItemViewType(int position) {
        return (getItem(position) instanceof Subscription) ? TYPE_SUBSCRIPTION : TYPE_PODCAST;
    }

    private final PodcastItemListener mProxyListener = new PodcastItemListener() {
        @Override
        public void onItemClick(int position) {
            PodcastItemListener listener = mListenerRef.get();
            if (listener != null) {
                listener.onItemClick(position);
            }
        }

        @Override
        public void onItemSubscribeClick(int position) {
            PodcastItemListener listener = mListenerRef.get();
            if (listener != null) {
                listener.onItemSubscribeClick(position);
            }
        }

        @Override
        public void onItemShareClick(int position) {
            PodcastItemListener listener = mListenerRef.get();
            if (listener != null) {
                listener.onItemShareClick(position);
            }
        }
    };

    protected static class PodcastViewHolder extends BaseListenerViewHolder<PodcastItemListener> {

        private ImageView mLogoImageView;
        private TextView mTitleTextView;
        private TextView mDescriptionTextView;
        private TextView mWebsiteTextView;
        private TextView mSubscribersTextView;

        public PodcastViewHolder(View itemView) {
            super(itemView);
            mLogoImageView = (ImageView) itemView.findViewById(R.id.logo);
            mTitleTextView = (TextView) itemView.findViewById(R.id.title);
            mDescriptionTextView = (TextView) itemView.findViewById(R.id.description);
            mSubscribersTextView = (TextView) itemView.findViewById(R.id.subscribers);
            mWebsiteTextView = (TextView) itemView.findViewById(R.id.website);
            mLogoImageView.setOnClickListener(v1 -> {
                PodcastItemListener listener = getListener();
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position);
                }
            });
            itemView.findViewById(R.id.action_subscribe).setOnClickListener(v -> {
                PodcastItemListener listener = getListener();
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemSubscribeClick(position);
                }
            });
            itemView.findViewById(R.id.action_share).setOnClickListener(v -> {
                PodcastItemListener listener = getListener();
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemShareClick(position);
                }
            });
        }

        protected void onBind(RemotePodcastData podcast) {
            mTitleTextView.setText(podcast.getTitle());
            mDescriptionTextView.setText(podcast.getDescription());
            mWebsiteTextView.setText(podcast.getWebsite());
            String subscribers = mSubscribersTextView.getResources()
                    .getString(
                            R.string.label_subscribers_count_format,
                            podcast.getSubscribers());
            mSubscribersTextView.setText(subscribers);
            loadLogoImage(podcast);
        }

        private void loadLogoImage(RemotePodcastData podcast) {
            if (podcast.getLogoUrl() != null) {
                Picasso.with(mLogoImageView.getContext())
                        .load(podcast.getLogoUrl())
                        .fit()
                        .centerCrop()
                        .placeholder(R.color.photo_placeholder)
                        .into(mLogoImageView);
            }
        }
    }

    protected static class SubscriptionViewHolder extends PodcastViewHolder {

        private TextView mLastUpdateTextView;

        public SubscriptionViewHolder(View itemView) {
            super(itemView);
            mLastUpdateTextView = (TextView) itemView.findViewById(R.id.lastUpdate);
        }

        @Override
        protected void onBind(RemotePodcastData podcast) {
            super.onBind(podcast);
            Subscription subscription = (Subscription) podcast;
            long now = System.currentTimeMillis();
            CharSequence lastUpdateText = DateUtils.getRelativeTimeSpanString(
                    subscription.getDateUpdatedUtc().getTime(), now, DateUtils.SECOND_IN_MILLIS);
            mLastUpdateTextView.setText(lastUpdateText);
        }
    }
}
