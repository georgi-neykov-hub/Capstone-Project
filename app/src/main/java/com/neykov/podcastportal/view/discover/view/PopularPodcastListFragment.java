package com.neykov.podcastportal.view.discover.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.neykov.podcastportal.R;
import com.neykov.podcastportal.view.ViewUtils;
import com.neykov.podcastportal.view.discover.presenter.PopularPodcastsPresenter;

public class PopularPodcastListFragment extends BaseListFragment<PopularPodcastsPresenter>  {

    public static PopularPodcastListFragment newInstance() {
        return new PopularPodcastListFragment();
    }

    @NonNull
    @Override
    protected RecyclerView.Adapter<? extends RecyclerView.ViewHolder> getAdapter() {
        return getPresenter().getAdapter();
    }

    @Override
    protected void onRefresh() {
        getPresenter().refreshData();
    }

    @Override
    protected void onShowLoadError(int error) {
        if(error == PopularPodcastsPresenter.ERROR_TYPE_NO_INTERNET){
            ViewUtils.getNoNetworkSnackbar(getContext(), getView()).show();
        }
    }

    @Override
    protected void onConfigureRecycleView(@NonNull RecyclerView view) {
        view.setItemAnimator(new DefaultItemAnimator());
        view.setVerticalScrollBarEnabled(true);
    }

    @NonNull
    @Override
    protected RecyclerView.LayoutManager onCreateLayoutManager(@NonNull Context context) {
        return new StaggeredGridLayoutManager(
                context.getResources().getInteger(R.integer.grid_column_count),
                StaggeredGridLayoutManager.VERTICAL);
    }

    @NonNull
    @Override
    protected PopularPodcastsPresenter onCreatePresenter() {
        return getDependencyResolver()
                .getDiscoverComponent()
                .createPopularPodcastsPresenter();
    }
}
