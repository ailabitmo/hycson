package ru.ifmo.hycson.demoapp.util.rx;

import java.util.List;
import java.util.Map;

import ru.ifmo.hycson.demoapp.presentation.navigation.links.display.DisplayableAppLink;
import ru.ifmo.hycson.demoapp.presentation.profile.entities.ProfileData;
import ru.ifmo.hymp.entities.Resource;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

public class ProfileDataTransformer implements Observable.Transformer<Resource, ProfileData> {
    private static final String PROFILE_GIVEN_NAME_KEY = "http://schema.org/givenName";
    private static final String PROFILE_FAMILY_NAME_KEY = "http://schema.org/familyName";
    private static final String PROFILE_IMAGE_KEY = "http://schema.org/image";

    @Override
    public Observable<ProfileData> call(Observable<Resource> resourceObservable) {
        return resourceObservable.flatMap(new Func1<Resource, Observable<List<DisplayableAppLink>>>() {
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
        });
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
