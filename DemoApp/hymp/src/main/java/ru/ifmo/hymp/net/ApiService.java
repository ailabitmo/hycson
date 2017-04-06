package ru.ifmo.hymp.net;

import com.google.gson.JsonObject;

import retrofit2.adapter.rxjava.Result;
import retrofit2.http.GET;
import retrofit2.http.Url;
import rx.Observable;

public interface ApiService {

    /**
     * Fetch {@link JsonObject} from api
     */
    @GET
    Observable<Result<JsonObject>> load(@Url String url);

}
