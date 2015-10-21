package com.neykov.podcastportal.view.explore.view;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.neykov.podcastportal.R;

public class ExplorePagerAdapter extends FragmentStatePagerAdapter {

    public static final int POSITION_POPULAR_PODCASTS = 0;
    public static final int POSITION_POPULAR_TAGS = 1;
    private String[] mPageTitles;

    public ExplorePagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mPageTitles = new String[]{
                context.getString(R.string.title_popular_podcasts),
                context.getString(R.string.title_popular_tags)
        };
    }

    @Override
    public Fragment getItem(int position) {
        if(position == POSITION_POPULAR_PODCASTS){
            return PopularPodcastsFragment.newInstance();
        }else if(position == POSITION_POPULAR_TAGS){
            return PopularTagsFragment.newInstance();
        }else {
            throw new IllegalArgumentException("Unknown pager position.");
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mPageTitles[position];
    }
}
