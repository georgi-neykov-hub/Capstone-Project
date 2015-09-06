package com.neykov.podcastportal.view.discover.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.neykov.podcastportal.R;
import com.neykov.podcastportal.view.base.adapter.BaseStateAdapter;

public class TagsAdapter extends BaseStateAdapter<String, TagsAdapter.TagViewHolder> {

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

    protected static class TagViewHolder extends RecyclerView.ViewHolder{

        private TextView mTagNameTextView;

        public TagViewHolder(View itemView) {
            super(itemView);
            mTagNameTextView = (TextView) itemView.findViewById(R.id.tagName);
        }

        protected void onBind(String tagName){
            mTagNameTextView.setText(tagName);
        }
    }
}
