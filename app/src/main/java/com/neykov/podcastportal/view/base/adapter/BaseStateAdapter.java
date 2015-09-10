package com.neykov.podcastportal.view.base.adapter;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.neykov.podcastportal.view.base.BaseSavedState;

public abstract class BaseStateAdapter<ItemType, VH extends RecyclerView.ViewHolder> extends BaseAdapter<ItemType, VH> {

    public
    @NonNull
    Parcelable onSaveInstanceState() {
        return new BaseAdapterState(BaseSavedState.EMPTY_STATE);
    }

    public void onRestoreInstanceState(@NonNull Parcelable state) {
        if (!(state instanceof BaseAdapterState)) {
            throw new IllegalArgumentException("Wrong state class -- expecting BaseAdapter State");
        }
    }

    public static class BaseAdapterState extends com.neykov.podcastportal.view.base.BaseSavedState {

        protected BaseAdapterState(Parcelable superState) {
            super(superState);
        }

        protected BaseAdapterState(Parcel source) {
            super(source);
        }
    }
}