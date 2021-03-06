package com.neykov.podcastportal.view.explore.presenter;

import android.os.Bundle;
import android.os.Parcelable;

import com.neykov.podcastportal.model.networking.GPodderService;
import com.neykov.podcastportal.view.base.BasePresenter;
import com.neykov.podcastportal.view.base.fragment.ItemListView;
import com.neykov.podcastportal.view.explore.view.TagsAdapter;

import javax.inject.Inject;

import retrofit.RetrofitError;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PopularTagsPresenter extends BasePresenter<ItemListView> {

    private static final String KEY_ADAPTER_STATE = "PopularTagsPresenter.KEY_ADAPTER_STATE";

    private GPodderService mService;
    private TagsAdapter mAdapter;

    @Inject
    public PopularTagsPresenter(GPodderService mService) {
        this.mService = mService;
        mAdapter = new TagsAdapter();
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

    public TagsAdapter getAdapter(){
        return mAdapter;
    }

    public void refreshData(){
        mAdapter.clearItems();
        fetchPopularTags();
    }

    public void loadItems(ItemListView view){
        view.showLoadingIndicator();
        fetchPopularTags();
    }

    private void fetchPopularTags(){
        mService.getTopPodcastsTags(100)
                .flatMap(Observable::from)
                .toSortedList()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(delayUntilViewAvailable())
                .subscribe(delivery -> {
                    delivery.split(
                            (topTagsView, tags) -> {
                                topTagsView.hideLoadingIndicator();
                                mAdapter.addItems(tags);
                                topTagsView.hideLoadingIndicator();
                            }, (topTagsView, throwable) -> {
                                topTagsView.hideLoadingIndicator();
                                if(throwable instanceof RetrofitError){
                                    RetrofitError typedError = (RetrofitError) throwable;
                                    if(typedError.getKind() == RetrofitError.Kind.NETWORK){
                                        topTagsView.showListLoadError(ItemListView.ERROR_NETWORK);
                                    }
                                }else {
                                    topTagsView.showListLoadError(ItemListView.ERROR_GENERAL);
                                }
                            });
                });
    }
}
