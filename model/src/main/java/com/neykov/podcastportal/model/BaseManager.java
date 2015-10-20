package com.neykov.podcastportal.model;

import android.content.Context;

public abstract class BaseManager {

    private Context mApplicationContext;

    protected BaseManager(Context mApplicationContext) {
        this.mApplicationContext = mApplicationContext;
    }

    /**
     * Returns the global application {@linkplain Context}.
     */
    protected Context getApplicationContext() {
        return mApplicationContext;
    }
}
