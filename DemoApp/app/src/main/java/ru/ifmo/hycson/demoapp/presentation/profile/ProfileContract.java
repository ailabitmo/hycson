package ru.ifmo.hycson.demoapp.presentation.profile;

import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;

import ru.ifmo.hycson.demoapp.presentation.profile.entities.ProfileData;

public interface ProfileContract {
    interface View extends MvpView {
        void showLoading(LoadingType type);

        void hideLoading(LoadingType type);

        void showError(Throwable e);

        void setProfileData(ProfileData profileData);

        enum LoadingType {
            MESSAGE, PROFILE
        }
    }

    interface Presenter extends MvpPresenter<View> {
        void loadProfileData(String profileUrl);

        void sendMessage(String url, String message);
    }
}
