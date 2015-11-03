package com.neykov.podcastportal.model;

import android.content.Context;

import com.squareup.sqlbrite.BriteContentResolver;

public abstract class BaseManager {

    private Context mApplicationContext;
    private BriteContentResolver mBriteResolver;

    protected BaseManager(Context mApplicationContext, BriteContentResolver resolver) {
        this.mApplicationContext = mApplicationContext;
        this.mBriteResolver = resolver;
    }

    /**
     * Returns the global application {@linkplain Context}.
     */
    public Context getApplicationContext() {
        return mApplicationContext;
    }

    protected BriteContentResolver getBriteResolver(){
        return mBriteResolver;
    }
}
