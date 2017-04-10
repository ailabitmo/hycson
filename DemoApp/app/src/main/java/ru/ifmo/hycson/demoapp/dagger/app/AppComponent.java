package ru.ifmo.hycson.demoapp.dagger.app;

import javax.inject.Singleton;

import dagger.Component;
import ru.ifmo.hycson.demoapp.dagger.hymp.HympComponent;
import ru.ifmo.hycson.demoapp.data.ApiModule;
import ru.ifmo.hycson.demoapp.data.OAuthModule;
import ru.ifmo.hycson.demoapp.presentation.auth.BaseAuthActivity;
import ru.ifmo.hycson.demoapp.presentation.auth.TwitterAuthActivity;
import ru.ifmo.hycson.demoapp.presentation.auth.VKAuthActivity;
import ru.ifmo.hycson.demoapp.presentation.home.HomeActivity;
import ru.ifmo.hycson.demoapp.presentation.home.HomeFragment;


/**
 * Main component that provide dependencies from @Singleton modules.
 * This is paren component for {@link HympComponent} and {@link ru.ifmo.hycson.demoapp.dagger.home.HomeComponent}
 */
@Singleton
@Component(
        modules = {
                AppModule.class,
                OAuthModule.class,
                ApiModule.class
        }
)
public interface AppComponent {
    void inject(HomeActivity homeActivity);

    void inject(HomeFragment mainFragment);

    void inject(VKAuthActivity vkAuthActivity);

    void inject(TwitterAuthActivity twitterAuthActivity);

    void inject(BaseAuthActivity baseAuthActivity);

    HympComponent plusHympComponent();
}