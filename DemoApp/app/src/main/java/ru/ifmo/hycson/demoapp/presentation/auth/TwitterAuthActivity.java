package ru.ifmo.hycson.demoapp.presentation.auth;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.oauth.OAuth10aService;

import java.util.concurrent.Callable;

import javax.inject.Inject;

import ru.ifmo.hycson.demoapp.App;
import ru.ifmo.hycson.demoapp.BuildConfig;
import ru.ifmo.hycson.demoapp.util.LogHelper;
import rx.Subscriber;

public class TwitterAuthActivity extends BaseAuthActivity {
    @Inject
    OAuth10aService mOAuth10aService;

    public static Intent prepareStartIntent(Context context) {
        return new Intent(context, TwitterAuthActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getApp(this).getAppComponent().inject(this);
        getRequestToken();
    }

    private void getRequestToken() {
        Callable<OAuth1RequestToken> getRequestTokenCallable = new Callable<OAuth1RequestToken>() {
            @Override
            public OAuth1RequestToken call() throws Exception {
                return mOAuth10aService.getRequestToken();
            }
        };

        mSubscription = asObservable(getRequestTokenCallable)
                .subscribe(new Subscriber<OAuth1RequestToken>() {
                    @Override
                    public void onCompleted() {
                        LogHelper.d("onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogHelper.d("onError: " + e);
                    }

                    @Override
                    public void onNext(OAuth1RequestToken requestToken) {
                        loadAuthPage(requestToken);
                    }
                });
    }


    private void loadAuthPage(final OAuth1RequestToken requestToken) {
        String authUrl = mOAuth10aService.getAuthorizationUrl(requestToken);

        mAuthWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (url.startsWith(BuildConfig.TWITTER_API_CALLBACK_URI)) {
                    mAuthWebView.stopLoading();
                    Uri uri = Uri.parse(url);
                    String oauthVerifier = uri.getQueryParameter("oauth_verifier");
                    if (!TextUtils.isEmpty(oauthVerifier)) {
                        getAccessToken(requestToken, oauthVerifier);
                    } else {
                        handleUnsuccessfulResult();
                    }
                }
            }
        });

        mAuthWebView.loadUrl(authUrl);
    }

    private void getAccessToken(final OAuth1RequestToken requestToken, final String oauthVerifier) {
        Callable<OAuth1AccessToken> getAccessTokenCallable = new Callable<OAuth1AccessToken>() {
            @Override
            public OAuth1AccessToken call() throws Exception {
                return mOAuth10aService.getAccessToken(requestToken, oauthVerifier);
            }
        };

        mSubscription = asObservable(getAccessTokenCallable)
                .subscribe(new Subscriber<OAuth1AccessToken>() {
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
                    public void onNext(OAuth1AccessToken oAuth1AccessToken) {
                        LogHelper.d("onNext");
                        String authString = oAuth1AccessToken.getToken() + "," + oAuth1AccessToken.getTokenSecret() + "," + BuildConfig.TWITTER_API_KEY + "," + BuildConfig.TWITTER_API_SECRET;
                        handleSuccessfulResult(authString);
                    }
                });
    }
}

