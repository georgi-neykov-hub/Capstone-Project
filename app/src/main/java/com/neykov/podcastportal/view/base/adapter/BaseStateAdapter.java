package com.neykov.podcastportal.view.base.adapter;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

public abstract class BaseStateAdapter<ItemType, VH extends RecyclerView.ViewHolder> extends BaseAdapter<ItemType, VH> {

    public @NonNull
    Parcelable onSaveInstanceState(){
        return BaseAdapterState.EMPTY_STATE;
    }

    public void onRestoreInstanceState(@NonNull Parcelable state){
        if (state != BaseAdapterState.EMPTY_STATE) {
            throw new IllegalArgumentException("Wrong state class -- expecting Preference State");
        }
    }

    public static abstract class BaseAdapterState<ItemType> implements Parcelable{
        public static final BaseAdapterState EMPTY_STATE = new BaseAdapterState() {
        };

        private final Parcelable mSuperState;

        /**
         * Constructor used to make the EMPTY_STATE singleton
         */
        private BaseAdapterState() {
            mSuperState = null;
        }

        /**
         * Constructor called by derived classes when creating their SavedState objects
         *
         * @param superState The state of the superclass of this view
         */
        protected BaseAdapterState(Parcelable superState) {
            if (superState == null) {
                throw new IllegalArgumentException("superState must not be null");
            }
            mSuperState = superState != EMPTY_STATE ? superState : null;
        }

        /**
         * Constructor used when reading from a parcel. Reads the state of the superclass.
         *
         * @param source
         */
        protected BaseAdapterState(Parcel source) {
            Parcelable superState = source.readParcelable(getClass().getClassLoader());

            mSuperState = superState != null ? superState : EMPTY_STATE;
        }

        final public Parcelable getSuperState() {
            return mSuperState;
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(mSuperState, flags);
        }

    }
}
