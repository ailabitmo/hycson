package ru.ifmo.hycson.demoapp.presentation.home;

import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;

import ru.ifmo.hymp.entities.Resource;

public interface HomeContract {
    interface View extends MvpView {
        void showLoading();

        void hideLoading();

        void showError(Throwable e);

        void setData(Resource entryPointResource);
    }

    interface Presenter extends MvpPresenter<View> {
        void loadEntryPoint(String url);
    }
}

