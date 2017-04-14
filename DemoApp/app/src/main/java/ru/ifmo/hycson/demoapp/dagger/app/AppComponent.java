package ru.ifmo.hycson.demoapp.dagger.app;

import javax.inject.Singleton;

import dagger.Component;
import ru.ifmo.hycson.demoapp.dagger.hymp.HypermediaComponent;
import ru.ifmo.hycson.demoapp.presentation.auth.BaseAuthActivity;
import ru.ifmo.hycson.demoapp.presentation.auth.TwitterAuthActivity;
import ru.ifmo.hycson.demoapp.presentation.auth.VKAuthActivity;
import ru.ifmo.hycson.demoapp.presentation.home.HomeActivity;


/**
 * Main component that provide dependencies from @Singleton modules.
 * This is paren component for {@link HypermediaComponent} and {@link ru.ifmo.hycson.demoapp.dagger.home.HomeComponent}
 */
@Singleton
@Component(
        modules = {
                AppModule.class,
                OAuthModule.class,
        }
)
public interface AppComponent {
    void inject(HomeActivity homeActivity);

    void inject(VKAuthActivity vkAuthActivity);

    void inject(TwitterAuthActivity twitterAuthActivity);

    void inject(BaseAuthActivity baseAuthActivity);

    HypermediaComponent plusHympComponent();
}