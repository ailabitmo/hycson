package ru.ifmo.hycson.demoapp.presentation.home;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import java.util.List;

import ru.ifmo.hycson.demoapp.presentation.navigation.links.AppLink;
import ru.ifmo.hycson.demoapp.presentation.navigation.links.display.DisplayAppLink;
import ru.ifmo.hycson.demoapp.util.rx.AppLinksTransformer;
import ru.ifmo.hymp.HypermediaClient;
import ru.ifmo.hymp.entities.Link;
import ru.ifmo.hymp.entities.Resource;
import rx.Observable;
import rx.SingleSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
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
                        .subscribeOn(Schedulers.io())
                        .flatMapObservable(new Func1<Resource, Observable<Link>>() {
                            @Override
                            public Observable<Link> call(Resource entryPoint) {
                                return Observable.from(entryPoint.getLinks());
                            }
                        })
                        .compose(new AppLinksTransformer())
                        .flatMap(new Func1<List<AppLink>, Observable<AppLink>>() {
                            @Override
                            public Observable<AppLink> call(List<AppLink> appLinks) {
                                return Observable.from(appLinks);
                            }
                        })
                        .filter(new Func1<AppLink, Boolean>() {
                            @Override
                            public Boolean call(AppLink appLink) {
                                return appLink instanceof DisplayAppLink;
                            }
                        })
                        .map(new Func1<AppLink, DisplayAppLink>() {
                            @Override
                            public DisplayAppLink call(AppLink appLink) {
                                return (DisplayAppLink) appLink;
                            }
                        })
                        .toList()
                        .toSingle()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleSubscriber<List<DisplayAppLink>>() {
                            @Override
                            public void onSuccess(List<DisplayAppLink> appLinks) {
                                if (isViewAttached()) {
                                    getView().hideLoading();
                                    getView().setHomeEntryPointLinks(appLinks);
                                }
                            }

                            @Override
                            public void onError(Throwable error) {
                                if (isViewAttached()) {
                                    getView().hideLoading();
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
