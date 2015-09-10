package com.neykov.podcastportal.view.discover.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.neykov.podcastportal.R;
import com.neykov.podcastportal.model.entity.Tag;
import com.neykov.podcastportal.view.ViewUtils;
import com.neykov.podcastportal.view.base.BaseListFragment;
import com.neykov.podcastportal.view.base.BaseListViewFragment;
import com.neykov.podcastportal.view.discover.presenter.PodcastsForTagPresenter;

public class PodcastsForTagFragment extends BaseListViewFragment<PodcastsAdapter, PodcastsForTagPresenter> {

    public static final String TAG = PodcastsForTagFragment.class.getSimpleName();
    private static final String ARG_TARGET_TAG = "PodcastsForTagFragment.ARG_TARGET_TAG";

    public static PodcastsForTagFragment newInstance(Tag tag) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_TARGET_TAG, tag);
        PodcastsForTagFragment fragment = new PodcastsForTagFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private Tag mTargetTag;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mTargetTag = getArguments().getParcelable(ARG_TARGET_TAG);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getAdapter().getItemCount() == 0) {
            getPresenter().loadItems(this, mTargetTag);
        }
    }

    @NonNull
    @Override
    protected PodcastsAdapter getAdapter() {
        return getPresenter().getAdapter();
    }

    @Override
    protected void onRefresh() {
        getPresenter().refreshData(mTargetTag);
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
    protected PodcastsForTagPresenter onCreatePresenter() {
        return getDependencyResolver()
                .getDiscoverComponent()
                .createPodcastsForTagPresenter();
    }
}
