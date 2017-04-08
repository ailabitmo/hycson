package ru.ifmo.hycson.demoapp.presentation.home;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import javax.inject.Inject;

import ru.ifmo.hycson.demoapp.App;
import ru.ifmo.hycson.demoapp.BuildConfig;
import ru.ifmo.hycson.demoapp.R;
import ru.ifmo.hycson.demoapp.data.ApiService;
import ru.ifmo.hycson.demoapp.data.PreferencesManager;
import ru.ifmo.hycson.demoapp.presentation.auth.BaseAuthActivity;
import ru.ifmo.hycson.demoapp.presentation.auth.TwitterAuthActivity;
import ru.ifmo.hycson.demoapp.presentation.auth.VKAuthActivity;
import ru.ifmo.hymp.HypermediaMessageParser;
import ru.ifmo.hymp.Parser;
import ru.ifmo.hymp.entities.Resource;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class HomeFragment extends Fragment {
    private static final int VK_AUTH_REQUEST_CODE = 0;
    private static final int TWITTER_AUTH_REQUEST_CODE = 1;

    @Inject
    PreferencesManager mPreferencesManager;

    @Inject
    ApiService mApiService;

    private TextView mGreetingTextView;
    private TextView mTextView;
    private ImageView mIconView;
    private View mLoginLogoutView;

    public static Fragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getApp(getContext()).getAppComponent().inject(this);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mGreetingTextView = (TextView) view.findViewById(R.id.greetingTextView);
        mLoginLogoutView = view.findViewById(R.id.logInLogOutView);
        mTextView = (TextView) mLoginLogoutView.findViewById(R.id.textView);
        mIconView = (ImageView) mLoginLogoutView.findViewById(R.id.iconView);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        AuthStatus authStatus;
        if (!TextUtils.isEmpty(mPreferencesManager.retrieveVKAccessToken())) {
            authStatus = AuthStatus.VK;
        } else if (!TextUtils.isEmpty(mPreferencesManager.retrieveTwitterAccessToken())) {
            authStatus = AuthStatus.TWITTER;
        } else {
            authStatus = AuthStatus.NOT_AUTH;
        }

        setupViews(authStatus);

        Parser parser = new HypermediaMessageParser(BuildConfig.VK_ENTRY_POINT);
//        String url = "/api/vk/person/638865/friends";
//        String url = "/api/vk/person/638865"; // person profile
        String url = ""; // entry point
        parser.loadAndParseResource(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Resource>() {
                    @Override
                    public void onCompleted() {
                        int a = 5;
                    }

                    @Override
                    public void onError(Throwable e) {
                        int a = 5;
                    }

                    @Override
                    public void onNext(Resource resource) {
                        int a = 5;
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            mGreetingTextView.setText(R.string.auth_failed);
        } else {
            String accessToken = data.getStringExtra(BaseAuthActivity.EXTRA_ACCESS_TOKEN);
            if (requestCode == VK_AUTH_REQUEST_CODE) {
                mPreferencesManager.saveVKAccessToken(accessToken);
                setupViews(AuthStatus.VK);
            } else {
                mPreferencesManager.saveTwitterAccessToken(accessToken);
                setupViews(AuthStatus.TWITTER);
            }
        }
    }

    private void setupClickListener(final AuthStatus authStatus, View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (authStatus != AuthStatus.NOT_AUTH) {
                    if (authStatus == AuthStatus.VK) {
                        mPreferencesManager.saveVKAccessToken("");
                    } else {
                        mPreferencesManager.saveTwitterAccessToken("");
                    }
                    mGreetingTextView.setVisibility(View.GONE);
                    setupViews(AuthStatus.NOT_AUTH);
                } else {
                    showDialog();
                }
            }
        });
    }

    private void showDialog() {
        AlertDialog alertDialog = new AlertDialog
                .Builder(getContext())
                .create();

        alertDialog.setTitle("VK or Twitter?");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "VK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent startIntent = VKAuthActivity.prepareStartIntent(getContext());
                        startActivityForResult(startIntent, VK_AUTH_REQUEST_CODE);
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "TWITTER",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent startIntent = TwitterAuthActivity.prepareStartIntent(getContext());
                        startActivityForResult(startIntent, TWITTER_AUTH_REQUEST_CODE);
                    }
                });

        alertDialog.show();
    }

    private void setupViews(AuthStatus authStatus) {
        if (authStatus != AuthStatus.NOT_AUTH) {
            mGreetingTextView.setVisibility(View.VISIBLE);
            mGreetingTextView.setText(authStatus == AuthStatus.VK ? R.string.vk_auth_greeting : R.string.twitter_auth_greeting);
            mTextView.setText(R.string.logout);
            mIconView.setImageResource(R.drawable.ic_24dp_logout);
        } else {
            mTextView.setText(R.string.login);
            mIconView.setImageResource(R.drawable.ic_24dp_login);
        }

        setupClickListener(authStatus, mLoginLogoutView);
    }

    private enum AuthStatus {
        VK, TWITTER, NOT_AUTH
    }
}