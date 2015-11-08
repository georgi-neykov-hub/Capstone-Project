package com.neykov.podcastportal.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;
import android.content.Context;

import com.neykov.podcastportal.R;
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

    @Provides
    @Singleton
    Application provideApplication() {
        return mApplication;
    }

    @Provides
    @Singleton
    @Global
    Context provideApplicationContext() {
        return mApplication.getApplicationContext();
    }

    @Provides
    @Singleton
    Account provideApplicationAccount(@Global Context context) {
        String accountType = context.getString(R.string.accountType);
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account newAccount;
        Account[] availableAccounts = accountManager.getAccountsByType(accountType);
        if (availableAccounts.length == 0) {
            newAccount = new Account(context.getString(R.string.app_name), accountType);
            accountManager.addAccountExplicitly(newAccount, null, null);
        } else {
            newAccount = availableAccounts[0];
        }

        return newAccount;
    }
}
