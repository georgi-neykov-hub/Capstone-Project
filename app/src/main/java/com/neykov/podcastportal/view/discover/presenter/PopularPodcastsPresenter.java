package com.neykov.podcastportal.view.discover.presenter;

import com.neykov.podcastportal.model.entity.Podcast;
import com.neykov.podcastportal.model.networking.GPodderService;
import com.neykov.podcastportal.view.base.BasePresenter;
import com.neykov.podcastportal.view.discover.view.BaseListView;
import com.neykov.podcastportal.view.discover.view.PopularPodcastsAdapter;

import javax.inject.Inject;

import retrofit.RetrofitError;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PopularPodcastsPresenter extends BasePresenter<BaseListView> {

    public static final int ERROR_TYPE_NO_INTERNET = 1;

    private PopularPodcastsAdapter mAdapter;
    private GPodderService mService;

    @Inject
    public PopularPodcastsPresenter(GPodderService mService) {
        this.mService = mService;
        mAdapter = new PopularPodcastsAdapter();
    }

    public PopularPodcastsAdapter getAdapter(){
        return mAdapter;
    }

    public void refreshData(){
        mAdapter.clearItems();
        fetchPopularPodcasts();
    }

    public void loadItems(BaseListView view){
        view.showLoadingIndicator();
        fetchPopularPodcasts();
    }

    private void fetchPopularPodcasts(){
        //noinspection ConstantConditions
        mService.getTopPodcasts(100)
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
                                baseListView1.showListLoadError(ERROR_TYPE_NO_INTERNET);
                            }
                        }else {
                            baseListView1.showListLoadError(0);
                        }
                    });
                });
    }
}
