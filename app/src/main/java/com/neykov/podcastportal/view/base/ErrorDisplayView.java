package com.neykov.podcastportal.view.base;

public interface ErrorDisplayView{
    int ERROR_GENERAL = 1;
    int ERROR_NETWORK = 2;

    void showError(int errorType);
}
