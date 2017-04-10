package ru.ifmo.hycson.demoapp.presentation.home;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import ru.ifmo.hymp.Parser;
import ru.ifmo.hymp.entities.Resource;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class HomePresenter extends MvpBasePresenter<HomeContract.View> implements HomeContract.Presenter {

    private Parser mParser;
    private final CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    public HomePresenter(Parser parser) {
        mParser = parser;
    }

    @Override
    public void loadEntryPoint(String url) {
        if (isViewAttached()) {
            //noinspection ConstantConditions
            getView().showLoading();
        }

        mCompositeSubscription.add(
                mParser.loadAndParseResource(url)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Subscriber<Resource>() {
                            @Override
                            public void onCompleted() {
                                int a = 4;
                            }

                            @Override
                            public void onError(Throwable e) {
                                int a = 4;
                            }

                            @Override
                            public void onNext(Resource resource) {
                                if (isViewAttached()) {
                                    getView().setData(resource);
                                }
                            }
                        })
        );
    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        mCompositeSubscription.clear();
    }
}
