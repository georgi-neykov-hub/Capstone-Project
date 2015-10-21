package com.neykov.podcastportal.view.explore.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.neykov.podcastportal.R;
import com.neykov.podcastportal.model.entity.Tag;
import com.neykov.podcastportal.view.ViewUtils;
import com.neykov.podcastportal.view.base.fragment.BaseListViewFragment;
import com.neykov.podcastportal.view.base.fragment.ItemListView;
import com.neykov.podcastportal.view.base.adapter.OnItemClickListener;
import com.neykov.podcastportal.view.explore.presenter.PopularTagsPresenter;
import com.neykov.podcastportal.view.widget.GridSpaceItemDecoration;

public class PopularTagsFragment extends BaseListViewFragment<TagsAdapter, PopularTagsPresenter> implements ItemListView, OnItemClickListener {

    public interface OnTagSelectedListener{
        void onTagSelected(Tag tag);
    }

    public static PopularTagsFragment newInstance() {
        return new PopularTagsFragment();
    }

    private OnTagSelectedListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(getParentFragment() instanceof OnTagSelectedListener){
            mListener = (OnTagSelectedListener) getParentFragment();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getAdapter().getItemCount() == 0) {
            getPresenter().loadItems(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getAdapter().setOnItemClickListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getAdapter().setOnItemClickListener(null);
    }

    @NonNull
    @Override
    protected PopularTagsPresenter onCreatePresenter() {
        return getDependencyResolver().getDiscoverComponent()
                .createTopTagsPresenter();
    }

    @NonNull
    @Override
    protected TagsAdapter getAdapter() {
        return getPresenter().getAdapter();
    }

    @Override
    protected boolean onRefresh() {
        getPresenter().refreshData();
        return true;
    }

    @Override
    protected void onShowLoadError(int error) {
        switch (error){
            case ERROR_GENERAL:
                //noinspection ConstantConditions
                Snackbar.make(getView(), R.string.error_could_not_load_tags, Snackbar.LENGTH_LONG)
                        .setAction(R.string.label_retry, v -> getPresenter().loadItems(this))
                        .show();
                break;
            case ERROR_NETWORK:
                ViewUtils.getNoNetworkSnackbar(getContext(), getView()).show();
                break;
        }

    }

    @Override
    protected void onConfigureRecycleView(@NonNull RecyclerView view) {
        view.setItemAnimator(new DefaultItemAnimator());
        int spanCount = getResources().getInteger(R.integer.grid_column_count) + 1;
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
                context.getResources().getInteger(R.integer.grid_column_count) + 1,
                StaggeredGridLayoutManager.VERTICAL );
    }

    @Override
    public void onItemClick(int position) {
        Tag tag = getAdapter().getItem(position);
        if(mListener != null){
            mListener.onTagSelected(tag);
        }
    }
}
