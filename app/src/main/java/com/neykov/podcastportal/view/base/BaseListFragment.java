package com.neykov.podcastportal.view.base;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.neykov.podcastportal.DependencyResolver;
import com.neykov.podcastportal.R;
import com.neykov.podcastportal.view.ViewUtils;

public abstract class BaseListFragment<A extends RecyclerView.Adapter> extends Fragment implements ItemListView, DependencyResolverProvider {

    private static final String TAG = BaseListFragment.class.getSimpleName();
    private static final String KEY_LAYOUT_MANAGER_STATE = "BaseListFragment.KEY_LAYOUT_MANAGER_STATE";
    private static final String KEY_EMPTY_VIEW_STATE = "BaseListFragment.KEY_EMPTY_VIEW_STATE";
    private static final String KEY_ERROR_VIEW_STATE = "BaseListFragment.KEY_ERROR_VIEW_STATE";

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView.LayoutManager mLayoutManager;
    private FrameLayout mEmptyViewContainer;
    private View mEmptyView;
    private View mEmptyErrorView;
    private ContentLoadingProgressBar mLoadingView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_base_list, container, false);
        initializeViewReferences(rootView);
        configureEmptyViews(savedInstanceState);
        configureRecycleView(savedInstanceState);
        setEventListeners();
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if(mLayoutManager != null) bundle.putParcelable(KEY_LAYOUT_MANAGER_STATE, mLayoutManager.onSaveInstanceState());
        if (mEmptyErrorView != null) bundle.putBoolean(KEY_ERROR_VIEW_STATE, mEmptyErrorView.getVisibility() == View.VISIBLE);
        if (mEmptyView != null) bundle.putBoolean(KEY_EMPTY_VIEW_STATE, mEmptyView.getVisibility() == View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRecyclerView.setAdapter(null);
        mRecyclerView = null;
        mLayoutManager = null;
        mSwipeRefreshLayout = null;
    }

    @Override
    public void showLoadingIndicator() {
        mLoadingView.show();
        toggleEmptyView(false);
        toggleEmptyErrorView(false);
    }

    @Override
    public void hideLoadingIndicator() {
        mLoadingView.hide();
        if(mRecyclerView.getAdapter().getItemCount() == 0){
            toggleEmptyView(true);
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showListLoadError(int errorType) {
        if(mRecyclerView.getAdapter().getItemCount() == 0){
            toggleEmptyView(false);
            toggleEmptyErrorView(true);
        }
        onShowLoadError(errorType);
    }

    @NonNull
    @Override
    public DependencyResolver getDependencyResolver() {
        return ((DependencyResolverProvider)getContext().getApplicationContext())
                .getDependencyResolver();
    }

    protected abstract @NonNull A getAdapter();

    protected abstract void onRefresh();

    protected abstract void onShowLoadError(int error);

    @SuppressWarnings("UnusedParameters")
    protected @Nullable View onCreateEmptyView(LayoutInflater inflater, ViewGroup parent){
        return null;
    }

    @SuppressWarnings("UnusedParameters")
    protected @Nullable View onCreateEmptyErrorView(LayoutInflater inflater, ViewGroup parent){
        return null;
    }

    protected abstract void onConfigureRecycleView(@NonNull RecyclerView view);

    protected abstract @NonNull RecyclerView.LayoutManager onCreateLayoutManager(@NonNull Context context);

    @Nullable protected SwipeRefreshLayout getSwipeRefreshLayout(){
        return mSwipeRefreshLayout;
    }

    private void initializeViewReferences(View rootView){
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh);
        mEmptyViewContainer = (FrameLayout) rootView.findViewById(R.id.emptyViewContainer);
        mLoadingView = (ContentLoadingProgressBar) rootView.findViewById(R.id.loadingIndicator);
        int colorAccent = ViewUtils.getThemeAttribute(getContext().getTheme(), R.attr.colorAccent);
        mSwipeRefreshLayout.setColorSchemeResources(colorAccent);
    }

    private void setEventListeners(){
        mSwipeRefreshLayout.setOnRefreshListener(this::refreshContent);
    }

    private void configureRecycleView(Bundle savedInstanceState){
        mLayoutManager = createLayoutManager();
        if(savedInstanceState != null){
            Parcelable layoutManagerState = savedInstanceState.getParcelable(KEY_LAYOUT_MANAGER_STATE);
            if(layoutManagerState != null) mLayoutManager.onRestoreInstanceState(layoutManagerState);
        }
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(getAdapter());
        onConfigureRecycleView(mRecyclerView);
    }

    @NonNull
    private RecyclerView.LayoutManager createLayoutManager() {
        return onCreateLayoutManager(getContext());
    }

    @Nullable
    private View createEmptyView() {
        return onCreateEmptyView(LayoutInflater.from(mEmptyViewContainer.getContext()), mEmptyViewContainer);
    }

    @Nullable
    private View createEmptyErrorView() {
        return onCreateEmptyErrorView(LayoutInflater.from(mEmptyViewContainer.getContext()), mEmptyViewContainer);
    }

    private void refreshContent(){
        toggleEmptyView(false);
        toggleEmptyErrorView(false);
        onRefresh();
    }

    private void configureEmptyViews(@Nullable Bundle savedInstanceState) {
        mEmptyView = createEmptyView();
        mEmptyErrorView = createEmptyErrorView();
        mEmptyViewContainer.removeAllViews();

        if(mEmptyView != null){
            configureEmptyViewLayoutParams(mEmptyView);
            mEmptyViewContainer.addView(mEmptyView);
            boolean shouldShow = false;
            if (savedInstanceState != null) {
                shouldShow = savedInstanceState.getBoolean(KEY_EMPTY_VIEW_STATE, false);
            }

            toggleEmptyView(shouldShow);
        }

        if(mEmptyErrorView != null){
            configureEmptyViewLayoutParams(mEmptyErrorView);
            mEmptyViewContainer.addView(mEmptyErrorView);
            boolean shouldShow = false;
            if (savedInstanceState != null) {
                shouldShow = savedInstanceState.getBoolean(KEY_ERROR_VIEW_STATE , false);
            }

            toggleEmptyErrorView(shouldShow);
        }
    }

    private void configureEmptyViewLayoutParams(@NonNull View emptyView) {
        ViewGroup.LayoutParams params = emptyView.getLayoutParams();
        if(params == null){
            Log.e(TAG, "Empty view has null LayoutParams.");
            params = createDefaultEmptyViewParams();
            emptyView.setLayoutParams(params);
        } else if (!(params instanceof FrameLayout.LayoutParams)){
            Log.e(TAG, "Empty view not using the correct LayoutParams type.");
            params = createDefaultEmptyViewParams();
            emptyView.setLayoutParams(params);
        } else {
            ((FrameLayout.LayoutParams) params).gravity = Gravity.CENTER;
        }
    }

    private ViewGroup.LayoutParams createDefaultEmptyViewParams(){
        return new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER);
    }

    private void toggleEmptyView(boolean show){
        if(mEmptyView != null) {
            int visibility = show ? View.VISIBLE : View.INVISIBLE;
            mEmptyView.setVisibility(visibility);
        }
    }

    private void toggleEmptyErrorView(boolean show){
        if(mEmptyErrorView != null) {
            int visibility = show ? View.VISIBLE : View.INVISIBLE;
            mEmptyErrorView.setVisibility(visibility);
        }
    }
}
