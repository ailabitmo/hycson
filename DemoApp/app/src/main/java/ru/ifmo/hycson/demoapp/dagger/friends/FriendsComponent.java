package ru.ifmo.hycson.demoapp.dagger.friends;

import dagger.Subcomponent;
import ru.ifmo.hycson.demoapp.presentation.friends.FriendsContract;

@Subcomponent(modules = FriendsModule.class)
public interface FriendsComponent {
    FriendsContract.Presenter presenter();
}
