package ru.ifmo.hycson.demoapp.presentation.home;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import ru.ifmo.hycson.demoapp.R;
import ru.ifmo.hycson.demoapp.presentation.base.ToolbarActivity;

public class HomeActivity extends ToolbarActivity {

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_main;
    }

    @Nullable
    @Override
    protected Fragment createDisplayedFragment() {
        return HomeFragment.newInstance();
    }
}
