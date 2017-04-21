package ru.ifmo.hymp;

import java.util.Map;

import ru.ifmo.hymp.entities.Resource;
import rx.Single;

public interface HypermediaClient {
    Single<Resource> loadHypermediaResource(String url);

    Single<Resource> createResource(String url, Map<String, String> data);
}
