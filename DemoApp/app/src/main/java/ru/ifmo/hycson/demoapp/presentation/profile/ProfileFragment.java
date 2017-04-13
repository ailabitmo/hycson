package ru.ifmo.hycson.demoapp.presentation.profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hannesdorfmann.mosby.mvp.MvpFragment;

import ru.ifmo.hycson.demoapp.App;
import ru.ifmo.hycson.demoapp.R;
import ru.ifmo.hycson.demoapp.presentation.profile.entities.ProfileData;

public class ProfileFragment extends MvpFragment<ProfileContract.View, ProfileContract.Presenter>
        implements ProfileContract.View {

    private static final String BUNDLE_PROFILE_URL = "ru.ifmo.hycson.demoapp.presentation.profile.profile_url";

    public static Fragment newInstance(String profileUrl) {
        Fragment instance = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(BUNDLE_PROFILE_URL, profileUrl);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getPresenter().loadProfileData(getArguments().getString(BUNDLE_PROFILE_URL));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @NonNull
    @Override
    public ProfileContract.Presenter createPresenter() {
        return App.getApp(getContext()).plusHympComponent().plusProfileComponent().presenter();
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showError(Throwable e) {

    }

    @Override
    public void setProfileData(ProfileData profileData) {
        int a = 4;
    }
}
