package ru.ifmo.hycson.demoapp.presentation.friends;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import java.util.Collection;
import java.util.List;

import ru.ifmo.hycson.demoapp.presentation.profile.entities.ProfileData;
import ru.ifmo.hycson.demoapp.util.rx.ProfileDataTransformer;
import ru.ifmo.hymp.HypermediaClient;
import ru.ifmo.hymp.entities.Resource;
import rx.Observable;
import rx.SingleSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class FriendsPresenter extends MvpBasePresenter<FriendsContract.View> implements FriendsContract.Presenter {
    private static final String COLLECTION_MEMBER = "http://www.w3.org/ns/hydra/core#member";

    private final HypermediaClient mHypermediaClient;
    private final CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    public FriendsPresenter(HypermediaClient hypermediaClient) {
        mHypermediaClient = hypermediaClient;
    }

    @Override
    public void loadFriendsData(String friendsUrl) {
        if (isViewAttached()) {
            //noinspection ConstantConditions
            getView().showLoading();
        }

        mCompositeSubscription.add(
                mHypermediaClient.loadHypermediaResource(friendsUrl)
                        .subscribeOn(Schedulers.io())
                        .flatMapObservable(new Func1<Resource, Observable<Resource>>() {
                            @Override
                            public Observable<Resource> call(Resource resource) {
                                Object members = resource.getPropertyMap().get(COLLECTION_MEMBER);
                                if (members instanceof Collection) {
                                    return Observable.from((List<? extends Resource>) members);
                                } else {
                                    return Observable.empty();
                                }
                            }
                        })
                        .compose(new ProfileDataTransformer())
                        .toList()
                        .toSingle()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleSubscriber<List<ProfileData>>() {
                            @Override
                            public void onSuccess(List<ProfileData> friends) {
                                if (isViewAttached()) {
                                    getView().hideLoading();
                                    getView().setFriends(friends);
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
}
