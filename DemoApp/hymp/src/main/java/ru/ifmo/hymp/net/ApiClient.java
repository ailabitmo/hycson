package ru.ifmo.hymp.net;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.ifmo.hymp.utils.StringUtils;

public class ApiClient {
    private static ApiService apiService;

    public static void initApiService(String entryPoint, String accessToken) {
        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);

        okHttpBuilder
                .connectTimeout(60 * 1000, TimeUnit.MILLISECONDS)
                .readTimeout(60 * 1000, TimeUnit.MILLISECONDS)
                .addInterceptor(logging);

        if (!StringUtils.isEmpty(accessToken)) {
            okHttpBuilder.addInterceptor(new AccessTokenInterceptor(accessToken));
        }

        OkHttpClient okHttpClient = okHttpBuilder.build();

        Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
        retrofitBuilder.client(okHttpClient)
                .baseUrl(entryPoint)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create());

        apiService = retrofitBuilder.build().create(ApiService.class);
    }

    public static ApiService getApiService() {
        return apiService;
    }

    private static class AccessTokenInterceptor implements Interceptor {
        private final String accessToken;

        AccessTokenInterceptor(String accessToken) {
            this.accessToken = accessToken;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            request = request.newBuilder()
                    .addHeader("Authorization", accessToken)
                    .build();

            return chain.proceed(request);
        }
    }
}