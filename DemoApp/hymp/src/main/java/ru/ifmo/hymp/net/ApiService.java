package ru.ifmo.hymp.net;

import com.google.gson.JsonObject;

import retrofit2.adapter.rxjava.Result;
import retrofit2.http.GET;
import retrofit2.http.Url;
import rx.Observable;
import rx.Single;

public interface ApiService {

    /**
     * Fetch resource from API as {@link JsonObject}
     *
     * @param url - url that point to resource
     */
    @GET
    Single<Result<JsonObject>> load(@Url String url);
}
