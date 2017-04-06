package ru.ifmo.hycson.demoapp;

import javax.inject.Singleton;

import dagger.Component;
import ru.ifmo.hycson.demoapp.data.ApiModule;
import ru.ifmo.hycson.demoapp.data.OAuthModule;
import ru.ifmo.hycson.demoapp.presentation.auth.BaseAuthActivity;
import ru.ifmo.hycson.demoapp.presentation.auth.TwitterAuthActivity;
import ru.ifmo.hycson.demoapp.presentation.auth.VKAuthActivity;
import ru.ifmo.hycson.demoapp.presentation.home.HomeFragment;


@Singleton
@Component(
        modules = {
                AppModule.class,
                OAuthModule.class,
                ApiModule.class
        }
)
public interface AppComponent {
    void inject(HomeFragment mainFragment);

    void inject(VKAuthActivity vkAuthActivity);

    void inject(TwitterAuthActivity twitterAuthActivity);

    void inject(BaseAuthActivity baseAuthActivity);
}