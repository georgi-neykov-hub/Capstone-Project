package com.neykov.podcastportal.view.subscriptions.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.neykov.podcastportal.R;
import com.neykov.podcastportal.view.ViewUtils;
import com.neykov.podcastportal.view.base.fragment.ToolbarViewFragment;
import com.neykov.podcastportal.view.subscriptions.presenter.MyPodcastsPresenter;
import com.neykov.podcastportal.view.widget.DividerItemDecoration;

public class MyPodcastsFragment extends ToolbarViewFragment<MyPodcastsPresenter> implements MyPodcastsView{

    public static final String TAG = MyPodcastsFragment.class.getSimpleName();
    private static final String KEY_LAYOUT_MANAGER_STATE = "MyPodcastsFragment.KEY_LAYOUT_MANAGER_STATE";

    public static MyPodcastsFragment newInstance() {
        return new MyPodcastsFragment();
    }

    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;

    @NonNull
    @Override
    protected MyPodcastsPresenter onCreatePresenter() {
        return getDependencyResolver().getSubscriptionsComponent().createMyPodcastsPresenter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_podcasts, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.podcastsList);
        configureRecycleView(mRecyclerView, savedInstanceState);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRecyclerView.setAdapter(null);
        mRecyclerView = null;
        mLayoutManager = null;
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if(mLayoutManager != null) {
            bundle.putParcelable(KEY_LAYOUT_MANAGER_STATE, mLayoutManager.onSaveInstanceState());
        }
    }

    @Nullable
    @Override
    protected Toolbar onSetToolbar(View view) {
        return (Toolbar) view.findViewById(R.id.toolbar);
    }

    protected void configureRecycleView(@NonNull RecyclerView view, Bundle savedState) {
        mLayoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        if(savedState != null && savedState.containsKey(KEY_LAYOUT_MANAGER_STATE)){
            mLayoutManager.onRestoreInstanceState(savedState.getParcelable(KEY_LAYOUT_MANAGER_STATE));
        }
        view.setLayoutManager(mLayoutManager);

        int dividerRes = ViewUtils.getThemeAttribute(view.getContext().getTheme(), R.attr.dividerHorizontal);
        DividerItemDecoration spaceDecoration = new DividerItemDecoration(view.getContext(), dividerRes, DividerItemDecoration.VERTICAL_LIST);
        view.addItemDecoration(spaceDecoration);
        view.setItemAnimator(new DefaultItemAnimator());
        view.setAdapter(getPresenter().getAdapter());
    }

    @Override
    public void showLoadingIndicator() {

    }

    @Override
    public void hideLoadingIndicator() {

    }

    @Override
    public void showError(int errorType) {

    }
}
