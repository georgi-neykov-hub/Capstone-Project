package com.neykov.podcastportal.model.entity.converter;

import android.content.ContentValues;
import android.database.Cursor;

public interface Converter<T> {
    ContentValues convert(T entity);
    T convert(Cursor valueCursor);
}
