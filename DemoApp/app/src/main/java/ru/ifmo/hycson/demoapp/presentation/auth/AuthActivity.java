package ru.ifmo.hycson.demoapp.presentation.auth;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;

import java.util.concurrent.Callable;

import javax.inject.Inject;

import ru.ifmo.hycson.demoapp.App;
import ru.ifmo.hycson.demoapp.BuildConfig;
import ru.ifmo.hycson.demoapp.R;
import ru.ifmo.hycson.demoapp.data.PreferencesManager;
import ru.ifmo.hycson.demoapp.presentation.base.BaseActivity;
import ru.ifmo.hycson.demoapp.util.LogHelper;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AuthActivity extends BaseActivity {

    private Subscription mSubscription;

    @Inject
    OAuth20Service mOAuth20Service;

    @Inject
    PreferencesManager mPreferencesManager;

    private WebView mAuthWebView;

    public static Intent prepareStartIntent(Context context) {
        Intent intent = new Intent(context, AuthActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getApp(this).getAppComponent().inject(this);

        mAuthWebView = (WebView) findViewById(R.id.authWebView);
        mAuthWebView.requestFocus(View.FOCUS_DOWN);
        mAuthWebView.getSettings().setDomStorageEnabled(true);
        mAuthWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (url.startsWith(BuildConfig.VK_API_CALLBACK_URI)) {
                    url = url.replace("#", "?");
                    Uri uri = Uri.parse(url);
                    String authCode = uri.getQueryParameter("code");

                    if (!TextUtils.isEmpty(authCode)) {
                        mAuthWebView.stopLoading();
                        getAndSaveAuthToken(authCode);
                    } else {
                        // ...
                    }
                }
            }
        });

        loadAuthPage();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_auth;
    }

    @Nullable
    @Override
    protected Fragment createDisplayedFragment() {
        return null;
    }

    private void loadAuthPage() {
        String authUrl = mOAuth20Service.getAuthorizationUrl();
        mAuthWebView.loadUrl(authUrl);
    }

    private void getAndSaveAuthToken(final String code) {
        mSubscription = getAuthToken(code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<OAuth2AccessToken>() {
                    @Override
                    public void onCompleted() {
                        LogHelper.d("onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogHelper.d("onError: " + e);
                        handleUnsuccessfulResult();
                    }

                    @Override
                    public void onNext(OAuth2AccessToken oAuth2AccessToken) {
                        LogHelper.d("onNext");
                        handleSuccessfulResult(oAuth2AccessToken);
                    }
                });
    }

    private void handleUnsuccessfulResult() {
        setResult(RESULT_CANCELED);
        finish();
    }

    private void handleSuccessfulResult(OAuth2AccessToken oAuth2AccessToken) {
        mPreferencesManager.saveVKAccessToken(oAuth2AccessToken.getAccessToken());
        setResult(RESULT_OK);
        finish();
    }

    private Observable<OAuth2AccessToken> getAuthToken(final String code) {
        return Observable.fromCallable(new Callable<OAuth2AccessToken>() {
            @Override
            public OAuth2AccessToken call() throws Exception {
                return mOAuth20Service.getAccessToken(code);
            }
        });
    }
}
