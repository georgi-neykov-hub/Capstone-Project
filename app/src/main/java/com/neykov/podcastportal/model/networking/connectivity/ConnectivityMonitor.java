package com.neykov.podcastportal.model.networking.connectivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.neykov.podcastportal.model.utils.Global;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * A simple utility class for monitoring and querying the current network connectivity state.
 */
@Singleton
public class ConnectivityMonitor {

    private ConnectivityManager mConnectivityManager;
    private Subject<ConnectivityStatus, ConnectivityStatus> mConnectivityStatusSubject;

    @Inject
    public ConnectivityMonitor(@Global Context context) {
        mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        mConnectivityStatusSubject = new SerializedSubject<>(BehaviorSubject.create(getConnectivityStatus()));
    }

    public Observable<ConnectivityStatus> getConnectivityStatusObservable(){
        return mConnectivityStatusSubject.asObservable();
    }

    /**
     * Returns a {@linkplain ConnectivityStatus} object resembling the current network connectivity status.
     */
    public ConnectivityStatus getConnectivityStatus() {

        NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            switch (networkInfo.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    return ConnectivityStatus.CONNECTED_WIFI;
                case ConnectivityManager.TYPE_MOBILE:
                    return ConnectivityStatus.CONNECTED_MOBILE;
                default:
                    return ConnectivityStatus.CONNECTED_OTHER;
            }
        } else {
            return ConnectivityStatus.OFFLINE;
        }
    }

    public void checkConnectionAndThrow(){
        if(!isConnectedToNetwork()){
            throw new NoNetworkException();
        }
    }

    public boolean isConnectedToNetwork(){
        return getConnectivityStatus() != ConnectivityStatus.OFFLINE;
    }

    public boolean isNetworkMetered(){
        return isConnectedToNetwork() && mConnectivityManager.isActiveNetworkMetered();
    }
}
