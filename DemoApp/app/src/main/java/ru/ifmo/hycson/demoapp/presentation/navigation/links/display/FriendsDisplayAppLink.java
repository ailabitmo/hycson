package ru.ifmo.hycson.demoapp.presentation.navigation.links.display;

import android.support.v4.app.Fragment;

import ru.ifmo.hycson.demoapp.presentation.friends.FriendsFragment;

public class FriendsDisplayAppLink extends DisplayAppLink {
    @Override
    public Fragment createDisplayFragment(String url) {
        return FriendsFragment.newInstance(url);
    }
}
