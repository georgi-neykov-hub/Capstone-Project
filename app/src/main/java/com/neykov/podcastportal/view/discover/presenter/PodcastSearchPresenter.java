package com.neykov.podcastportal.view.discover.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.neykov.podcastportal.model.entity.Tag;
import com.neykov.podcastportal.model.networking.GPodderService;
import com.neykov.podcastportal.view.base.BasePresenter;
import com.neykov.podcastportal.view.base.ItemListView;
import com.neykov.podcastportal.view.discover.view.PodcastsAdapter;

import javax.inject.Inject;

import retrofit.RetrofitError;
import rx.android.schedulers.AndroidSchedulers;

public class PodcastSearchPresenter extends BasePresenter<ItemListView>{

    private static final String KEY_SEARCH_QUERY = "PodcastSearchPresenter.KEY_SEARCH_QUERY";

    private GPodderService mService;
    private PodcastsAdapter mAdapter;

    private String mQuery;

    @Inject
    public PodcastSearchPresenter(GPodderService mService) {
        this.mService = mService;
    }

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        mQuery = savedState.getString(KEY_SEARCH_QUERY);
    }

    @Override
    protected void onSave(Bundle state) {
        super.onSave(state);
        state.putString(KEY_SEARCH_QUERY, mQuery);
    }

    public PodcastsAdapter getAdapter(){
        return mAdapter;
    }

    public void setQuery(String query){
        mQuery = query;
    }

    public void loadItems(ItemListView view, boolean showLoading) {
        if (showLoading) view.showLoadingIndicator();
        mAdapter.clearItems();
        if (mQuery != null) {
            executeSearchQuery(mQuery);
        } else {
            view.hideLoadingIndicator();
        }
    }

    private void executeSearchQuery(@NonNull String query){
        mService.searchPodcasts(query)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(delayUntilViewAvailable())
                .subscribe(baseListViewListDelivery -> {
                    baseListViewListDelivery.split((baseListView, podcasts) -> {
                        baseListView.hideLoadingIndicator();
                        mAdapter.addItems(podcasts);
                    }, (baseListView1, throwable) -> {
                        baseListView1.hideLoadingIndicator();
                        if(throwable instanceof RetrofitError){
                            RetrofitError typedError = (RetrofitError) throwable;
                            if(typedError.getKind() == RetrofitError.Kind.NETWORK){
                                baseListView1.showListLoadError(ItemListView.ERROR_NETWORK);
                            }
                        }else {
                            baseListView1.showListLoadError(ItemListView.ERROR_GENERAL);
                        }
                    });
                });
    }
}
