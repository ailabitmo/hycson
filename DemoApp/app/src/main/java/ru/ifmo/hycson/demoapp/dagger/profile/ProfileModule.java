package ru.ifmo.hycson.demoapp.dagger.profile;

import dagger.Module;
import dagger.Provides;
import ru.ifmo.hycson.demoapp.presentation.profile.ProfileContract;
import ru.ifmo.hycson.demoapp.presentation.profile.ProfilePresenter;
import ru.ifmo.hycson.demoapp.util.LogHelper;
import ru.ifmo.hymp.HypermediaClient;

@Module
public class ProfileModule {
    @Provides
    ProfileContract.Presenter provideProfilePresenter(HypermediaClient hypermediaClient) {
        LogHelper.d(LogHelper.TAG_MODULE, this.getClass().getSimpleName() + ".provideProfilePresenter");
        return new ProfilePresenter(hypermediaClient);
    }
}
