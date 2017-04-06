package ru.ifmo.hycson.demoapp.data;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Supported entity types
 */
public class SupportedTypes {
    public static final Set<String> sTypes = new HashSet<>(Arrays.asList(
            "Entrypoint",
            "http://schema.org/Person",
            "http://schema.org/Message",
            "FriendsCollection",
            "MessagesCollection"
    ));
}
