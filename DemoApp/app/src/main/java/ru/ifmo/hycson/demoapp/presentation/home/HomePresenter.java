package ru.ifmo.hycson.demoapp.presentation.home;

import android.support.annotation.Nullable;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import java.util.List;

import ru.ifmo.hycson.demoapp.navigation.links.AppLink;
import ru.ifmo.hycson.demoapp.navigation.NavigationRules;
import ru.ifmo.hymp.HypermediaClient;
import ru.ifmo.hymp.entities.Link;
import ru.ifmo.hymp.entities.Operation;
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
                        .map(new Func1<Link, AppLink>() {
                            @Override
                            public AppLink call(Link link) {
                                return mapToAppLink(link);
                            }
                        })
                        .filter(new Func1<AppLink, Boolean>() {
                            @Override
                            public Boolean call(AppLink appLink) {
                                return appLink != null;
                            }
                        })
                        .toList()
                        .toSingle()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleSubscriber<List<AppLink>>() {
                            @Override
                            public void onSuccess(List<AppLink> appLinks) {
                                if (isViewAttached()) {
                                    getView().setEntryPointLinks(appLinks);
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

    @Nullable
    private AppLink mapToAppLink(Link link) {
        for (Operation operation : link.getOperations()) {
            if (operation.getType() == Operation.Type.GET) {
                AppLink appLink = NavigationRules.getAppLink(operation);
                if (appLink != null) {
                    appLink.setTitle(link.getTitle());
                    appLink.setUrl(link.getUrl());
                }
                return appLink;
            }
        }
        return null;
    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        mCompositeSubscription.clear();
    }
}
