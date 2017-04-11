package ru.ifmo.hycson.demoapp.presentation.home;

import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;

import java.util.List;

import ru.ifmo.hycson.demoapp.navigation.links.AppLink;

public interface HomeContract {
    interface View extends MvpView {
        void showLoading();

        void hideLoading();

        void showError(Throwable e);

        void setEntryPointLinks(List<AppLink> appLinks);
    }

    interface Presenter extends MvpPresenter<View> {
        void loadEntryPoint();
    }
}

