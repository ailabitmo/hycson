package ru.ifmo.hymp.utils.rx;

import com.google.gson.JsonObject;

import retrofit2.adapter.rxjava.Result;
import rx.Observable;
import rx.functions.Action1;

public class NetworkResultTransformer implements Observable.Transformer<Result<JsonObject>, Result<JsonObject>> {
    @Override
    public Observable<Result<JsonObject>> call(Observable<Result<JsonObject>> resultObservable) {
        return resultObservable.doOnNext(new Action1<Result<JsonObject>>() {
            @Override
            public void call(Result<JsonObject> result) {
                if (result.isError()) {
                    throw new RuntimeException("Network error", result.error());
                }
            }
        });
    }
}