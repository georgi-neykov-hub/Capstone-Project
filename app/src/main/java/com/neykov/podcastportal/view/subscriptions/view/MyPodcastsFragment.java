package com.neykov.podcastportal.view.subscriptions.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.neykov.podcastportal.R;
import com.neykov.podcastportal.model.entity.Subscription;
import com.neykov.podcastportal.view.base.SubscriptionListView;
import com.neykov.podcastportal.view.base.fragment.BaseListViewFragment;
import com.neykov.podcastportal.view.base.fragment.ItemListView;
import com.neykov.podcastportal.view.base.fragment.ToolbarFragment;
import com.neykov.podcastportal.view.subscriptions.presenter.SubscriptionsAdapter;
import com.neykov.podcastportal.view.subscriptions.presenter.SubscriptionsPresenter;
import com.neykov.podcastportal.view.widget.GridSpaceItemDecoration;

public class MyPodcastsFragment extends ToolbarFragment {

    public static final String TAG = MyPodcastsFragment.class.getSimpleName();

    public static MyPodcastsFragment newInstance() {
        return new MyPodcastsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_podcasts, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(savedInstanceState == null){
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.podcastsListContainer, new ContentFragment(), ContentFragment.class.getName())
                    .commit();
            getChildFragmentManager().executePendingTransactions();
        }
    }

    @Nullable
    @Override
    protected Toolbar onSetToolbar(View view) {
        return (Toolbar) view.findViewById(R.id.toolbar);
    }

    public static class ContentFragment extends BaseListViewFragment<SubscriptionsAdapter, SubscriptionsPresenter> implements SubscriptionListView {

        @NonNull
        @Override
        protected SubscriptionsPresenter onCreatePresenter() {
            return getDependencyResolver().getSubscriptionsComponent().createSubscriptionsPresenter();
        }

        @NonNull
        @Override
        protected SubscriptionsAdapter getAdapter() {
            return getPresenter().getAdapter();
        }

        @Override
        public void onResume() {
            getAdapter().setItemListener(mItemListener);
            super.onResume();
        }

        @Override
        public void onPause() {
            getAdapter().setItemListener(null);
            super.onPause();
        }

        @Override
        protected void onShowLoadError(int error) {

        }

        @Override
        protected void onConfigureRecycleView(@NonNull RecyclerView view) {
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
            int spanCount = context.getResources().getInteger(R.integer.grid_column_count);
            return new GridLayoutManager(context,spanCount, LinearLayoutManager.VERTICAL, false);
        }

        private void openEpisodesListScreen(Subscription target){
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, EpisodesListFragment.newInstance(target), EpisodesListFragment.TAG)
                    .addToBackStack(EpisodesListFragment.TAG)
                    .commit();
        }

        private final SubscriptionsAdapter.ItemListener mItemListener = new SubscriptionsAdapter.ItemListener() {
            @Override
            public void onUnsubscribeClick(int position) {

            }

            @Override
            public void onRefreshClick(int position) {

            }

            @Override
            public void onItemClick(int position) {
                Subscription target = getAdapter().getItem(position);
                openEpisodesListScreen(target);
            }
        };

        @Override
        public void onUnsubscribe(Subscription subscription) {

        }

        @Override
        public void onUnsubscribeError(Subscription subscription) {

        }
    }
}
