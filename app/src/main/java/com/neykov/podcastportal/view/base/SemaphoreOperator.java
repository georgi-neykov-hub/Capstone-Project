package com.neykov.podcastportal.view.base;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class SemaphoreOperator<T> implements Observable.Operator<T, T>{

    private Observable<Boolean> permissionObservable;

    public SemaphoreOperator(Observable<Boolean> permissionObservable) {
        this.permissionObservable = permissionObservable;
    }

    @Override
    public Subscriber<? super T> call(Subscriber<? super T> childSubscriber) {
        return new Subscriber<T>() {

            List<T> onNextItems = new ArrayList<>();
            Throwable error = null;

            boolean childSubcriberHasCompleted = false;
            boolean permitEmmissions = false;
            boolean hasTerminated = false;

            @Override
            public void onStart() {
                super.onStart();
                childSubscriber.add(permissionObservable.subscribe(permitEmmissions -> {
                    this.permitEmmissions = permitEmmissions;
                }));
                childSubscriber.add(this);
            }

            @Override
            public void onCompleted() {
                childSubcriberHasCompleted = true;
                unsubscribe();
                flushNotifications();
            }

            @Override
            public void onError(Throwable e) {
                error = e;
                flushNotifications();
            }

            @Override
            public void onNext(T viewTDelivery) {
                onNextItems.add(viewTDelivery);
                flushNotifications();
            }

            private boolean deliveriesAllowed(){
                return !childSubscriber.isUnsubscribed() && permitEmmissions && !hasTerminated;
            }

            private void flushNotifications() {
                if (deliveriesAllowed()){

                    while (onNextItems.size()>0){
                        T value = onNextItems.remove(0);
                        childSubscriber.onNext(value);
                    }

                    if(childSubcriberHasCompleted){
                        childSubscriber.onCompleted();
                        hasTerminated = true;
                    }

                    if(error != null){
                        childSubscriber.onError(error);
                        hasTerminated = true;
                        error = null;
                    }
                }
            }
        };
    }

}
