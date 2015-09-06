package com.neykov.podcastportal.view.base.adapter;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Simple Wrapper class to allow the addition of footers to RecycleViews
 *
 * @author Georgi
 */
public class FooterAdapterWrapper extends BaseWrapperAdapter<RecyclerView.ViewHolder> {

    private static final int ITEM_TYPE_FOOTER = 0x234567;

    private int mFooterViewResid;
    private boolean mFooterShown;

    public FooterAdapterWrapper(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter, @LayoutRes int viewLayoutResid) {
        super(adapter);
        mFooterViewResid = viewLayoutResid;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_FOOTER) {
            View footer = LayoutInflater.from(parent.getContext())
                    .inflate(mFooterViewResid, parent, false);
            ViewGroup.LayoutParams params = footer.getLayoutParams();
            if(params instanceof StaggeredGridLayoutManager.LayoutParams){
                ((StaggeredGridLayoutManager.LayoutParams)params).setFullSpan(true);
            }
            return new FooterViewHolder(footer);
        } else {
            return super.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getFooterPosition()) {
            return ITEM_TYPE_FOOTER;
        } else {
            return super.getItemViewType(position);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position != getFooterPosition()) {
            super.onBindViewHolder(holder, position);
        }
    }

    @Override
    public long getItemId(int position) {
        if (position == getFooterPosition()) {
            return mFooterViewResid;
        } else {
            return super.getItemId(position);
        }
    }

    @Override
    public int getItemCount() {
        if (mFooterShown) {
            return super.getItemCount() + 1;
        } else {
            return super.getItemCount();
        }
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        if (!(holder instanceof FooterViewHolder)) {
            super.onViewRecycled(holder);
        }
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        if (!(holder instanceof FooterViewHolder)) {
            super.onViewAttachedToWindow(holder);
        }
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        if (!(holder instanceof FooterViewHolder)) {
            super.onViewDetachedFromWindow(holder);
        }
    }

    public void showFooter() {
        if (!mFooterShown) {
            mFooterShown = true;
            notifyItemInserted(getFooterPosition());
        }
    }

    public void hideFooter() {
        if (mFooterShown) {
            mFooterShown = false;
            notifyItemRemoved(getFooterPosition());
        }
    }

    private int getFooterPosition(){
        return getWrappedAdapter().getItemCount();
    }

    private static class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }
}
