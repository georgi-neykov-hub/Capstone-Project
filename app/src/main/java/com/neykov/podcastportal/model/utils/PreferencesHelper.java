package com.neykov.podcastportal.model.utils;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;

import com.neykov.podcastportal.R;
import com.neykov.podcastportal.model.persistence.DatabaseContract;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PreferencesHelper {

    private Context mContext;
    private SharedPreferences mPreferences;
    private Account mSyncAccount;

    @Inject
    public PreferencesHelper(@Global Context mContext, Account account) {
        this.mContext = mContext;
        this.mSyncAccount = account;
        mPreferences = mContext.getSharedPreferences(mContext.getString(R.string.pref_filename), Context.MODE_PRIVATE);
    }

    public int getSelectedSyncFrequency(){
         return Integer.parseInt(mPreferences.getString(mContext.getString(R.string.pref_key_sync_frequency), "180"));
    }

    public boolean areNotificationsEnabled(){
        return mPreferences.getBoolean(mContext.getString(R.string.pref_key_notifications_enabled), true);
    }

    public boolean isPlaybackOverMeteredEnabled(){
        return mPreferences.getBoolean(mContext.getString(R.string.pref_key_playback_over_metered), false);
    }

    public boolean isMasterSyncEnabled(){
        return ContentResolver.getMasterSyncAutomatically();
    }

    public boolean isDownloadOverMeteredEnabled(){
        return mPreferences.getBoolean(mContext.getString(R.string.pref_key_download_over_metered), false);
    }

    public boolean isAutomaticSyncEnabled(){
        return ContentResolver.getSyncAutomatically(mSyncAccount, DatabaseContract.CONTENT_AUTHORITY);
    }
}
