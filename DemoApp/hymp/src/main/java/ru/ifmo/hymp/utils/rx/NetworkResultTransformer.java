package ru.ifmo.hymp.utils.rx;

import com.google.gson.JsonObject;

import retrofit2.adapter.rxjava.Result;
import rx.Observable;
import rx.Single;
import rx.functions.Action1;

public class NetworkResultTransformer implements Single.Transformer<Result<JsonObject>, Result<JsonObject>> {
    @Override
    public Single<Result<JsonObject>> call(Single<Result<JsonObject>> resultObservable) {
        return resultObservable.doOnSuccess(new Action1<Result<JsonObject>>() {
            @Override
            public void call(Result<JsonObject> result) {
                if (!result.response().isSuccessful()) {
                    throw new RuntimeException("Network error", result.error());
                }
            }
        });
    }
}