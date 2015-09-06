package com.neykov.podcastportal.view.base;

/**
 * Created by Georgi on 6.9.2015 г..
 */
public interface DataObserver<T> {
    void onDataLoaded(T data);
    void onDataLoadFailed();
}
