package ru.ifmo.hycson.demoapp.presentation.profile;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import java.util.Map;

import ru.ifmo.hycson.demoapp.presentation.profile.entities.ProfileData;
import ru.ifmo.hymp.HypermediaClient;
import ru.ifmo.hymp.entities.Resource;
import rx.SingleSubscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ProfilePresenter extends MvpBasePresenter<ProfileContract.View> implements ProfileContract.Presenter {
    private static final String PROFILE_ID_KEY = "@id";
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
                        .map(new Func1<Resource, ProfileData>() {
                            @Override
                            public ProfileData call(Resource resource) {
                                return mapToProfile(resource.getPropertyMap());
                            }
                        })
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

    private ProfileData mapToProfile(Map<String, Object> resPropertyMap) {
        ProfileData.Builder builder = new ProfileData.Builder();

        ProfileData profileData = builder.setId((String) resPropertyMap.get(PROFILE_ID_KEY))
                .setGivenName((String) resPropertyMap.get(PROFILE_GIVEN_NAME_KEY))
                .setFamilyName((String) resPropertyMap.get(PROFILE_FAMILY_NAME_KEY))
                .setImage((String) resPropertyMap.get(PROFILE_IMAGE_KEY))
                .build();

        return profileData;
    }
}
