package com.neykov.podcastportal.view.discover.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.neykov.podcastportal.R;
import com.neykov.podcastportal.model.entity.Podcast;
import com.neykov.podcastportal.model.entity.Subscription;
import com.neykov.podcastportal.view.ViewUtils;
import com.neykov.podcastportal.view.base.BaseListViewFragment;
import com.neykov.podcastportal.view.discover.presenter.PopularPodcastsPresenter;
import com.neykov.podcastportal.view.subscriptions.view.PodcastDetailFragment;

public class PopularPodcastsFragment extends BaseListViewFragment<PodcastsAdapter, PopularPodcastsPresenter> implements DiscoverPodcastsView {

    public static PopularPodcastsFragment newInstance() {
        return new PopularPodcastsFragment();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getAdapter().getItemCount() == 0) {
            getPresenter().loadItems(this);
        }
    }

    @Override
    public void onPause() {
        getAdapter().setListener(null);
        super.onPause();
    }

    @Override
    public void onResume() {
        getAdapter().setListener(mItemListener);
        super.onResume();
    }

    @NonNull
    @Override
    protected PodcastsAdapter getAdapter() {
        return getPresenter().getAdapter();
    }

    @Override
    protected void onRefresh() {
        getPresenter().refreshData();
    }

    @Override
    protected void onShowLoadError(int error) {
        if(error == ERROR_NETWORK){
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

    private final PodcastsAdapter.PodcastItemListener mItemListener = new PodcastsAdapter.PodcastItemListener() {
        @Override
        public void onItemClick(int position) {
            FragmentManager manager = getParentFragment() != null ?
                    getParentFragment().getFragmentManager() : getActivity().getSupportFragmentManager();
            Podcast target = getAdapter().getItem(position);
            manager.beginTransaction()
                    .replace(R.id.content, PodcastDetailFragment.newInstance(target), PodcastDetailFragment.TAG)
                    .addToBackStack(PodcastDetailFragment.TAG)
                    .commit();
            manager.executePendingTransactions();
        }

        @Override
        public void onItemSubscribeClick(int position) {
            Podcast podcast = getAdapter().getItem(position);
            if (podcast instanceof Subscription) {
                getPresenter().unsubscribeFromPodcast(position, (Subscription) podcast);
            } else {
                getPresenter().subscribeForPodcast(position, podcast);
            }
        }

        @Override
        public void onItemShareClick(int position) {

        }
    };

    @Override
    public void onPodcastSubcribed(Subscription podcast) {

    }

    @Override
    public void onPodcastUnsubscribed(Podcast podcast) {

    }
}
