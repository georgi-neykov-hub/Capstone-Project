package com.neykov.podcastportal.model.networking;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

/**
 *
 * @author Georgi
 * A Dagger 2 module providing network-related dependencies
 */
@Module
public class NetworkingModule {

    public static final int OKHTTP_CACHE_SIZE_BYTES = 10 * 1024 *1024;
    public static final int HTTP_CONNECT_TIMEOUT_MS = 10*1000;
    public static final int HTTP_READOUT_TIMEOUT_MS = 20*1000;
    public static final int HTTP_WRITE_TIMEOUT_MS = 20*1000;

    private static final String GPODDER_DATE_FORMAT = "yyyy-MM-ddTHH:mm:ssZ";

    public static final String ITUNES_SEARCH_API_BASE_URL = "https://itunes.apple.com/";
    public static final String GPODDER_API_BASE_URL = "http://gpodder.net/";

    @Provides
    @Singleton
    Picasso providePicasso(Context context, OkHttpClient httpClient){
        return new Picasso.Builder(context)
                .downloader(new OkHttpDownloader(httpClient))
                .build();
    }

    @Provides @Singleton
    ITunesSearchService provideITunesSearchService(OkHttpClient okHttpClient, Gson gson) {
        return new RestAdapter.Builder()
                .setEndpoint(ITUNES_SEARCH_API_BASE_URL)
                .setClient(new OkClient(okHttpClient))
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .setConverter(new GsonConverter(gson))
                .build()
                .create(ITunesSearchService.class);
    }

    @Provides @Singleton
    GPodderService provideGPodderService(OkHttpClient okHttpClient) {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .excludeFieldsWithoutExposeAnnotation()
                //.setDateFormat(GPODDER_DATE_FORMAT)
                .create();

        return new RestAdapter.Builder()
                .setEndpoint(GPODDER_API_BASE_URL)
                .setClient(new OkClient(okHttpClient))
                .setConverter(new GsonConverter(gson))
                .build()
                .create(GPodderService.class);
    }

    @Provides @Singleton
    OkHttpClient provideOkHttpClient(Application application) {
        OkHttpClient client = new OkHttpClient();

        //Set Cache size and Timeout limits
        Cache cache = new Cache(application.getCacheDir(), OKHTTP_CACHE_SIZE_BYTES);
        client.setCache(cache);

        client.setConnectTimeout(HTTP_CONNECT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        client.setReadTimeout(HTTP_READOUT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        client.setWriteTimeout(HTTP_WRITE_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        client.networkInterceptors().add(new UserAgentInterceptor());
        return client;
    }

    @Provides @Singleton
    Gson provideGson() {
        return new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .excludeFieldsWithoutExposeAnnotation()
                .create();
    }
}
