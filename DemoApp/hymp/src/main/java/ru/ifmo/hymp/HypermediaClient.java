package ru.ifmo.hymp;

import ru.ifmo.hymp.entities.Resource;
import rx.Observable;
import rx.Single;

public interface HypermediaClient {
    Single<Resource> loadHypermediaResource(String url);
}
