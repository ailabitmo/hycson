package ru.ifmo.hycson.demoapp;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.ifmo.hycson.demoapp.data.PreferencesManager;
import ru.ifmo.hycson.demoapp.util.LogHelper;

@Module
class AppModule {
    private final Application mApplication;

    AppModule(Application application) {
        mApplication = application;
    }

    @Provides
    @Singleton
    Application provideApplication() {
        LogHelper.d(LogHelper.TAG_MODULE, this.getClass().getSimpleName() + ".provideApplication");
        return mApplication;
    }

    @Provides
    @Singleton
    Context provideContext(Application application) {
        LogHelper.d(LogHelper.TAG_MODULE, this.getClass().getSimpleName() + ".provideContext");
        return application.getApplicationContext();
    }

    @Provides
    @Singleton
    PreferencesManager providePreferencesManager(Context context) {
        LogHelper.d(LogHelper.TAG_MODULE, this.getClass().getSimpleName() + ".providePreferencesManager");
        return new PreferencesManager(context);
    }
}
