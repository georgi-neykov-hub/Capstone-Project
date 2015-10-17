package com.neykov.podcastportal.model.entity.converter;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.simpleframework.xml.transform.Transform;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RSSDateConverter implements Transform<Date> {

    private DateTimeFormatter mTimeFormat = DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss zzz");
    private DateTimeFormatter mISOTimeFormat = DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss Z");
    private DateTimeZone mUtcTimezone = DateTimeZone.UTC;

    @Override
    public Date read(String value) throws Exception {
        DateTime originalTime;
        try {
             originalTime = mTimeFormat.parseDateTime(value);
        } catch (IllegalArgumentException e){
            originalTime = mISOTimeFormat.parseDateTime(value);
        }
        DateTime utcTime = originalTime.toDateTime(mUtcTimezone);
        return new Date(utcTime.getMillis());
    }

    @Override
    public String write(Date value) throws Exception {
        throw new UnsupportedOperationException();
    }
}
