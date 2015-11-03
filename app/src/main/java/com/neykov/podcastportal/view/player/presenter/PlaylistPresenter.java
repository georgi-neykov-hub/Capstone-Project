package com.neykov.podcastportal.view.player.presenter;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.neykov.podcastportal.model.entity.PlaylistEntry;
import com.neykov.podcastportal.model.playlist.PlaylistManager;
import com.neykov.podcastportal.view.base.BasePresenter;
import com.neykov.podcastportal.view.player.view.PlaylistItemTouchCallback;
import com.neykov.podcastportal.view.player.view.PlaylistView;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PlaylistPresenter extends BasePresenter<PlaylistView> implements PlaylistItemTouchCallback.PlaylistItemMoveHandler {

    public static final int PLAYLIST_STREAM_RESTARTABLE_ID = 1;
    private PlaylistManager mPlaylistManager;
    private PlaylistEntryAdapter mAdapter;

    @Inject
    public PlaylistPresenter(PlaylistManager manager) {
        this.mPlaylistManager = manager;
        mAdapter = new PlaylistEntryAdapter();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        mAdapter.moveItem(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        PlaylistEntry entry = getAdapter().getItem(position);
        getAdapter().removeItem(position);
        remove(entry);
    }

    @Override
    public void onItemMoveCompleted(int currentPosition, int originalPosition) {
        if(mAdapter.getItemCount() == 1){
            return;
        }
        PlaylistEntry target = getAdapter().getItem(currentPosition);
        if(currentPosition > 0){
            PlaylistEntry aboveEntry = getAdapter().getItem(currentPosition - 1);
            moveAfter(target, aboveEntry);
        }else {
            PlaylistEntry belowEntry = getAdapter().getItem(1);
            moveBefore(target, belowEntry);
        }
    }

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        this.restartable(PLAYLIST_STREAM_RESTARTABLE_ID, () -> mPlaylistManager.getPlaylistStream()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(throwable1 -> new ArrayList<>(0))
                .subscribe(mAdapter::setData));
        this.start(PLAYLIST_STREAM_RESTARTABLE_ID);
    }

    public PlaylistEntryAdapter getAdapter(){
        return mAdapter;
    }

    public void moveAfter(PlaylistEntry entry, PlaylistEntry anchor){
        this.add(mPlaylistManager.moveAfter(entry, anchor).subscribe());
    }


    public void moveBefore(PlaylistEntry entry, PlaylistEntry anchor){
        this.add(mPlaylistManager.moveBefore(entry, anchor).subscribe());
    }


    public void remove(PlaylistEntry entry){
        this.add(mPlaylistManager.remove(entry).subscribe());
    }
}
