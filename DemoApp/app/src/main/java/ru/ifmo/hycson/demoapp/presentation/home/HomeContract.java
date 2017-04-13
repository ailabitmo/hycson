package ru.ifmo.hycson.demoapp.presentation.home;

import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;

import java.util.List;

import ru.ifmo.hycson.demoapp.presentation.navigation.links.display.DisplayableAppLink;

public interface HomeContract {
    interface View extends MvpView {
        void showLoading();

        void hideLoading();

        void showError(Throwable e);

        void setHomeEntryPointLinks(List<DisplayableAppLink> appLinks);
    }

    interface Presenter extends MvpPresenter<View> {
        void loadEntryPoint();
    }
}
