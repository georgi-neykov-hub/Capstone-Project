package com.neykov.podcastportal.model.networking;

import com.neykov.podcastportal.model.entity.RemoteEpisodeData;
import com.neykov.podcastportal.model.entity.RemotePodcastData;
import com.neykov.podcastportal.model.entity.Tag;

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
    Observable<List<RemotePodcastData>> getTopPodcasts(@Path("count") int count);

    @GET("/api/2/tags/{count}.json")
    Observable<List<Tag>> getTopPodcastsTags(@Path("count") int count);

    @GET("/api/2/tag/{tag}/{count}.json")
    Observable<List<RemotePodcastData>> getPodcastsWithTag(@Path("tag") String tag, @Path("count") int count);

    @GET("/api/2/data/podcast.json")
    Observable<RemotePodcastData> getTopPodcastsTags(@Query("url") String podcastUrl);

    @GET("/api/2/data/episode.json")
    Observable<RemoteEpisodeData> getEpisodeForPodcast(@Query("podcast-url") String podcastUrl, @Query("episode-url") String episodeUrl);

    @GET("/search.json")
    Observable<List<RemotePodcastData>> searchPodcasts(@Query("q") String query);
}
