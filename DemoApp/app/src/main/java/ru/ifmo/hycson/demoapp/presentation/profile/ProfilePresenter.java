package ru.ifmo.hycson.demoapp.presentation.profile;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import ru.ifmo.hycson.demoapp.presentation.profile.entities.ProfileData;
import ru.ifmo.hycson.demoapp.util.rx.ProfileDataTransformer;
import ru.ifmo.hymp.HypermediaClient;
import rx.SingleSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ProfilePresenter extends MvpBasePresenter<ProfileContract.View> implements ProfileContract.Presenter {
    private HypermediaClient mHypermediaClient;
    private final CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    public ProfilePresenter(HypermediaClient hypermediaClient) {
        mHypermediaClient = hypermediaClient;
    }

    @Override
    public void loadProfileData(String profileUrl) {
        if (isViewAttached()) {
            //noinspection ConstantConditions
            getView().showLoading();
        }

        mCompositeSubscription.add(
                mHypermediaClient.loadHypermediaResource(profileUrl)
                        .subscribeOn(Schedulers.io())
                        .toObservable()
                        .compose(new ProfileDataTransformer())
                        .toSingle()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleSubscriber<ProfileData>() {
                            @Override
                            public void onSuccess(ProfileData profileData) {
                                if (isViewAttached()) {
                                    getView().hideLoading();
                                    getView().setProfileData(profileData);
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
