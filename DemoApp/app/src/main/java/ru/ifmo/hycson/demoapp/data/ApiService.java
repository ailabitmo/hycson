package ru.ifmo.hycson.demoapp.data;

import com.google.gson.JsonObject;

import retrofit2.http.GET;
import retrofit2.http.Url;
import rx.Observable;

public interface ApiService {

    @GET
    Observable<JsonObject> fetchEntryPoint(@Url String entryPointUrl);
}
