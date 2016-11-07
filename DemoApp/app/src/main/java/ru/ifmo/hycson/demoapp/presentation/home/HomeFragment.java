package ru.ifmo.hycson.demoapp.presentation.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import javax.inject.Inject;

import ru.ifmo.hycson.demoapp.App;
import ru.ifmo.hycson.demoapp.R;
import ru.ifmo.hycson.demoapp.data.PreferencesManager;
import ru.ifmo.hycson.demoapp.presentation.auth.AuthActivity;

public class HomeFragment extends Fragment {
    private static final int AUTH_REQUEST_CODE = 1;

    @Inject
    PreferencesManager mPreferencesManager;

    private TextView mGreetingTextView;
    private TextView mTextView;
    private ImageView mIconView;
    private View mLoginLogoutView;

    public static Fragment newInstance() {
        return new HomeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mGreetingTextView = (TextView) view.findViewById(R.id.greetingTextView);
        mLoginLogoutView = view.findViewById(R.id.logInLogOutView);
        mTextView = (TextView) mLoginLogoutView.findViewById(R.id.textView);
        mIconView = (ImageView) mLoginLogoutView.findViewById(R.id.iconView);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        App.getApp(getContext()).getAppComponent().inject(this);

        String vkAccessToken = mPreferencesManager.retrieveVKAccessToken();
        boolean userLoggedIn = !TextUtils.isEmpty(vkAccessToken);
        setupViews(userLoggedIn);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTH_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                setupViews(true);
            } else {
                mGreetingTextView.setText(R.string.auth_failed);
            }
        }
    }

    private void setClickListener(final boolean userLoggedIn, View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userLoggedIn) {
                    mPreferencesManager.saveVKAccessToken("");
                    mGreetingTextView.setVisibility(View.GONE);
                    setupViews(false);
                } else {
                    Intent startIntent = AuthActivity.prepareStartIntent(getContext());
                    startActivityForResult(startIntent, AUTH_REQUEST_CODE);
                }
            }
        });
    }

    private void setupViews(boolean userLoggedIn) {
        if (userLoggedIn) {
            mGreetingTextView.setVisibility(View.VISIBLE);
            mGreetingTextView.setText(R.string.vk_auth_greeting);
            mTextView.setText(R.string.logout);
            mIconView.setImageResource(R.drawable.ic_24dp_logout);
        } else {
            mTextView.setText(R.string.login);
            mIconView.setImageResource(R.drawable.ic_24dp_login);
        }

        setClickListener(userLoggedIn, mLoginLogoutView);
    }
}
