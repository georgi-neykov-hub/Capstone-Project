package com.neykov.podcastportal.view.base;

import nucleus.presenter.RxPresenter;
import nucleus.presenter.delivery.Delivery;
import rx.Observable;

public class BasePresenter<V> extends RxPresenter<V> {

    private Observable<Boolean> viewAvailable;

    protected BasePresenter(){
        super();
        viewAvailable = view().asObservable().map(v -> v != null);
    }

    protected Observable<Boolean> viewStatus(){
        return viewAvailable;
    }

    protected <T> Observable.Transformer<T, Delivery<V,T>> delayUntilViewAvailable(){
        return tObservable -> tObservable.lift(
                new SemaphoreOperator<>(viewStatus()))
                .materialize()
                .filter(notification -> !notification.isOnCompleted())
                .withLatestFrom(view(), (notification, v) -> new Delivery<>(v, notification));
    }
}
