package com.neykov.podcastportal.view.explore.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.neykov.podcastportal.R;
import com.neykov.podcastportal.model.entity.RemotePodcastData;
import com.neykov.podcastportal.model.entity.PodcastSubscription;
import com.neykov.podcastportal.model.entity.Tag;
import com.neykov.podcastportal.view.ViewUtils;
import com.neykov.podcastportal.view.base.fragment.BaseListViewFragment;
import com.neykov.podcastportal.view.base.fragment.ToolbarFragment;
import com.neykov.podcastportal.view.explore.presenter.PodcastsForTagPresenter;

public class PodcastsForTagFragment extends ToolbarFragment {

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
        if (mTargetTag == null) {
            throw new IllegalArgumentException("Tag argument is null.");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_podcasts_for_tag, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ContentFragment fragment = (ContentFragment) getChildFragmentManager()
                .findFragmentById(R.id.podcastsForTagContentFragment);
        fragment.setTargetTag(mTargetTag);
    }

    @Nullable
    @Override
    protected Toolbar onSetToolbar(View view) {
        return (Toolbar) view.findViewById(R.id.toolbar);
    }

    @Override
    protected void onConfigureToolbar(Toolbar toolbar) {
        super.onConfigureToolbar(toolbar);
        toolbar.setSubtitle(mTargetTag.getTitle());
        setHomeAsUpEnabled(true);
    }

    public static class ContentFragment extends BaseListViewFragment<PodcastsAdapter, PodcastsForTagPresenter> implements DiscoverPodcastsView {

        private Tag mTargetTag;

        public void setTargetTag(@NonNull Tag targetTag) {
            this.mTargetTag = targetTag;
            getPresenter().setTargetTag(mTargetTag.getTag());
            getPresenter().loadItems(this);
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
            if (error == ERROR_NETWORK) {
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

        @Override
        public void onPodcastSubcribed(PodcastSubscription podcast) {

        }

        @Override
        public void onPodcastUnsubscribed(RemotePodcastData podcast) {

        }

        private final PodcastsAdapter.PodcastItemListener mItemListener = new PodcastsAdapter.PodcastItemListener() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onItemSubscribeClick(int position) {
                RemotePodcastData podcast = getAdapter().getItem(position);
                if (podcast instanceof PodcastSubscription) {
                    getPresenter().unsubscribeFromPodcast(position, (PodcastSubscription) podcast);
                } else {
                    getPresenter().subscribeForPodcast(position, podcast);
                }
            }

            @Override
            public void onItemShareClick(int position) {

            }
        };
    }
}
