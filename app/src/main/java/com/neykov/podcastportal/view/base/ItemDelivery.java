package com.neykov.podcastportal.view.base;


import android.support.annotation.Nullable;

import rx.Notification;
import rx.functions.Action0;
import rx.functions.Action2;

public class ItemDelivery<View, T> {

    private final View view;
    private final Notification<T> notification;

    public ItemDelivery(View view, Notification<T> notification) {
        this.view = view;
        this.notification = notification;
    }

    public void split(Action2<View, T> onNext, @Nullable Action2<View, Throwable> onError) {
        split(onNext, onError, null);
    }

    public void split(Action2<View, T> onNext, @Nullable Action2<View, Throwable> onError, @Nullable Action0 onCompleted) {
        if (notification.getKind() == Notification.Kind.OnNext)
            onNext.call(view, notification.getValue());
        else if (onError != null && notification.getKind() == Notification.Kind.OnError)
            onError.call(view, notification.getThrowable());
        else if(onCompleted != null && notification.getKind() == Notification.Kind.OnCompleted){
            onCompleted.call();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemDelivery<?, ?> delivery = (ItemDelivery<?, ?>) o;

        if (view != null ? !view.equals(delivery.view) : delivery.view != null) return false;
        return !(notification != null ? !notification.equals(delivery.notification) : delivery.notification != null);
    }

    @Override
    public int hashCode() {
        int result = view != null ? view.hashCode() : 0;
        result = 31 * result + (notification != null ? notification.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Delivery{" +
                "view=" + view +
                ", notification=" + notification +
                '}';
    }
}
