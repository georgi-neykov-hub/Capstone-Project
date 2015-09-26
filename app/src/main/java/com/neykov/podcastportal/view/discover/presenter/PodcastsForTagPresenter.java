package com.neykov.podcastportal.view.discover.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.neykov.podcastportal.model.entity.Podcast;
import com.neykov.podcastportal.model.networking.GPodderService;
import com.neykov.podcastportal.model.subscriptions.SubscriptionsManager;
import com.neykov.podcastportal.view.discover.view.PodcastsAdapter;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

public class PodcastsForTagPresenter extends BaseDiscoverPodcastsPresenter {

    private static final String KEY_TARGET_TAG = "PodcastsForTagPresenter.KEY_TARGET_TAG";

    public static final int LOADING_ITEM_COUNT = 100;

    private GPodderService mService;
    private String mTargetTag;

    @Inject
    public PodcastsForTagPresenter(GPodderService mService, SubscriptionsManager manager) {
        super(manager);
        this.mService = mService;
    }

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        if(savedState != null){
            mTargetTag = savedState.getString(KEY_TARGET_TAG);
        }
    }

    @Override
    protected void onSave(Bundle state) {
        super.onSave(state);
        state.putString(KEY_TARGET_TAG, mTargetTag);
    }

    public PodcastsAdapter getAdapter(){
        return mAdapter;
    }

    public @Nullable String getTargetTag() {
        return mTargetTag;
    }

    public void setTargetTag(@NonNull String targetTag) {
        this.mTargetTag = targetTag;
    }

    @NonNull
    @Override
    protected Observable<List<Podcast>> getRemotePodcastsObservable() {
        if(getTargetTag() == null){
            throw new IllegalStateException("setTargetTag() not called.");
        }
        return mService.getPodcastsWithTag(getTargetTag(), LOADING_ITEM_COUNT);
    }
}
