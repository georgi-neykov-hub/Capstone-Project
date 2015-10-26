package com.neykov.podcastportal.view.subscriptions.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.neykov.podcastportal.R;
import com.neykov.podcastportal.model.entity.PodcastSubscription;
import com.neykov.podcastportal.view.base.fragment.BaseListViewFragment;
import com.neykov.podcastportal.view.base.fragment.ItemListView;
import com.neykov.podcastportal.view.base.fragment.ToolbarFragment;
import com.neykov.podcastportal.view.subscriptions.presenter.NestedEpisodeAdapter;
import com.neykov.podcastportal.view.subscriptions.presenter.EpisodesListPresenter;
import com.neykov.podcastportal.view.widget.GridSpaceItemDecoration;

public class EpisodesListFragment extends ToolbarFragment {

    public static final String TAG = EpisodesListFragment.class.getName();
    private static final String ARG_TARGET_PODCAST = "EpisodesListFragment.ARG_TARGET_PODCAST";

    public static EpisodesListFragment newInstance(PodcastSubscription podcast) {
        if (podcast == null) {
            throw new IllegalArgumentException("Podcast argument cannot be null.");
        }

        Bundle args = new Bundle();
        args.putParcelable(ARG_TARGET_PODCAST, podcast);
        EpisodesListFragment fragment = new EpisodesListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static EpisodesListFragment newInstance() {
        return new EpisodesListFragment();
    }

    private PodcastSubscription mTargetPodcast;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTargetPodcast = getArguments().getParcelable(ARG_TARGET_PODCAST);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_episodes, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(savedInstanceState == null){
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.episodesListContainer, ContentFragment.newInstance(mTargetPodcast), ContentFragment.TAG)
                    .commit();
        }
    }

    @Nullable
    @Override
    protected Toolbar onSetToolbar(View view) {
        return (Toolbar) view.findViewById(R.id.toolbar);
    }

    @Override
    protected void onConfigureToolbar(Toolbar toolbar) {
        super.onConfigureToolbar(toolbar);
        setHomeAsUpEnabled(true);
        toolbar.setSubtitle(mTargetPodcast.getTitle());
    }

    public static class ContentFragment extends BaseListViewFragment<NestedEpisodeAdapter, EpisodesListPresenter> implements ItemListView {

        public static final String TAG = ContentFragment.class.getName();

        protected static ContentFragment newInstance(PodcastSubscription podcast) {
            if (podcast == null) {
                throw new IllegalArgumentException("Podcast argument cannot be null.");
            }

            Bundle args = new Bundle();
            args.putParcelable(ARG_TARGET_PODCAST, podcast);
            ContentFragment fragment = new ContentFragment();
            fragment.setArguments(args);
            return fragment;
        }

        private PodcastSubscription mTargetPodcast;

        @Override
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            mTargetPodcast = getArguments().getParcelable(ARG_TARGET_PODCAST);
            setRefreshingEnabled(true);
            getAdapter().setDefaultThumbnail(mTargetPodcast.getLogoUrl());
            getPresenter().subscribeForEpisodeStream(mTargetPodcast);
        }

        @Override
        protected boolean onRefresh() {
            getPresenter().refreshEpisodes(mTargetPodcast);
            return true;
        }

        @NonNull
        @Override
        protected NestedEpisodeAdapter getAdapter() {
            return getPresenter().getAdapter();
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
            return new GridLayoutManager(context, spanCount);
        }

        @NonNull
        @Override
        protected EpisodesListPresenter onCreatePresenter() {
            return getDependencyResolver().getSubscriptionsComponent().createEpisodesListPresenter();
        }
    }
}
