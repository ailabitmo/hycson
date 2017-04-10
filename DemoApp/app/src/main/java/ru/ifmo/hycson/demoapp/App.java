package ru.ifmo.hycson.demoapp;

import android.app.Application;
import android.content.Context;

import ru.ifmo.hycson.demoapp.dagger.app.AppComponent;
import ru.ifmo.hycson.demoapp.dagger.app.AppModule;
import ru.ifmo.hycson.demoapp.dagger.app.DaggerAppComponent;
import ru.ifmo.hycson.demoapp.dagger.hymp.HympComponent;


public class App extends Application {
    private AppComponent mAppComponent;
    private HympComponent mHympComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public static App getApp(Context context) {
        return (App) context.getApplicationContext();
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }

    public HympComponent plusHympComponent() {
        // always get only one instance
        if (mHympComponent == null) {
            // start lifecycle of HympComponent
            mHympComponent = mAppComponent.plusHympComponent();
        }
        return mHympComponent;
    }

    public void clearHympComponent() {
        // end lifecycle of HympComponent
        mHympComponent = null;
    }
}
