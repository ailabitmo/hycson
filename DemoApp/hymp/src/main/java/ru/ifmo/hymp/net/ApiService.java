package ru.ifmo.hymp.net;

import com.google.gson.JsonObject;

import java.util.Map;

import retrofit2.adapter.rxjava.Result;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;
import rx.Single;

public interface ApiService {

    /**
     * Fetch resource from API as {@link JsonObject}
     *
     * @param url - url that point to resource
     */
    @GET
    Single<Result<JsonObject>> load(@Url String url);

    /**
     * Create new resource in API
     *
     * @param url - url to create resource
     */
    @POST
    @FormUrlEncoded
    Single<Result<JsonObject>> send(@Url String url, @FieldMap Map<String, String> params);
}
