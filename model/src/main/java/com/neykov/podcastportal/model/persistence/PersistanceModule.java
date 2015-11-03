package com.neykov.podcastportal.model.persistence;

import android.content.Context;

import com.neykov.podcastportal.model.BuildConfig;
import com.neykov.podcastportal.model.utils.Global;
import com.squareup.sqlbrite.BriteContentResolver;
import com.squareup.sqlbrite.SqlBrite;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class PersistanceModule {
    @Provides @Singleton
    SqlBrite provideSqlBrite(){
        return SqlBrite.create();
    }

    @Provides @Singleton
    BriteContentResolver provideBriteContentResolver(SqlBrite brite, @Global Context context){
        BriteContentResolver resolver = brite.wrapContentProvider(context.getContentResolver());
        resolver.setLoggingEnabled(BuildConfig.DEBUG);
        return resolver;
    }

}
