package ru.ifmo.hycson.demoapp.presentation.home;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import ru.ifmo.hymp.HypermediaClient;
import ru.ifmo.hymp.entities.Resource;
import rx.SingleSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class HomePresenter extends MvpBasePresenter<HomeContract.View> implements HomeContract.Presenter {

    private HypermediaClient mHypermediaClient;
    private final CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    public HomePresenter(HypermediaClient hypermediaClient) {
        mHypermediaClient = hypermediaClient;
    }

    @Override
    public void loadEntryPoint() {
        if (isViewAttached()) {
            //noinspection ConstantConditions
            getView().showLoading();
        }

        mCompositeSubscription.add(
                mHypermediaClient.loadHypermediaResource("")
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new SingleSubscriber<Resource>() {
                            @Override
                            public void onSuccess(Resource resource) {
                                if (isViewAttached()) {
                                    getView().entryPointLoaded(resource);
                                }
                            }

                            @Override
                            public void onError(Throwable error) {
                                if (isViewAttached()) {
                                    getView().showError(error);
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
