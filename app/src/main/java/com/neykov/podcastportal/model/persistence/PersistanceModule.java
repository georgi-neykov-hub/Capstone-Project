package com.neykov.podcastportal.model.persistence;

import android.content.Context;

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
    BriteContentResolver provideBriteContentResolver(SqlBrite brite, Context context){
        return brite.wrapContentProvider(context.getContentResolver());
    }

}
