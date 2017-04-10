package ru.ifmo.hycson.demoapp.dagger.home;

import dagger.Module;
import dagger.Provides;
import ru.ifmo.hycson.demoapp.presentation.home.HomeContract;
import ru.ifmo.hycson.demoapp.presentation.home.HomePresenter;
import ru.ifmo.hycson.demoapp.util.LogHelper;
import ru.ifmo.hymp.Parser;

@Module
public class HomeModule {
    @Provides
    HomeContract.Presenter provideHomePresenter(Parser parser) {
        LogHelper.d(LogHelper.TAG_MODULE, this.getClass().getSimpleName() + ".provideHomePresenter");
        return new HomePresenter(parser);
    }
}
