package com.neykov.podcastportal.view.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.neykov.podcastportal.DependencyResolver;
import com.neykov.podcastportal.R;
import com.neykov.podcastportal.view.base.fragment.FragmentStack;

import nucleus.view.ViewWithPresenter;

public class BaseActivity extends AppCompatActivity {

    private FragmentStack mFragmentStack;
    private DependencyResolver mDependencyResolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDependencyResolver = ((DependencyResolverProvider)getApplicationContext()).getDependencyResolver();
        mFragmentStack = new FragmentStack(
                this,
                getSupportFragmentManager(),
                R.id.fragmentContainer,
                mOnFragmentRemovedListener);
    }

    @Override
    public void onBackPressed() {
        if (!mFragmentStack.pop()) {
            super.onBackPressed();
        }
        if(isFinishing()){
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    public final DependencyResolver getDependencyResolver() {
        return mDependencyResolver;
    }

    public FragmentStack getFragmentStack(){
        return mFragmentStack;
    }

    private final FragmentStack.OnFragmentRemovedListener mOnFragmentRemovedListener = fragment -> {
        if (fragment instanceof ViewWithPresenter)
            ((ViewWithPresenter)fragment).getPresenter().destroy();
    };

}
