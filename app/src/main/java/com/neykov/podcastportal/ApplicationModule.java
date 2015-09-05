package com.neykov.podcastportal;

import android.app.Application;
import android.content.Context;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {

    final Application application;

    public ApplicationModule(Application application) {
        this.application = application;
    }

    @Provides
    Application provideApplication(){
        return application;
    }

    @Provides
    Context provideApplicationContext(){
        return application.getApplicationContext();
    }
}
