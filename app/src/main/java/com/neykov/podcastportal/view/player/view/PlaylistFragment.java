package com.neykov.podcastportal.view.player.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.neykov.podcastportal.R;
import com.neykov.podcastportal.view.ViewUtils;
import com.neykov.podcastportal.view.base.fragment.BaseListViewFragment;
import com.neykov.podcastportal.view.base.fragment.BaseViewFragment;
import com.neykov.podcastportal.view.player.presenter.PlaylistEntryAdapter;
import com.neykov.podcastportal.view.player.presenter.PlaylistPresenter;
import com.neykov.podcastportal.view.widget.DividerItemDecoration;
import com.neykov.podcastportal.view.widget.SpaceItemDecoration;

public class PlaylistFragment extends BaseListViewFragment<PlaylistEntryAdapter, PlaylistPresenter> implements PlaylistView {

    private RecyclerView mItemsRecyclerView;

    @NonNull
    @Override
    protected PlaylistPresenter onCreatePresenter() {
        return getDependencyResolver().getPlayerComponent().createPlaylistPresenter();
    }

    @NonNull
    @Override
    protected PlaylistEntryAdapter getAdapter() {
        return getPresenter().getAdapter();
    }

    @Override
    protected void onShowLoadError(int error) {

    }

    @Override
    protected void onConfigureRecycleView(@NonNull RecyclerView view) {
    }

    @NonNull
    @Override
    protected RecyclerView.LayoutManager onCreateLayoutManager(@NonNull Context context) {
        return new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
    }
}
