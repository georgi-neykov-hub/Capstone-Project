package com.neykov.podcastportal.model.networking;

import android.content.Context;
import android.provider.Settings;

import com.neykov.podcastportal.model.utils.Global;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserAgentInterceptor implements Interceptor {

    private final String mUserAgentValue;

    @Inject
    public UserAgentInterceptor(@Global Context context){
        mUserAgentValue = context.getPackageName()+ " / " + Settings.Secure.ANDROID_ID;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request requestWithUserAgent = originalRequest.newBuilder()
                .removeHeader("User-Agent")
                .addHeader("User-Agent", mUserAgentValue)
                .build();
        return chain.proceed(requestWithUserAgent);
    }
}
