package com.neykov.podcastportal.view.explore.view;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.neykov.podcastportal.R;
import com.neykov.podcastportal.model.entity.RemotePodcastData;
import com.neykov.podcastportal.model.entity.Subscription;
import com.neykov.podcastportal.view.ViewUtils;
import com.neykov.podcastportal.view.base.AlertDialogFragment;
import com.neykov.podcastportal.view.base.fragment.BaseListViewFragment;
import com.neykov.podcastportal.view.explore.presenter.PopularPodcastsPresenter;
import com.neykov.podcastportal.view.widget.GridSpaceItemDecoration;

public class PopularPodcastsFragment extends BaseListViewFragment<PodcastsAdapter, PopularPodcastsPresenter> implements DiscoverPodcastsView, AlertDialogFragment.OnDialogFragmentClickListener {

    public static final int UNSUBSCRIBE_DIALOG_ID = 1000;
    public static final String TAG_UNSUBSCRIBE_DIALOG = "PopularPodcastsFragment.TAG_UNSUBSCRIBE_DIALOG";

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
    protected boolean onRefresh() {
        getPresenter().refreshData();
        return true;
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
        int spanCount = getResources().getInteger(R.integer.grid_column_count);
        int horizontalPadding = getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
        int verticalPadding = getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin);
        view.setPaddingRelative(horizontalPadding, 0, horizontalPadding, 0);
        GridSpaceItemDecoration spaceDecoration = new GridSpaceItemDecoration(spanCount, GridSpaceItemDecoration.VERTICAL);
        spaceDecoration.setVerticalEndSpacing(verticalPadding);
        view.addItemDecoration(spaceDecoration);
        view.setItemAnimator(new DefaultItemAnimator());
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
        }

        @Override
        public void onItemSubscribeClick(int position) {
            RemotePodcastData podcast = getAdapter().getItem(position);
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
    public void onPodcastUnsubscribed(RemotePodcastData podcast) {

    }

    private void unsubscribePodcast(Subscription subscription){
        new AlertDialogFragment.Builder(getContext())
                .setMessage("Unsubscribing will delete any related downloaded episodes.")
                .setPositiveButton(R.string.action_unsubscribe)
                .setNegativeButton(android.R.string.cancel)
                .setId(UNSUBSCRIBE_DIALOG_ID)
                .show(getChildFragmentManager(), TAG_UNSUBSCRIBE_DIALOG);
    }

    @Override
    public void onClick(DialogFragment dialog, @AlertDialogFragment.DialogButton int which, int dialogId) {
        if(dialogId == UNSUBSCRIBE_DIALOG_ID && which == DialogInterface.BUTTON_POSITIVE){
        }
    }
}
