package ru.ifmo.hycson.demoapp;

import android.app.Application;
import android.content.Context;

import ru.ifmo.hycson.demoapp.dagger.app.AppComponent;
import ru.ifmo.hycson.demoapp.dagger.app.AppModule;
import ru.ifmo.hycson.demoapp.dagger.app.DaggerAppComponent;
import ru.ifmo.hycson.demoapp.dagger.hymp.HypermediaComponent;


public class App extends Application {
    private AppComponent mAppComponent;
    private HypermediaComponent mHypermediaComponent;

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

    /**
     * Get or create {@link HypermediaComponent}
     */
    public HypermediaComponent plusHympComponent() {
        if (mHypermediaComponent == null) {
            mHypermediaComponent = mAppComponent.plusHympComponent();
        }
        return mHypermediaComponent;
    }

    /**
     * Stop lifecycle of {@link HypermediaComponent} and destroy all graph with dependencies
     */
    public void clearHympComponent() {
        mHypermediaComponent = null;
    }
}
