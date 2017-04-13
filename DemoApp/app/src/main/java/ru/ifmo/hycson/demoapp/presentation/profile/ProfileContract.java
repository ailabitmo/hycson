package ru.ifmo.hycson.demoapp.presentation.profile;

import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;

import ru.ifmo.hycson.demoapp.presentation.profile.entities.ProfileData;

public interface ProfileContract {
    interface View extends MvpView {
        void showLoading();

        void hideLoading();

        void showError(Throwable e);

        void setProfileData(ProfileData profileData);
    }

    interface Presenter extends MvpPresenter<View> {
        void loadProfileData(String profileUrl);
    }
}
