package ru.ifmo.hycson.demoapp.util.rx;

import java.util.List;

import ru.ifmo.hycson.demoapp.presentation.navigation.NavigationRules;
import ru.ifmo.hycson.demoapp.presentation.navigation.links.AppLink;
import ru.ifmo.hycson.demoapp.presentation.navigation.links.display.DisplayableAppLink;
import ru.ifmo.hymp.entities.Link;
import ru.ifmo.hymp.entities.Operation;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

public class DisplayableAppLinksTransformer implements Observable.Transformer<Link, List<DisplayableAppLink>> {
    @Override
    public Observable<List<DisplayableAppLink>> call(Observable<Link> linkObservable) {
        return linkObservable
                .flatMap(new Func1<Link, Observable<Operation>>() {
                    @Override
                    public Observable<Operation> call(Link link) {
                        return Observable.from(link.getOperations())
                                .filter(new Func1<Operation, Boolean>() {
                                    @Override
                                    public Boolean call(Operation operation) {
                                        return operation.getType() == Operation.Type.GET;
                                    }
                                });
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
                        return appLink instanceof DisplayableAppLink;
                    }
                })
                .map(new Func1<AppLink, DisplayableAppLink>() {
                    @Override
                    public DisplayableAppLink call(AppLink appLink) {
                        return (DisplayableAppLink) appLink;
                    }
                })
                .toSortedList(new Func2<DisplayableAppLink, DisplayableAppLink, Integer>() {
                    @Override
                    public Integer call(DisplayableAppLink appLink, DisplayableAppLink appLink2) {
                        return appLink.getTitle().compareTo(appLink2.getTitle()) * -1;
                    }
                });
    }
}
