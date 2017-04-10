package ru.ifmo.hycson.demoapp.dagger.hymp;

import dagger.Module;
import dagger.Provides;
import ru.ifmo.hycson.demoapp.BuildConfig;
import ru.ifmo.hycson.demoapp.dagger.SocialNetworkScope;
import ru.ifmo.hycson.demoapp.data.PreferencesManager;
import ru.ifmo.hycson.demoapp.util.LogHelper;
import ru.ifmo.hymp.HypermediaMessageParser;
import ru.ifmo.hymp.Parser;

@Module
public class HympModule {

    @Provides
    @SocialNetworkScope
    String provideBaseUrl(PreferencesManager preferencesManager) {
        LogHelper.d(LogHelper.TAG_MODULE, this.getClass().getSimpleName() + ".provideBaseUrl");
        return BuildConfig.VK_ENTRY_POINT;
    }

    @Provides
    @SocialNetworkScope
    Parser provideHypermediaMessageParser(String baseUrl) {
        LogHelper.d(LogHelper.TAG_MODULE, this.getClass().getSimpleName() + ".provideHypermediaMessageParser");
        return new HypermediaMessageParser(baseUrl);
    }
}
