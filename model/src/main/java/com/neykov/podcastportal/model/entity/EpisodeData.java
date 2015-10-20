package com.neykov.podcastportal.model.entity;

import java.util.Date;

/**
 * Created by Georgi on 17.10.2015 г..
 */
public interface EpisodeData {
    String getTitle();

    String getUrl();

    String getDescription();

    Date getReleased();
}
