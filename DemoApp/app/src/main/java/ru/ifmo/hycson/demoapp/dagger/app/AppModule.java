package ru.ifmo.hycson.demoapp.dagger.app;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.ifmo.hycson.demoapp.data.PreferencesManager;
import ru.ifmo.hycson.demoapp.util.LogHelper;

@Module
public class AppModule {
    private final Application mApplication;

    public AppModule(Application application) {
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
