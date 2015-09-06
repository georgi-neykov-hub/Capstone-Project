package com.neykov.podcastportal.view.base.adapter;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class BaseAdapter<ItemType, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private List<ItemType> mItems;

    public BaseAdapter() {
        this.mItems = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public ItemType getItem(int position){
        return mItems.get(position);
    }

    public boolean isEmpty(){
        return mItems.isEmpty();
    }

    public void addItems(Collection<ItemType> newItems){
        if(newItems == null){
            throw new IllegalArgumentException("Null collection provided.");
        } else if(!newItems.isEmpty()){
            int addPosition = getItemCount();
            mItems.addAll(newItems);
            notifyItemRangeInserted(addPosition, newItems.size());
        }
    }

    public void clearItems() {
        int count = getItemCount();
        if (count > 0) {
            mItems.clear();
            notifyItemRangeRemoved(0, count);
        }
    }

    protected final void setItems(List<ItemType> items){
        this.mItems = items;
        notifyDataSetChanged();
    }

    protected final List<ItemType> getItems(){
        return mItems;
    }
}
