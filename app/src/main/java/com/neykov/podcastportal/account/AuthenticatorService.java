package com.neykov.podcastportal.account;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AuthenticatorService extends Service {

    private PodcastPortalAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        mAuthenticator = new PodcastPortalAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
