package com.neykov.podcastportal.view.discover.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.neykov.podcastportal.R;
import com.neykov.podcastportal.model.entity.Tag;
import com.neykov.podcastportal.view.ViewUtils;
import com.neykov.podcastportal.view.base.BaseViewFragment;
import com.neykov.podcastportal.view.discover.presenter.PopularTagsPresenter;

import java.util.List;

public class PopularTagsFragment extends BaseViewFragment<PopularTagsPresenter> implements PopularTagsView {

    private static final String KEY_ADAPTER_STATE = "TagsFragment.KEY_ADAPTER_STATE";

    public static PopularTagsFragment newInstance() {
        return new PopularTagsFragment();
    }

    private RecyclerView mTagsRecyclerView;
    private View mLoadingView;
    private SwipeRefreshLayout mRefreshLayout;
    private TagsAdapter mTagsAdapter;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mTagsAdapter = new TagsAdapter();
        if (bundle != null) {
            //noinspection ConstantConditions
            mTagsAdapter.onRestoreInstanceState(bundle.getParcelable(KEY_ADAPTER_STATE));
        }

        if (mTagsAdapter.isEmpty()) {
            getPresenter().getTopPodcastTags();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_top_tags, container, false);
        mTagsRecyclerView = (RecyclerView) rootView.findViewById(R.id.tagsList);
        mTagsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mTagsRecyclerView.setAdapter(mTagsAdapter);
        mTagsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mLoadingView = rootView.findViewById(R.id.loadingIndicator);
        mRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefresh);
        int colorAccent = ViewUtils.getThemeAttribute(getContext().getTheme(), R.attr.colorAccent);
        mRefreshLayout.setColorSchemeResources(colorAccent);
        mRefreshLayout.setOnRefreshListener(() -> getPresenter().getTopPodcastTags());
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTagsRecyclerView = null;
        mLoadingView = null;
        mRefreshLayout = null;
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putParcelable(KEY_ADAPTER_STATE, mTagsAdapter.onSaveInstanceState());
    }

    @NonNull
    @Override
    protected PopularTagsPresenter onCreatePresenter() {
        return getDependencyResolver().getDiscoverComponent()
                .createTopTagsPresenter();
    }

    @Override
    public void onTagsLoaded(List<Tag> tags) {
        mTagsAdapter.addItems(tags);
    }

    @Override
    public void onTagsLoadFailed() {
        mRefreshLayout.setRefreshing(false);
        //noinspection ConstantConditions
        Snackbar.make(getView(), R.string.error_could_not_load_tags, Snackbar.LENGTH_LONG)
                .setAction(R.string.label_retry, v -> getPresenter().getTopPodcastTags())
                .show();
    }

    @Override
    public void showLoadingIndicator() {
        mLoadingView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoadingIndicator() {
        mLoadingView.setVisibility(View.INVISIBLE);
    }
}
