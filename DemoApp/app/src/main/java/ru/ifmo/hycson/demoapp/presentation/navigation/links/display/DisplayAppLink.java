package ru.ifmo.hycson.demoapp.presentation.navigation.links.display;

import android.support.v4.app.Fragment;

import ru.ifmo.hycson.demoapp.presentation.navigation.links.AppLink;

public abstract class DisplayAppLink extends AppLink {
    public abstract Fragment createDisplayFragment(String url);
}
