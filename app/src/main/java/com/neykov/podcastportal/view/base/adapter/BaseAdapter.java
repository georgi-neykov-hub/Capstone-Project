package com.neykov.podcastportal.view.base.adapter;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class BaseAdapter<ItemType, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private List<ItemType> mItems;

    public BaseAdapter() {
        this(new ArrayList<>());
    }

    public BaseAdapter(List<ItemType> data) {
        this.mItems = data;
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

    public void addItem(ItemType item){
        if(item == null){
            throw new IllegalArgumentException("Null item provided.");
        }

        int insertPosition = mItems.size();
        mItems.add(item);
        notifyItemInserted(insertPosition);
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

    public void removeItem(ItemType item) {
        if (item == null) {
            throw new IllegalArgumentException("Null item provided.");
        }

        int position = mItems.indexOf(item);
        if (position == -1) {
            throw new IllegalArgumentException("Item not found.");
        }

        this.removeItem(position);
    }

    public void removeItem(int position) {
        if (position < 0 || position >= mItems.size()) {
            throw new IllegalArgumentException("Invalid position provided.");
        }

        mItems.remove(position);
        notifyItemRemoved(position);
    }

    public void setData(Collection<ItemType> items){
        if(items == null){
            throw new IllegalArgumentException("Null collection provided.");
        }

        mItems.clear();
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    public void clearItems() {
        int count = getItemCount();
        if (count > 0) {
            mItems.clear();
            notifyItemRangeRemoved(0, count);
        }
    }

    public void moveItem(int currentPosition, int newPosition){
        if (currentPosition < 0 || currentPosition >= mItems.size()) {
            throw new IllegalArgumentException("Invalid position provided.");
        }

        if (newPosition < 0 || newPosition >= mItems.size()) {
            throw new IllegalArgumentException("Invalid new position provided.");
        }

        if(currentPosition == newPosition) {
            return;
        }

        if (currentPosition < newPosition) {
            for (int i = currentPosition; i < newPosition; i++) {
                Collections.swap(mItems, i, i + 1);
            }
        } else {
            for (int i = currentPosition; i > newPosition; i--) {
                Collections.swap(mItems, i, i - 1);
            }
        }
        notifyItemMoved(currentPosition, newPosition);
    }

    public void swapItem(int position, ItemType replacement){
        mItems.set(position, replacement);
        notifyItemChanged(position, null);
    }

    protected final List<ItemType> getItems(){
        return mItems;
    }
}
