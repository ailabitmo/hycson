package ru.ifmo.hycson.demoapp.presentation.auth;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;

import java.util.concurrent.Callable;

import javax.inject.Inject;

import ru.ifmo.hycson.demoapp.App;
import ru.ifmo.hycson.demoapp.BuildConfig;
import ru.ifmo.hycson.demoapp.util.LogHelper;
import rx.Subscriber;

public class VKAuthActivity extends BaseAuthActivity {
    @Inject
    OAuth20Service mOAuth20Service;

    public static Intent prepareStartIntent(Context context) {
        return new Intent(context, VKAuthActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getApp(this).getAppComponent().inject(this);
        loadAuthPage();
    }

    private void loadAuthPage() {
        String authUrl = mOAuth20Service.getAuthorizationUrl();

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
                        handleUnsuccessfulResult();
                    }
                }
            }
        });

        mAuthWebView.loadUrl(authUrl);
    }

    private void getAndSaveAuthToken(final String code) {
        Callable<OAuth2AccessToken> callableFunction = new Callable<OAuth2AccessToken>() {
            @Override
            public OAuth2AccessToken call() throws Exception {
                return mOAuth20Service.getAccessToken(code);
            }
        };

        mSubscription = asObservable(callableFunction)
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
                        handleSuccessfulResult(oAuth2AccessToken.getAccessToken());
                    }
                });
    }
}
