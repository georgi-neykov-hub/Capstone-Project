package com.neykov.podcastportal.model.entity.converter;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.database.Cursor;

public interface TransactionConverter<T>  {

    ContentProviderOperation convertToInsertOperation(T value);
    ContentProviderOperation convertToDeleteOperation(T value);
    ContentProviderOperation convertToUpdateOperation(T value);
}
