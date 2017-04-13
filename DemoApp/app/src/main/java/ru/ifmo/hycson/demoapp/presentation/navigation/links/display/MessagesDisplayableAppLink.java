package ru.ifmo.hycson.demoapp.presentation.navigation.links.display;

import android.support.v4.app.Fragment;

import ru.ifmo.hycson.demoapp.presentation.messages.MessagesFragment;

public class MessagesDisplayableAppLink extends DisplayableAppLink {
    @Override
    public Fragment createDisplayFragment(String url) {
        return new MessagesFragment();
    }
}
