package ru.ifmo.hycson.demoapp.presentation.profile;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import java.util.List;
import java.util.Map;

import ru.ifmo.hycson.demoapp.presentation.navigation.links.display.DisplayableAppLink;
import ru.ifmo.hycson.demoapp.presentation.profile.entities.ProfileData;
import ru.ifmo.hycson.demoapp.util.rx.DisplayableAppLinksTransformer;
import ru.ifmo.hymp.HypermediaClient;
import ru.ifmo.hymp.entities.Resource;
import rx.Observable;
import rx.SingleSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ProfilePresenter extends MvpBasePresenter<ProfileContract.View> implements ProfileContract.Presenter {
    private static final String PROFILE_GIVEN_NAME_KEY = "http://schema.org/givenName";
    private static final String PROFILE_FAMILY_NAME_KEY = "http://schema.org/familyName";
    private static final String PROFILE_IMAGE_KEY = "http://schema.org/image";

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
                        .flatMap(new Func1<Resource, Observable<List<DisplayableAppLink>>>() {
                            @Override
                            public Observable<List<DisplayableAppLink>> call(Resource resource) {
                                return Observable.from(resource.getLinks())
                                        .compose(new DisplayableAppLinksTransformer());
                            }
                        }, new Func2<Resource, List<DisplayableAppLink>, ProfileData>() {
                            @Override
                            public ProfileData call(Resource profileResource, List<DisplayableAppLink> displayableAppLinks) {
                                return mapToProfileData(profileResource, displayableAppLinks);
                            }
                        })
                        .toSingle()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleSubscriber<ProfileData>() {
                            @Override
                            public void onSuccess(ProfileData profileData) {
                                if (isViewAttached()) {
                                    getView().setProfileData(profileData);
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

    private ProfileData mapToProfileData(Resource profileResource, List<DisplayableAppLink> displayableAppLinks) {
        Map<String, Object> resPropertyMap = profileResource.getPropertyMap();

        ProfileData.Builder builder = new ProfileData.Builder();
        ProfileData profileData = builder.setId(profileResource.getId())
                .setGivenName((String) resPropertyMap.get(PROFILE_GIVEN_NAME_KEY))
                .setFamilyName((String) resPropertyMap.get(PROFILE_FAMILY_NAME_KEY))
                .setImage((String) resPropertyMap.get(PROFILE_IMAGE_KEY))
                .setDisplayableAppLinks(displayableAppLinks)
                .build();

        return profileData;
    }
}
