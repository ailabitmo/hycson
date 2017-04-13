package ru.ifmo.hycson.demoapp.presentation.auth;

import ru.ifmo.hycson.demoapp.BuildConfig;
import ru.ifmo.hycson.demoapp.data.PreferencesManager;

public enum SelectedSocialNetwork {
    VK(PreferencesManager.PREF_VK_ACCESS_TOKEN_KEY, BuildConfig.VK_ENTRY_POINT),
    TWITTER(PreferencesManager.PREF_TWITTER_ACCESS_TOKEN_KEY, BuildConfig.TWITTER_ENTRY_POINT),
    NON("", BuildConfig.DEFAULT_ENTRY_POINT);

    private String mKey;
    private String mEntryPointUrl;

    SelectedSocialNetwork(String key, String entryPointUrl) {
        this.mKey = key;
        this.mEntryPointUrl = entryPointUrl;
    }

    public String getKey() {
        return mKey;
    }

    public String getEntryPointUrl() {
        return mEntryPointUrl;
    }
}
