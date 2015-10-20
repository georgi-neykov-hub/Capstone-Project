package com.neykov.podcastportal.view.discover.view;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.neykov.podcastportal.R;
import com.neykov.podcastportal.model.entity.Tag;
import com.neykov.podcastportal.view.base.BaseSavedState;
import com.neykov.podcastportal.view.base.adapter.BaseListenerViewHolder;
import com.neykov.podcastportal.view.base.adapter.BaseStateAdapter;
import com.neykov.podcastportal.view.base.adapter.OnItemClickListener;

import java.lang.ref.WeakReference;
import java.util.List;

public class TagsAdapter extends BaseStateAdapter<Tag, TagsAdapter.TagViewHolder> implements OnItemClickListener {

    private WeakReference<OnItemClickListener> mClickListenerRef;

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mClickListenerRef = new WeakReference<>(listener);
    }

    @Override
    public TagViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_tag, parent, false);
        return new TagViewHolder(itemView);
    }

    @Override
    public void onViewRecycled(TagViewHolder holder) {
        super.onViewRecycled(holder);
        holder.setListener(null);
    }

    @Override
    public void onBindViewHolder(TagViewHolder holder, int position) {
        holder.onBind(getItem(position));
        holder.setListener(this);
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
        setData(typedState.tags);
    }

    @Override
    public void onItemClick(int position) {
        OnItemClickListener listener = mClickListenerRef.get();
        if(listener != null) listener.onItemClick(position);
    }

    protected static class SavedState extends BaseSavedState{
        private List<Tag> tags;

        public SavedState(Parcelable superState, List<Tag> tags) {
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
            this.tags = in.createTypedArrayList(Tag.CREATOR);
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

    protected static class TagViewHolder extends BaseListenerViewHolder<OnItemClickListener>{

        private TextView mTagNameTextView;
        private TextView mUsageCountTextView;

        public TagViewHolder(View itemView) {
            super(itemView);
            mTagNameTextView = (TextView) itemView.findViewById(R.id.tagName);
            mUsageCountTextView = (TextView) itemView.findViewById(R.id.usageCount);
            itemView.findViewById(R.id.clickView).setOnClickListener(v -> getListener().onItemClick(getAdapterPosition()));
        }

        protected void onBind(Tag tag) {
            mTagNameTextView.setText(tag.getTitle());
            String usage = mUsageCountTextView.getResources().getString(R.string.label_tag_usage_format, tag.getUsage());
            mUsageCountTextView.setText(usage);
        }
    }
}
