package com.neykov.podcastportal.model.networking;

import android.provider.Settings;

import com.neykov.podcastportal.BuildConfig;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class UserAgentInterceptor implements Interceptor {

    private static final String USER_AGENT_STRING = BuildConfig.APPLICATION_ID + " / " + Settings.Secure.ANDROID_ID;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request requestWithUserAgent = originalRequest.newBuilder()
                .removeHeader("User-Agent")
                .addHeader("User-Agent", USER_AGENT_STRING)
                .build();
        return chain.proceed(requestWithUserAgent);
    }
}
