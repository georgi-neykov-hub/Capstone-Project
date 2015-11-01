package com.neykov.podcastportal.model.utils;

import android.app.Service;
import android.content.Context;

import com.neykov.podcastportal.model.ModelComponent;
import com.neykov.podcastportal.model.ModelComponentProvider;

public abstract class ComponentService extends Service {

    private ModelComponent mModelComponent;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        mModelComponent = ((ModelComponentProvider) base.getApplicationContext()).getModelComponent();
    }

    public ModelComponent getModelComponent() {
        return mModelComponent;
    }
}
