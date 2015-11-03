package com.neykov.podcastportal.util;

import android.app.Application;
import android.content.Context;

import com.neykov.podcastportal.model.utils.Global;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {

    private Application mApplication;

    public ApplicationModule(Application mApplication) {
        this.mApplication = mApplication;
    }

    @Provides @Singleton
    Application provideApplication(){
        return mApplication;
    }

    @Provides @Singleton @Global
    Context provideApplicationContext(){
        return mApplication.getApplicationContext();
    }
}
