package ru.ifmo.hycson.demoapp.presentation.navigation.links.display;

import android.support.v4.app.Fragment;

import ru.ifmo.hycson.demoapp.presentation.profile.ProfileFragment;

public class ProfileDisplayAppLink extends DisplayAppLink {
    @Override
    public Fragment createDisplayFragment(String url) {
        return ProfileFragment.newInstance(url);
    }
}
