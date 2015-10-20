package com.neykov.podcastportal.view.base.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.neykov.podcastportal.DependencyResolver;
import com.neykov.podcastportal.view.base.DependencyResolverProvider;

import nucleus.factory.PresenterFactory;
import nucleus.factory.ReflectionPresenterFactory;
import nucleus.presenter.Presenter;
import nucleus.view.PresenterLifecycleDelegate;
import nucleus.view.ViewWithPresenter;

public abstract class BaseViewFragment<PresenterType extends Presenter> extends Fragment implements ViewWithPresenter<PresenterType>, DependencyResolverProvider {

    private static final String PRESENTER_STATE_KEY = "presenter_state";
    private PresenterLifecycleDelegate<PresenterType> presenterDelegate = new PresenterLifecycleDelegate<>(this::onCreatePresenter);

    /**
     * Returns a current presenter factory.
     */
    public PresenterFactory<PresenterType> getPresenterFactory() {
        return presenterDelegate.getPresenterFactory();
    }

    /**
     * Sets a presenter factory.
     * Call this method before onCreate/onFinishInflate to override default {@link ReflectionPresenterFactory} presenter factory.
     * Use this method for presenter dependency injection.
     */
    @Override
    public void setPresenterFactory(PresenterFactory<PresenterType> presenterFactory) {
        presenterDelegate.setPresenterFactory(presenterFactory);
    }

    /**
     * Returns a current attached presenter.
     * This method is guaranteed to return a non-null value between
     * onResume/onPause and onAttachedToWindow/onDetachedFromWindow calls
     * if the presenter factory returns a non-null value.
     *
     * @return a currently attached presenter or null.
     */
    public PresenterType getPresenter() {
        return presenterDelegate.getPresenter();
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        if (bundle != null) {
            presenterDelegate.onRestoreInstanceState(bundle.getBundle(PRESENTER_STATE_KEY));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBundle(PRESENTER_STATE_KEY, presenterDelegate.onSaveInstanceState());
    }

    @Override
    public void onResume() {
        super.onResume();
        presenterDelegate.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        presenterDelegate.onPause(getActivity().isFinishing());
    }

    /**
     * This will be called to obtain an instance of PresenterType for this view.
     *
     * <p>This method will be called every time a new {@link PresenterType} instance is needed.
     * New instances can be created by hand, e.g. calling a constructor or obtained/injected from
     * {@linkplain DependencyResolver}</p>
     * @see #getDependencyResolver()
     * @see DependencyResolver
     *
     * @return an new instance of PresenterType
     */
    protected abstract @NonNull
    PresenterType onCreatePresenter();

    @NonNull
    @Override
    public DependencyResolver getDependencyResolver() {
        return ((DependencyResolverProvider)getContext().getApplicationContext())
                .getDependencyResolver();
    }
}