package com.neykov.podcastportal.view.discover.view;

import com.neykov.podcastportal.view.base.LoadingView;

public interface BaseListView extends LoadingView {
    void showListLoadError(int errorType);
}
