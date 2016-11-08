package ru.ifmo.hycson.demoapp.data;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {
    private static final String STORAGE_NAME = "ru.ifmo.hycson.demoapp";
    private static final String PREF_VK_ACCESS_TOKEN_KEY = "ru.ifmo.hycson.demoapp.vk_access_token_key";
    private static final String PREF_TWITTER_ACCESS_TOKEN_KEY = "ru.ifmo.hycson.demoapp.twitter_access_token_key";

    private final SharedPreferences mSharedPreferences;

    public PreferencesManager(Context context) {
        mSharedPreferences = context.getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE);
    }

    public void saveVKAccessToken(String token) {
        mSharedPreferences.edit()
                .putString(PREF_VK_ACCESS_TOKEN_KEY, token)
                .apply();
    }

    public String retrieveVKAccessToken() {
        return mSharedPreferences.getString(PREF_VK_ACCESS_TOKEN_KEY, "");
    }

    public void saveTwitterAccessToken(String token) {
        mSharedPreferences.edit()
                .putString(PREF_TWITTER_ACCESS_TOKEN_KEY, token)
                .apply();
    }

    public String retrieveTwitterAccessToken() {
        return mSharedPreferences.getString(PREF_TWITTER_ACCESS_TOKEN_KEY, "");
    }
}
