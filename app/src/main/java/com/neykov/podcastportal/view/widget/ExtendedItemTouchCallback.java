package com.neykov.podcastportal.view.widget;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

public abstract class ExtendedItemTouchCallback extends ItemTouchHelper.Callback {
    private RecyclerView.ViewHolder mCurrentSelectedHolder;
    private int mDragStartPosition;

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if(actionState == ItemTouchHelper.ACTION_STATE_DRAG){
            mCurrentSelectedHolder = viewHolder;
            mDragStartPosition = viewHolder.getAdapterPosition();
        } else if(actionState == ItemTouchHelper.ACTION_STATE_IDLE && mCurrentSelectedHolder != null){
            onMoveCompleted(mCurrentSelectedHolder, mDragStartPosition);
            mDragStartPosition = RecyclerView.NO_POSITION;
            mCurrentSelectedHolder = null;
        }
    }

    public void onMoveCompleted(RecyclerView.ViewHolder viewHolder, int originalPosition){

    }
}
