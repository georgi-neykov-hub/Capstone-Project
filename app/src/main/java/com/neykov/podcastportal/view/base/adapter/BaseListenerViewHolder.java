package com.neykov.podcastportal.view.base.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class BaseListenerViewHolder<ListenerType> extends RecyclerView.ViewHolder {

    private ListenerType mListener;

    protected BaseListenerViewHolder(View itemView) {
        super(itemView);
    }

    public void setListener(ListenerType listener){
        mListener = listener;
    }

    protected ListenerType getListener(){
        return mListener;
    }
}
