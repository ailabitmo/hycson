package ru.ifmo.hymp;

import ru.ifmo.hymp.entities.Resource;
import rx.Observable;

public interface Parser {
    Observable<Resource> loadAndParseResource(String url);
}
