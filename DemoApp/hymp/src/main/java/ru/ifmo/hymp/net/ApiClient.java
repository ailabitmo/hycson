package ru.ifmo.hymp.net;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static ApiService apiService;

    public static void initApiService(String entryPoint) {
        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);

        okHttpBuilder
                .connectTimeout(60 * 1000, TimeUnit.MILLISECONDS)
                .readTimeout(60 * 1000, TimeUnit.MILLISECONDS)
                .addInterceptor(logging);

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
}
