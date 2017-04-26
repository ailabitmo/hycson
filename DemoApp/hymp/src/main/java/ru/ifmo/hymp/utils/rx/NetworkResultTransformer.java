package ru.ifmo.hymp.utils.rx;

import com.google.gson.JsonObject;

import retrofit2.Response;
import retrofit2.adapter.rxjava.Result;
import ru.ifmo.hymp.net.NetworkException;
import rx.Single;
import rx.functions.Action1;

public class NetworkResultTransformer implements Single.Transformer<Result<JsonObject>, Result<JsonObject>> {
    @Override
    public Single<Result<JsonObject>> call(Single<Result<JsonObject>> resultObservable) {
        return resultObservable.doOnSuccess(new Action1<Result<JsonObject>>() {
            @Override
            public void call(Result<JsonObject> result) {
                if (!result.response().isSuccessful()) {
                    generateRuntimeException(result.response());
                }
            }
        });
    }

    private void generateRuntimeException(Response response) {
        String errorMessage = response.message();
        switch (response.code()) {
            case 302:
                throw new NetworkException.Redirect(errorMessage);
            case 400:
                throw new NetworkException.BadRequest(errorMessage);
            case 401:
                throw new NetworkException.NotAuthorized(errorMessage);
            case 403:
                throw new NetworkException.Forbidden(errorMessage);
            case 404:
                throw new NetworkException.NotFound(errorMessage);
            case 405:
                throw new NetworkException.NotAllowed(errorMessage);
            case 410:
                throw new NetworkException.Gone(errorMessage);
            case 429:
                throw new NetworkException.RateLimit(errorMessage);
            case 504:
                throw new NetworkException.Timeout(errorMessage);
            default:
                throw new NetworkException.Unknown(errorMessage);
        }
    }
}