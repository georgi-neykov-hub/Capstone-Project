package com.neykov.podcastportal.view.base.fragment;

import com.neykov.podcastportal.view.base.LoadingView;

public interface ItemListView extends LoadingView {
    int ERROR_GENERAL = 1;
    int ERROR_NETWORK = 2;


    void showListLoadError(int errorType);
}
