package com.neykov.podcastportal.view.explore.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnCloseListener;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.neykov.podcastportal.R;
import com.neykov.podcastportal.model.entity.RemotePodcastData;
import com.neykov.podcastportal.model.entity.PodcastSubscription;
import com.neykov.podcastportal.view.ViewUtils;
import com.neykov.podcastportal.view.base.fragment.BaseListViewFragment;
import com.neykov.podcastportal.view.base.fragment.ToolbarFragment;
import com.neykov.podcastportal.view.explore.presenter.PodcastSearchPresenter;
import com.neykov.podcastportal.view.widget.GridSpaceItemDecoration;

import java.util.concurrent.TimeUnit;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

public class PodcastSearchFragment extends ToolbarFragment implements SearchView.OnQueryTextListener, OnCloseListener {

    public static final String TAG = PodcastSearchFragment.class.getSimpleName();

    public static PodcastSearchFragment newInstance() {
        return new PodcastSearchFragment();
    }

    private SearchView mSearchView;
    private ContentFragment mContentFragment;
    private Subject<String, String> mQuerySubject = PublishSubject.create();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_podcast, container, false);
        mSearchView = (SearchView) rootView.findViewById(R.id.search);
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnCloseListener(this);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnSearchClickListener(v -> onQueryTextSubmit(mSearchView.getQuery().toString()));
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContentFragment = (ContentFragment) getChildFragmentManager().findFragmentById(R.id.podcastSearchContentFragment);
        mQuerySubject
                .debounce(1300, TimeUnit.MILLISECONDS, Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mQuerySubscriber);
    }

    @Override
    public void onDestroyView() {
        mContentFragment = null;
        mSearchView = null;
        mQuerySubscriber.unsubscribe();
        super.onDestroyView();
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
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (mContentFragment != null) {
            mContentFragment.setSearchQuery(query);
            ViewUtils.hideSoftwareKeyboard(getActivity());
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mQuerySubject.onNext(newText);
        return true;
    }

    private final Subscriber<String> mQuerySubscriber = new Subscriber<String>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(String s) {
            if (mContentFragment != null) {
                mContentFragment.setSearchQuery(s);
            }
        }
    };

    @Override
    public boolean onClose() {
        getFragmentManager().popBackStack();
        return true;
    }

    public static class ContentFragment extends BaseListViewFragment<PodcastsAdapter, PodcastSearchPresenter> implements DiscoverPodcastsView{

        @NonNull
        @Override
        protected PodcastSearchPresenter onCreatePresenter() {
            return getDependencyResolver().getDiscoverComponent().createPodcastSearchPresenter();
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

        protected void setSearchQuery(String query) {
            getPresenter().setQuery(query);
            getPresenter().refreshData();
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
