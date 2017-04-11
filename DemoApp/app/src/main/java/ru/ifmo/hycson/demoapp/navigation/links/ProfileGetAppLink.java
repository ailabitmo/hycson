package ru.ifmo.hycson.demoapp.navigation.links;

import android.support.v4.app.Fragment;

import ru.ifmo.hycson.demoapp.presentation.profile.ProfileFragment;

public class ProfileGetAppLink extends AppLink {
    @Override
    public Fragment createDisplayFragment() {
        return new ProfileFragment();
    }
}
