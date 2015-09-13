package com.neykov.podcastportal.view.discover.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.neykov.podcastportal.R;
import com.neykov.podcastportal.model.entity.Podcast;
import com.neykov.podcastportal.model.entity.Subscription;
import com.neykov.podcastportal.view.base.adapter.BaseAdapter;
import com.neykov.podcastportal.view.base.adapter.BaseListenerViewHolder;
import com.squareup.picasso.Picasso;

public class PodcastsAdapter extends BaseAdapter<Podcast, PodcastsAdapter.PodcastViewHolder> {

    private static final int TYPE_SUBSCRIPTION = 1;
    private static final int TYPE_PODCAST = 2;

    @Override
    public PodcastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_PODCAST) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_podcast, parent, false);
            return new PodcastViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_subscription, parent, false);
            return new SubscriptionViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(PodcastViewHolder holder, int position) {
        holder.onBind(getItem(position));
    }

    @Override
    public int getItemViewType(int position) {
        return (getItem(position) instanceof Subscription) ? TYPE_SUBSCRIPTION : TYPE_PODCAST;
    }

    protected static class PodcastViewHolder extends BaseListenerViewHolder<PodcastViewHolder.Listener> {

        public interface Listener {
            void onSubscribeClick(int position);
            void onShareClick(int position);
        }

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
            itemView.findViewById(R.id.action_subscribe).setOnClickListener(v -> {
                Listener listener = getListener();
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onSubscribeClick(position);
                }
            });
            itemView.findViewById(R.id.action_share).setOnClickListener(v -> {
                Listener listener = getListener();
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onShareClick(position);
                }
            });
        }

        protected void onBind(Podcast podcast) {
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

        private void loadLogoImage(Podcast podcast) {
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

        public SubscriptionViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Podcast podcast) {
            super.onBind(podcast);
        }
    }
}
