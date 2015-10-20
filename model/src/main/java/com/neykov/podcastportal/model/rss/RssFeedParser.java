package com.neykov.podcastportal.model.rss;

import android.text.TextUtils;
import android.util.Xml;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RssFeedParser {

    static final String TITLE = "title";
    static final String DESCRIPTION = "description";
    static final String ENCLOSURE = "enclosure";
    static final String ENCLOSURE_URL = "url";
    static final String ENCLOSURE_MIMETYPE = "type";
    static final String ENCLOSURE_LENGTH = "length";
    static final String RSS = "rss";
    static final String CHANNEL = "channel";
    static final String LINK = "link";
    static final String ITEM = "item";
    static final String PUB_DATE = "pubDate";

    private DateTimeFormatter mTimeFormat = DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss zzz");
    private DateTimeFormatter mISOTimeFormat = DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss Z");
    private DateTimeZone mUtcTimezone = DateTimeZone.UTC;

    public RSSFeed parse(InputStream source) throws XmlPullParserException, IOException {
        try {
            RssChannel channel = null;
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(source, null);
            parser.nextTag();
            parser.require(XmlPullParser.START_TAG, null, RSS);
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                switch (name) {
                    case CHANNEL:
                        channel = readChannel(parser);
                        break;
                    default:
                        skip(parser);
                        break;
                }
            }
            if(channel == null){
                throw new XmlPullParserException("No channel elements found.");
            }

            return new RSSFeed(channel);
        } finally {
            source.close();
        }
    }

    private RssChannel readChannel(XmlPullParser parser) throws IOException, XmlPullParserException {
        String title = null;
        String description = null;
        List<RssItem> itemList = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, null, CHANNEL);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case ITEM:
                    itemList.add(readEntry(parser));
                    break;
                case TITLE:
                    title = readTitle(parser);
                    break;
                case DESCRIPTION:
                    description = readDescription(parser);
                    break;
                default:
                    skip(parser);
                    break;
            }
        }
        return new RssChannel(title, description, itemList);
    }

    private RssItem readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, "item");
        String title = null;
        String description = null;
        Date pubDate = null;
        Content content = null;
        String link = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case TITLE:
                    title = readTitle(parser);
                    break;
                case DESCRIPTION:
                    description = readDescription(parser);
                    break;
                case PUB_DATE:
                    pubDate = readDatePublished(parser);
                    break;
                case ENCLOSURE:
                    content = readEnclosure(parser);
                    break;
                case LINK:
                    link = readLink(parser);
                    break;
                default:
                    skip(parser);
                    break;
            }
        }
        return new RssItem(title, description, content, pubDate, link);
    }

    private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, TITLE);
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, TITLE);
        return title;
    }

    private String readDescription(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, DESCRIPTION);
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, DESCRIPTION);
        return title;
    }

    private Date readDatePublished(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, PUB_DATE);
        Date date = readDate(parser);
        parser.require(XmlPullParser.END_TAG, null, PUB_DATE);
        return date;
    }

    private Content readEnclosure(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, ENCLOSURE);

        String url = null;
        String type = null;
        long length = 0;
        int attrCount = parser.getAttributeCount();
        for (int index = 0; index < attrCount; index++) {
            String attrName = parser.getAttributeName(index);
            switch (attrName) {
                case ENCLOSURE_URL:
                    url = parser.getAttributeValue(index);
                    break;
                case ENCLOSURE_MIMETYPE:
                    type = parser.getAttributeValue(index);
                    break;
                case ENCLOSURE_LENGTH:
                    length = Long.parseLong(parser.getAttributeValue(index));
                    break;
            }
        }
        parser.nextTag();
        if (TextUtils.isEmpty(url)) {
            throw new XmlPullParserException("Null or empty enclosure url attribute.");
        }

        return new Content(url, type, length);
    }

    private String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, LINK);
        String link = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, LINK);
        return link;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private Date readDate(XmlPullParser parser) throws IOException, XmlPullParserException {
        String value = readText(parser);
        DateTime originalTime;
        try {
            originalTime = mTimeFormat.parseDateTime(value);
        } catch (IllegalArgumentException e) {
            originalTime = mISOTimeFormat.parseDateTime(value);
        }
        DateTime utcTime = originalTime.toDateTime(mUtcTimezone);
        return new Date(utcTime.getMillis());
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}