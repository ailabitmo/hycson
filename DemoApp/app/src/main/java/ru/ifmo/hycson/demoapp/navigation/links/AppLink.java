package ru.ifmo.hycson.demoapp.navigation.links;

import android.support.v4.app.Fragment;

public abstract class AppLink {
    private String mUrl;
    private String mTitle;

    public abstract Fragment createDisplayFragment();

    public String getUrl() {
        return mUrl;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public void setTitle(String title) {
        mTitle = title;
    }
}
