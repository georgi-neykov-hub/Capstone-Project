package com.neykov.podcastportal.view.player.presenter;

import android.os.Bundle;

import com.neykov.podcastportal.model.entity.PlaylistEntry;
import com.neykov.podcastportal.model.playlist.PlaylistManager;
import com.neykov.podcastportal.view.base.BasePresenter;
import com.neykov.podcastportal.view.player.view.PlaylistView;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PlaylistPresenter extends BasePresenter<PlaylistView> {

    public static final int PLAYLIST_STREAM_RESTARTABLE_ID = 1;
    private PlaylistManager mPlaylistManager;
    private PlaylistEntryAdapter mAdapter;

    @Inject
    public PlaylistPresenter(PlaylistManager manager) {
        this.mPlaylistManager = manager;
        mAdapter = new PlaylistEntryAdapter();
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
}
