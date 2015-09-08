package com.neykov.podcastportal.view.discover.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.neykov.podcastportal.R;
import com.neykov.podcastportal.model.entity.Podcast;
import com.neykov.podcastportal.view.base.adapter.BaseStateAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PopularPodcastsAdapter extends BaseStateAdapter<Podcast, PopularPodcastsAdapter.PodcastViewHolder> {

    @Override
    public PodcastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_podcast, parent, false);
        return new PodcastViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PodcastViewHolder holder, int position) {
        holder.onBind(getItem(position));
    }

    @NonNull
    @Override
    public Parcelable onSaveInstanceState() {
        return new SavedState(super.onSaveInstanceState(), getItems());
    }

    @Override
    public void onRestoreInstanceState(@NonNull Parcelable state) {
        if (!(state instanceof SavedState)) {
            throw new IllegalArgumentException("Improper Parcelable type.");
        }

        SavedState typedState = (SavedState) state;
        super.onRestoreInstanceState(typedState.getSuperState());
        setItems(typedState.tags);
    }

    protected static class SavedState extends BaseAdapterState {

        private List<Podcast> tags;

        public SavedState(Parcelable superState, List<Podcast> tags) {
            super(superState);
            this.tags = tags;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeTypedList(tags);
        }

        protected SavedState(Parcel in) {
            super(in);
            this.tags = in.createTypedArrayList(Podcast.CREATOR);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    protected static class PodcastViewHolder extends RecyclerView.ViewHolder{

        private ImageView mLogoImageView;
        private TextView mTitleTextView;
        private TextView mDescriptionTextView;

        public PodcastViewHolder(View itemView) {
            super(itemView);
            mLogoImageView = (ImageView) itemView.findViewById(R.id.logo);
            mTitleTextView = (TextView) itemView.findViewById(R.id.title);
            mDescriptionTextView = (TextView) itemView.findViewById(R.id.description);
        }

        protected void onBind(Podcast podcast){
            mTitleTextView.setText(podcast.getTitle());
            mDescriptionTextView.setText(podcast.getDescription());
            loadLogoImage(podcast);
        }

        private void loadLogoImage(Podcast podcast) {
            if(podcast.getLogoUrl() != null) {
                Picasso.with(mLogoImageView.getContext())
                        .load(podcast.getLogoUrl())
                        .centerCrop()
                        .fit()
                        .noPlaceholder()
                        .into(mLogoImageView);
            }
        }
    }
}
