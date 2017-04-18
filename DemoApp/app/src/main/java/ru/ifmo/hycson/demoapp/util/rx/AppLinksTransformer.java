package ru.ifmo.hycson.demoapp.util.rx;

import java.util.List;

import ru.ifmo.hycson.demoapp.presentation.navigation.NavigationRules;
import ru.ifmo.hycson.demoapp.presentation.navigation.links.AppLink;
import ru.ifmo.hymp.entities.Link;
import ru.ifmo.hymp.entities.Operation;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

public class AppLinksTransformer implements Observable.Transformer<Link, List<AppLink>> {
    @Override
    public Observable<List<AppLink>> call(Observable<Link> linkObservable) {
        return linkObservable
                .flatMap(new Func1<Link, Observable<Operation>>() {
                    @Override
                    public Observable<Operation> call(Link link) {
                        return Observable.from(link.getOperations());
                    }
                }, new Func2<Link, Operation, AppLink>() {
                    @Override
                    public AppLink call(Link link, Operation operation) {
                        return NavigationRules.mapToAppLink(operation, link.getTitle(), link.getUrl());
                    }
                })
                .filter(new Func1<AppLink, Boolean>() {
                    @Override
                    public Boolean call(AppLink appLink) {
                        return appLink != null;
                    }
                })
                .toSortedList(new Func2<AppLink, AppLink, Integer>() {
                    @Override
                    public Integer call(AppLink appLink, AppLink appLink2) {
                        return appLink.getTitle().compareTo(appLink2.getTitle()) * -1;
                    }
                });
    }
}
