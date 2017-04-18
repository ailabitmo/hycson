package ru.ifmo.hycson.demoapp.dagger.friends;

import dagger.Module;
import dagger.Provides;
import ru.ifmo.hycson.demoapp.presentation.friends.FriendsContract;
import ru.ifmo.hycson.demoapp.presentation.friends.FriendsPresenter;
import ru.ifmo.hycson.demoapp.util.LogHelper;
import ru.ifmo.hymp.HypermediaClient;

@Module
public class FriendsModule {
    @Provides
    FriendsContract.Presenter provideFriendsPresenter(HypermediaClient hypermediaClient) {
        LogHelper.d(LogHelper.TAG_MODULE, this.getClass().getSimpleName() + ".provideFriendsPresenter");
        return new FriendsPresenter(hypermediaClient);
    }
}
