package com.neykov.podcastportal.model.download;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.inject.Inject;

import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;
import rx.Observable;

/**
 * Created by Georgi on 11.9.2015 г..
 */
public class Downloader {

    private final OkHttpClient mClient;

    @Inject
    public Downloader(OkHttpClient mClient) {
        this.mClient = mClient;
    }

    public Observable<Long> downloadFile(String url, File destination){
        return Observable.create(subscriber -> {
            Request request = new Request.Builder()
                    .get()
                    .url(url)
                    .build();
            try {
                Response response = mClient.newCall(request).execute();
                Sink trackedSink = new ForwardingSink(Okio.sink(destination)) {
                    long totalBytesWritten = 0L;

                    @Override
                    public void write(Buffer source, long byteCount) throws IOException {
                        super.write(source, byteCount);
                        totalBytesWritten += byteCount;
                        subscriber.onNext(totalBytesWritten);
                    }

                    @Override
                    public void close() throws IOException {
                        super.close();
                        subscriber.onCompleted();
                    }
                };

                BufferedSink sink = Okio.buffer(trackedSink);
                sink.writeAll(response.body().source());
            } catch (IOException e) {
                subscriber.onError(e);
            }
        });
    }
}
