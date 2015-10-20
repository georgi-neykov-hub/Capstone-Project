package com.neykov.podcastportal.view.subscriptions.presenter;

import android.support.v7.internal.view.SupportMenuInflater;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.neykov.podcastportal.R;
import com.neykov.podcastportal.model.entity.Subscription;
import com.neykov.podcastportal.view.base.adapter.BaseListenerViewHolder;
import com.neykov.podcastportal.view.base.adapter.BaseStateAdapter;
import com.neykov.podcastportal.view.base.adapter.OnItemClickListener;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;

public class SubscriptionsAdapter extends BaseStateAdapter<Subscription, SubscriptionsAdapter.SubscriptionViewHolder> {

    public interface ItemListener extends OnItemClickListener {
        void onUnsubscribeClick(int position);
        void onRefreshClick(int position);
    }

    private WeakReference<ItemListener> mListenerRef;

    public void setItemListener(ItemListener listener){
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
    public SubscriptionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.list_item_subscription, parent, false);
        SubscriptionViewHolder holder = new SubscriptionViewHolder(itemView);
        holder.setListener(mProxyListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(SubscriptionViewHolder holder, int position) {
        holder.onBind(getItem(position));
    }

    private final ItemListener mProxyListener = new ItemListener() {
        @Override
        public void onUnsubscribeClick(int position) {
            ItemListener listener = mListenerRef.get();
            if(listener != null){
                listener.onUnsubscribeClick(position);
            }
        }

        @Override
        public void onRefreshClick(int position) {
            ItemListener listener = mListenerRef.get();
            if(listener != null){
                listener.onRefreshClick(position);
            }
        }

        @Override
        public void onItemClick(int position) {
            ItemListener listener = mListenerRef.get();
            if(listener != null){
                listener.onItemClick(position);
            }
        }
    };

    protected static class SubscriptionViewHolder extends BaseListenerViewHolder<ItemListener>{

        private TextView mTitleTextView;
        private ImageView mLogoImageView;

        protected SubscriptionViewHolder(View itemView) {
            super(itemView);
            mLogoImageView = (ImageView) itemView.findViewById(R.id.logo);
            mTitleTextView = (TextView) itemView.findViewById(R.id.title);
            mLogoImageView.setOnClickListener(mClickListener);
            ActionMenuView menuView = ((ActionMenuView)itemView.findViewById(R.id.menu));
            new SupportMenuInflater(menuView.getContext()).inflate(R.menu.menu_subscription, menuView.getMenu());
            menuView.setOnMenuItemClickListener(mMenuItemListener);
        }

        private void onBind(Subscription subscription){
            mTitleTextView.setText(subscription.getTitle());
            bindLogo(subscription);
        }

        private void bindLogo(Subscription subscription){
            Picasso.with(mLogoImageView.getContext())
                    .load(subscription.getLogoUrl())
                    .fit()
                    .centerCrop()
                    .placeholder(R.color.photo_placeholder)
                    .into(mLogoImageView);
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