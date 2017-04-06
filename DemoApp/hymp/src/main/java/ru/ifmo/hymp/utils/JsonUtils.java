package ru.ifmo.hymp.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonUtils {
    public static String getMemberAsStringNullSafety(JsonObject jsonObject, String member) {
        JsonElement jsonElement = jsonObject.get(member);
        return jsonElement == null ? "" : jsonElement.getAsString();
    }
}

