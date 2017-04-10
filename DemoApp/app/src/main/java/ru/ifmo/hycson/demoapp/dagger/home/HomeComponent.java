package ru.ifmo.hycson.demoapp.dagger.home;


import dagger.Subcomponent;
import ru.ifmo.hycson.demoapp.presentation.home.HomeContract;

@Subcomponent(modules = HomeModule.class)
public interface HomeComponent {
    HomeContract.Presenter presenter();
}
