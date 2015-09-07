package com.neykov.podcastportal.view.discover.view;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.neykov.podcastportal.R;
import com.neykov.podcastportal.model.entity.Tag;
import com.neykov.podcastportal.view.base.adapter.BaseStateAdapter;

import org.w3c.dom.Text;

import java.util.List;

public class TagsAdapter extends BaseStateAdapter<Tag, TagsAdapter.TagViewHolder> {

    @Override
    public TagViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_tag, parent, false);
        return new TagViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TagViewHolder holder, int position) {
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

    protected static class SavedState extends BaseAdapterState{
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

    protected static class TagViewHolder extends RecyclerView.ViewHolder{

        private TextView mTagNameTextView;
        private TextView mUsageCountTextView;

        public TagViewHolder(View itemView) {
            super(itemView);
            mTagNameTextView = (TextView) itemView.findViewById(R.id.tagName);
            mUsageCountTextView = (TextView) itemView.findViewById(R.id.usageCount);
        }

        protected void onBind(Tag tag){
            mTagNameTextView.setText(tag.getTitle());
            mUsageCountTextView.setText(String.valueOf(tag.getUsage()));
        }
    }
}
