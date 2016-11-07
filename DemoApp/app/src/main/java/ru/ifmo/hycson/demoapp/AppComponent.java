package ru.ifmo.hycson.demoapp;

import javax.inject.Singleton;

import dagger.Component;
import ru.ifmo.hycson.demoapp.data.OAuthModule;
import ru.ifmo.hycson.demoapp.presentation.auth.AuthActivity;
import ru.ifmo.hycson.demoapp.presentation.home.HomeFragment;


@Singleton
@Component(
        modules = {
                AppModule.class,
                OAuthModule.class
        }
)
public interface AppComponent {
    void inject(HomeFragment mainFragment);

    void inject(AuthActivity authActivity);
}