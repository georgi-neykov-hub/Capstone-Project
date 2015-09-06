package com.neykov.podcastportal.view.base.adapter;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Georgi on 7/30/2015.
 */
public class BaseSavedState implements Parcelable {

    public static final BaseSavedState EMPTY_STATE = new BaseSavedState();

    private final Parcelable mSuperState;

    public BaseSavedState(Parcelable superState) {
        if (superState == null) {
            throw new IllegalArgumentException("superState must not be null");
        }
        mSuperState = superState != EMPTY_STATE ? superState : null;
    }

    protected BaseSavedState(Parcel in) {
        // FIXME need class loader
        Parcelable superState = in.readParcelable(Parcelable.class.getClassLoader());
        mSuperState = superState != null ? superState : EMPTY_STATE;
    }

    private BaseSavedState() {
        mSuperState = null;
    }

    public static final Creator<BaseSavedState> CREATOR = new Creator<BaseSavedState>() {
        @Override
        public BaseSavedState createFromParcel(Parcel in) {
            Parcelable superState = in.readParcelable(null);
            if (superState != null) {
                throw new IllegalStateException("superState must be null");
            }
            return EMPTY_STATE;
        }

        @Override
        public BaseSavedState[] newArray(int size) {
            return new BaseSavedState[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mSuperState, 0);
    }

    public Parcelable getSuperState() {
        return mSuperState;
    }

}
