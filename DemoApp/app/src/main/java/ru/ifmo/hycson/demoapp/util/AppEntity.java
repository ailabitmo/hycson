package ru.ifmo.hycson.demoapp.util;

import android.support.annotation.Nullable;

import ru.ifmo.hycson.demoapp.presentation.navigation.links.AppLink;
import ru.ifmo.hycson.demoapp.presentation.navigation.links.display.ProfileDisplayAppLink;
import ru.ifmo.hymp.entities.Link;

public enum AppEntity {
    ENTRY_POINT(null),
    PERSON(new ProfileDisplayAppLink()),
    MESSAGE(null),
    FRIENDS_COLLECTION(null),
    MESSAGES_COLLECTION(null);

    private AppLink mAppLink;

    AppEntity(AppLink appLink) {
        mAppLink = appLink;
    }

    @Nullable
    public AppLink getAppLink() {
        return mAppLink;
    }

    @Nullable
    public static AppEntity getAppEntity(Link link, String type) {
        AppEntity appEntity;
        switch (type) {
            case "Entrypoint":
                appEntity = ENTRY_POINT;
                break;
            case "http://schema.org/Person":
                appEntity = PERSON;
                break;
            case "http://schema.org/Message":
                appEntity = MESSAGE;
                break;
            case "FriendsCollection":
                appEntity = FRIENDS_COLLECTION;
                break;
            case "MessagesCollection":
                appEntity = MESSAGES_COLLECTION;
                break;
            default:
                return null;
        }

        AppLink appLink = appEntity.getAppLink();
        if (appLink != null) {
            appLink.setUrl(link.getUrl());
            appLink.setTitle(link.getTitle());
        }

        return appEntity;
    }


}
