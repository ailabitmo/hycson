package ru.ifmo.hycson.demoapp.navigation;

import ru.ifmo.hycson.demoapp.navigation.links.AppLink;
import ru.ifmo.hycson.demoapp.navigation.links.ProfileGetAppLink;
import ru.ifmo.hymp.entities.Operation;

public class NavigationRules {

    public static AppLink getAppLink(Operation linkOperation) {
        switch (linkOperation.getType()) {
            case GET:
                switch (linkOperation.getReturns()) {
                    case "Entrypoint":
                        return null;
                    case "http://schema.org/Person":
                        return new ProfileGetAppLink();
                    case "http://schema.org/Message":
                        return null;
                    case "FriendsCollection":
                        return null;
                    case "MessagesCollection":
                        return null;
                }
            case POST:
                switch (linkOperation.getExpects()) {
                    case "http://schema.org/Message":
                        return null;
                }
            default:
                return null;
        }
    }
}
