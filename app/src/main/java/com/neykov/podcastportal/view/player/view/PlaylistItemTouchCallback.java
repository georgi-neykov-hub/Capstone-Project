package com.neykov.podcastportal.view.player.view;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.neykov.podcastportal.view.widget.ExtendedItemTouchCallback;

public class PlaylistItemTouchCallback extends ExtendedItemTouchCallback {

    public interface PlaylistItemMoveHandler {
        void onItemMove(int fromPosition, int toPosition);
        void onItemDismiss(int position);
        void onItemMoveCompleted(int currentPosition, int originalPosition);
    }

    private PlaylistItemMoveHandler mItemMoveHandler;

    public void setItemMoveHandler(PlaylistItemMoveHandler handler) {
        this.mItemMoveHandler = handler;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        if (viewHolder.getItemViewType() == target.getItemViewType()) {
            int holderPosition = viewHolder.getAdapterPosition();
            int targetPosition = target.getAdapterPosition();
            if(holderPosition != RecyclerView.NO_POSITION &&
                    targetPosition != RecyclerView.NO_POSITION &&
                    mItemMoveHandler != null){
                mItemMoveHandler.onItemMove(holderPosition, targetPosition);
                return true;
            }
        }

        return false;
    }

    @Override
    public void onMoveCompleted(RecyclerView.ViewHolder viewHolder, int originalPosition) {
        int holderPosition = viewHolder.getAdapterPosition();
        if(holderPosition != RecyclerView.NO_POSITION &&
                holderPosition != originalPosition &&
                mItemMoveHandler != null){
            mItemMoveHandler.onItemMoveCompleted(holderPosition, originalPosition);
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int holderPosition = viewHolder.getAdapterPosition();
        if(holderPosition != RecyclerView.NO_POSITION &&
                mItemMoveHandler != null){
            mItemMoveHandler.onItemDismiss(holderPosition);
        }
    }
}
