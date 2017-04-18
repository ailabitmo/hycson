package ru.ifmo.hycson.demoapp.presentation.friends;

import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;

import java.util.List;

import ru.ifmo.hycson.demoapp.presentation.profile.entities.ProfileData;

public interface FriendsContract {
    interface View extends MvpView {
        void showLoading();

        void hideLoading();

        void showError(Throwable e);

        void setFriends(List<ProfileData> friends);
    }

    interface Presenter extends MvpPresenter<View> {
        void loadFriendsData(String friendsUrl);
    }
}
