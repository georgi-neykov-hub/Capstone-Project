package com.neykov.podcastportal.view.discover;


import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.neykov.podcastportal.R;
import com.neykov.podcastportal.view.base.ToolbarFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExploreFragment extends ToolbarFragment {

    public static final String TAG = ExploreFragment.class.getSimpleName();

    public static ExploreFragment newInstance() {
        return new ExploreFragment();
    }

    private ViewPager mTabViewPager;
    private TabLayout mTabLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_explore, container, false);
        mTabLayout = (TabLayout) rootView.findViewById(R.id.tabLayout);
        mTabViewPager = (ViewPager) rootView.findViewById(R.id.pager);
        mTabViewPager.setAdapter(new HomePagerAdapter(getContext(), getChildFragmentManager()));
        mTabLayout.setupWithViewPager(mTabViewPager);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTabLayout = null;
        mTabViewPager = null;
    }

    @NonNull
    @Override
    protected Toolbar onSetToolbar(View view) {
        return (Toolbar) view.findViewById(R.id.toolbar);
    }

    @Override
    protected void onConfigureToolbar(Toolbar toolbar) {

    }
}
