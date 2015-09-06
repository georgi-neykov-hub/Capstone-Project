package com.neykov.podcastportal.model.networking;

import com.neykov.podcastportal.model.entity.Episode;
import com.neykov.podcastportal.model.entity.Podcast;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface GPodderService {

    /*
    * Directory API
    * */

    @GET("/toplist/{count}.json")
    Observable<List<Podcast>> getTopPodcasts(@Path("count") int count);

    @GET("/api/2/tags/{count}.json")
    Observable<List<String>> getTopPodcastsTags(@Path("count") int count);

    @GET("/api/2/tag/{tag}/{count}.json")
    Observable<List<Podcast>> getPodcastsWithTag(@Path("tag") String tag, @Path("count") int count);

    @GET("/api/2/data/podcast.json")
    Observable<Podcast> getTopPodcastsTags(@Query("url") String podcastUrl);

    @GET("/api/2/data/episode.json")
    Observable<Episode> getEpisodeForPodcast(@Query("podcast-url") String podcastUrl, @Query("episode-url") String episodeUrl);

    @GET("/search.json")
    Observable<List<Podcast>> searchPodcasts(@Query("q") String query);
}
