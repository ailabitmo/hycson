package ru.ifmo.hycson.demoapp.presentation.home;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import java.util.List;

import ru.ifmo.hycson.demoapp.presentation.navigation.NavigationRules;
import ru.ifmo.hycson.demoapp.presentation.navigation.links.AppLink;
import ru.ifmo.hycson.demoapp.presentation.navigation.links.display.DisplayableAppLink;
import ru.ifmo.hymp.HypermediaClient;
import ru.ifmo.hymp.entities.Link;
import ru.ifmo.hymp.entities.Operation;
import ru.ifmo.hymp.entities.Resource;
import rx.Observable;
import rx.SingleSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
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
                        .flatMap(new Func1<Link, Observable<Operation>>() {
                            @Override
                            public Observable<Operation> call(Link link) {
                                return Observable.from(link.getOperations())
                                        .filter(new Func1<Operation, Boolean>() {
                                            @Override
                                            public Boolean call(Operation operation) {
                                                return operation.getType() == Operation.Type.GET;
                                            }
                                        });
                            }
                        }, new Func2<Link, Operation, AppLink>() {
                            @Override
                            public AppLink call(Link link, Operation operation) {
                                return NavigationRules.mapToAppLink(operation, link.getTitle(), link.getUrl());
                            }
                        })
                        .filter(new Func1<AppLink, Boolean>() {
                            @Override
                            public Boolean call(AppLink appLink) {
                                return appLink instanceof DisplayableAppLink;
                            }
                        })
                        .map(new Func1<AppLink, DisplayableAppLink>() {
                            @Override
                            public DisplayableAppLink call(AppLink appLink) {
                                return (DisplayableAppLink) appLink;
                            }
                        })
                        .toSortedList(new Func2<DisplayableAppLink, DisplayableAppLink, Integer>() {
                            @Override
                            public Integer call(DisplayableAppLink appLink, DisplayableAppLink appLink2) {
                                return appLink.getTitle().compareTo(appLink2.getTitle()) * -1;
                            }
                        })
                        .toSingle()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleSubscriber<List<DisplayableAppLink>>() {
                            @Override
                            public void onSuccess(List<DisplayableAppLink> appLinks) {
                                if (isViewAttached()) {
                                    getView().setHomeEntryPointLinks(appLinks);
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
