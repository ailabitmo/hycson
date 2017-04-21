package ru.ifmo.hycson.demoapp.dagger.app;

import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.apis.VkontakteApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.oauth.OAuth10aService;
import com.github.scribejava.core.oauth.OAuth20Service;

import dagger.Module;
import dagger.Provides;
import ru.ifmo.hycson.demoapp.BuildConfig;
import ru.ifmo.hycson.demoapp.util.LogHelper;

@Module
public class OAuthModule {

    @Provides
    OAuth20Service provideVKOAuth20Service() {
        LogHelper.d(LogHelper.TAG_MODULE, this.getClass().getSimpleName() + ".provideVKOAuth20Service");
        return new ServiceBuilder()
                .apiKey(BuildConfig.VK_API_KEY)
                .apiSecret(BuildConfig.VK_API_SECRET)
                .callback(BuildConfig.VK_API_CALLBACK_URI)
                .scope("wall,friends,messages")
                .build(VkontakteApi.instance());
    }

    @Provides
    OAuth10aService provideTwitterOAuth10aService() {
        LogHelper.d(LogHelper.TAG_MODULE, this.getClass().getSimpleName() + ".provideTwitterOAuth10aService");
        return new ServiceBuilder()
                .apiKey(BuildConfig.TWITTER_API_KEY)
                .apiSecret(BuildConfig.TWITTER_API_SECRET)
                .callback(BuildConfig.TWITTER_API_CALLBACK_URI)
                .build(TwitterApi.instance());
    }
}
