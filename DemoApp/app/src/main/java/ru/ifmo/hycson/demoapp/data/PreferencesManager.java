package ru.ifmo.hycson.demoapp.data;

import android.content.Context;
import android.content.SharedPreferences;

import ru.ifmo.hycson.demoapp.presentation.auth.SelectedSocialNetwork;

public class PreferencesManager {
    private static final String STORAGE_NAME = "ru.ifmo.hycson.demoapp";
    private static final String PREF_SELECTED_SOCIAL_NETWORK_KEY = "ru.ifmo.hycson.demoapp.selected_social_network_key";

    public static final String PREF_VK_ACCESS_TOKEN_KEY = "ru.ifmo.hycson.demoapp.vk_access_token_key";
    public static final String PREF_TWITTER_ACCESS_TOKEN_KEY = "ru.ifmo.hycson.demoapp.twitter_access_token_key";

    private final SharedPreferences mSharedPreferences;

    public PreferencesManager(Context context) {
        mSharedPreferences = context.getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE);
    }

    public void saveAccessToken(String accessTokenKey, String token) {
        mSharedPreferences.edit()
                .putString(accessTokenKey, token)
                .apply();
    }

    public String retrieveAccessToken(String accessTokenKey) {
        return mSharedPreferences.getString(accessTokenKey, "");
    }

    public void saveSelectedSocialNetwork(SelectedSocialNetwork selectedSocialNetwork) {
        mSharedPreferences.edit()
                .putString(PREF_SELECTED_SOCIAL_NETWORK_KEY, selectedSocialNetwork.name())
                .apply();
    }

    public SelectedSocialNetwork retrieveSelectedSocialNetwork() {
        return SelectedSocialNetwork.valueOf(mSharedPreferences.getString(PREF_SELECTED_SOCIAL_NETWORK_KEY, SelectedSocialNetwork.NON.toString()));
    }
}
