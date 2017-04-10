package ru.ifmo.hycson.demoapp.dagger.hymp;

import dagger.Subcomponent;
import ru.ifmo.hycson.demoapp.dagger.SocialNetworkScope;
import ru.ifmo.hycson.demoapp.dagger.home.HomeComponent;

@Subcomponent(modules = HympModule.class)
@SocialNetworkScope
public interface HympComponent {
    HomeComponent plusHomeComponent();
}
