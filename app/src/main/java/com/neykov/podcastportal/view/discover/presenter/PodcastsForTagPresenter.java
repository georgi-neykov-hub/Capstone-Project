package com.neykov.podcastportal.view.discover.presenter;

import android.os.Bundle;
import android.os.Parcelable;

import com.neykov.podcastportal.model.entity.Tag;
import com.neykov.podcastportal.model.networking.GPodderService;
import com.neykov.podcastportal.view.base.BasePresenter;
import com.neykov.podcastportal.view.base.ItemListView;
import com.neykov.podcastportal.view.discover.view.PodcastsAdapter;

import javax.inject.Inject;

import retrofit.RetrofitError;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PodcastsForTagPresenter extends BasePresenter<ItemListView> {

    private static final String KEY_ADAPTER_STATE = "PopularPodcastsPresenter.KEY_ADAPTER_STATE";

    private PodcastsAdapter mAdapter;
    private GPodderService mService;

    @Inject
    public PodcastsForTagPresenter(GPodderService mService) {
        this.mService = mService;
        mAdapter = new PodcastsAdapter();
    }

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        if(savedState != null){
            Parcelable adapterState = savedState.getParcelable(KEY_ADAPTER_STATE);
            if(adapterState != null) mAdapter.onRestoreInstanceState(adapterState);
        }
    }

    @Override
    protected void onSave(Bundle state) {
        super.onSave(state);
        state.putParcelable(KEY_ADAPTER_STATE, mAdapter.onSaveInstanceState());
    }

    public PodcastsAdapter getAdapter(){
        return mAdapter;
    }

    public void refreshData(Tag tag){
        mAdapter.clearItems();
        fetchPodcastsForTag(tag);
    }

    public void loadItems(ItemListView view, Tag tag){
        view.showLoadingIndicator();
        fetchPodcastsForTag(tag);
    }

    private void fetchPodcastsForTag(Tag tag){
        //noinspection ConstantConditions
        mService.getPodcastsWithTag(tag.getTag(), 100)
                .flatMap(Observable::from)
                .toSortedList((podcast, podcast2) -> -podcast.compareTo(podcast2), 100)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(delayUntilViewAvailable())
                .subscribe(baseListViewListDelivery -> {
                    baseListViewListDelivery.split((baseListView, podcasts) -> {
                        baseListView.hideLoadingIndicator();
                        mAdapter.clearItems();
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
