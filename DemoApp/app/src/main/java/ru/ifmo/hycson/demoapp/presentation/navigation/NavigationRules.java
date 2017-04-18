package ru.ifmo.hycson.demoapp.presentation.navigation;

import ru.ifmo.hycson.demoapp.presentation.navigation.links.AppLink;
import ru.ifmo.hycson.demoapp.presentation.navigation.links.create.MessageCreateLink;
import ru.ifmo.hycson.demoapp.presentation.navigation.links.display.FriendsDisplayAppLink;
import ru.ifmo.hycson.demoapp.presentation.navigation.links.display.MessagesDisplayAppLink;
import ru.ifmo.hycson.demoapp.presentation.navigation.links.display.ProfileDisplayAppLink;
import ru.ifmo.hymp.entities.Operation;

public class NavigationRules {
    public static AppLink mapToAppLink(Operation linkOperation, String linkTitle, String linkUrl) {
        AppLink appLink = getAppLinkByOperation(linkOperation);
        if (appLink != null) {
            appLink.setTitle(linkTitle);
            appLink.setUrl(linkUrl);
        }

        return appLink;
    }

    private static AppLink getAppLinkByOperation(Operation linkOperation) {
        switch (linkOperation.getType()) {
            case GET:
                switch (linkOperation.getReturns()) {
                    case "Entrypoint":
                        return null;
                    case "http://schema.org/Person":
                        return new ProfileDisplayAppLink();
                    case "http://schema.org/Message":
                        return null;
                    case "FriendsCollection":
                        return new FriendsDisplayAppLink();
                    case "MessagesCollection":
                        return new MessagesDisplayAppLink();
                }
            case POST:
                switch (linkOperation.getExpects()) {
                    case "http://schema.org/Message":
                        return new MessageCreateLink();
                }
            default:
                return null;
        }
    }
}
