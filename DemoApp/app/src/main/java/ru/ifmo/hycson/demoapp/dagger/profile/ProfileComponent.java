package ru.ifmo.hycson.demoapp.dagger.profile;

import dagger.Subcomponent;
import ru.ifmo.hycson.demoapp.presentation.profile.ProfileContract;

@Subcomponent(modules = ProfileModule.class)
public interface ProfileComponent {
    ProfileContract.Presenter presenter();
}
