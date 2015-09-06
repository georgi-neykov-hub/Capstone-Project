package com.neykov.podcastportal.view.discover.presenter;

import com.neykov.podcastportal.model.networking.GPodderService;
import com.neykov.podcastportal.view.base.BasePresenter;
import com.neykov.podcastportal.view.discover.view.PopularTagsView;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;

public class PopularTagsPresenter extends BasePresenter<PopularTagsView> {

    private GPodderService mService;

    @Inject
    public PopularTagsPresenter(GPodderService mService) {
        this.mService = mService;
    }

    public void getTopPodcastTags(){
        mService.getTopPodcastsTags(100)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(delayUntilViewAvailable())
                .subscribe(topTagsViewListDelivery -> {
                    topTagsViewListDelivery.split(
                            (topTagsView, tags) -> topTagsView.onTagsLoaded(tags),
                            (topTagsView, throwable) -> topTagsView.onTagsLoadFailed());
                });
    }
}
