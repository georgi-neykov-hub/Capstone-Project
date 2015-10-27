package com.neykov.podcastportal.view.player.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

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

    private final ItemTouchHelper.Callback mCallback = new ItemTouchHelper.Callback() {
        @Override
        public boolean isLongPressDragEnabled() {
            return true;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return true;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            int position = viewHolder.getAdapterPosition();
            if(position != RecyclerView.NO_POSITION) {
                getPresenter().remove(getAdapter().getItem(position));
            }
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            if(position != RecyclerView.NO_POSITION) {
                getPresenter().remove(getAdapter().getItem(position));
            }
        }
    };
}
