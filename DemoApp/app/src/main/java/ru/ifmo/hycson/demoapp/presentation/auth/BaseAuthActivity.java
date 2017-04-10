package ru.ifmo.hycson.demoapp.presentation.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;

import java.util.concurrent.Callable;

import javax.inject.Inject;

import ru.ifmo.hycson.demoapp.App;
import ru.ifmo.hycson.demoapp.R;
import ru.ifmo.hycson.demoapp.data.PreferencesManager;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public abstract class BaseAuthActivity extends AppCompatActivity {
    public static final String EXTRA_ACCESS_TOKEN = "ru.ifmo.hycson.demoapp.presentation.auth.access_token";

    @Inject
    PreferencesManager mPreferencesManager;

    protected WebView mAuthWebView;
    protected Subscription mSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        App.getApp(this).getAppComponent().inject(this);

        mAuthWebView = (WebView) findViewById(R.id.authWebView);
        mAuthWebView.requestFocus(View.FOCUS_DOWN);
        mAuthWebView.getSettings().setDomStorageEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
    }

    protected <T> Observable<T> asObservable(final Callable<T> function) {
        Observable<T> observable = Observable.fromCallable(new Callable<T>() {
            @Override
            public T call() throws Exception {
                return function.call();
            }
        });

        return observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    protected void handleUnsuccessfulResult() {
        setResult(RESULT_CANCELED);
        finish();
    }

    protected void handleSuccessfulResult(String accessToken) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_ACCESS_TOKEN, accessToken);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
