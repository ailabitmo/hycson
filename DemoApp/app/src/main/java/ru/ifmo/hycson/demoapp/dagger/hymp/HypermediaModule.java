package ru.ifmo.hycson.demoapp.dagger.hymp;

import dagger.Module;
import dagger.Provides;
import ru.ifmo.hycson.demoapp.dagger.SocialNetworkScope;
import ru.ifmo.hycson.demoapp.data.PreferencesManager;
import ru.ifmo.hycson.demoapp.presentation.auth.SelectedSocialNetwork;
import ru.ifmo.hycson.demoapp.util.LogHelper;
import ru.ifmo.hymp.HydraHypermediaClient;
import ru.ifmo.hymp.HypermediaClient;

@Module
public class HypermediaModule {
    @Provides
    @SocialNetworkScope
    Bundle provideEntryPointUrl(PreferencesManager preferencesManager) {
        SelectedSocialNetwork socialNetwork = preferencesManager.retrieveSelectedSocialNetwork();
        String token = preferencesManager.retrieveAccessToken(socialNetwork.getKey());
        LogHelper.d(LogHelper.TAG_MODULE, this.getClass().getSimpleName() + ".provideEntryPointUrl" + ", Selected network: " + socialNetwork);
        return new Bundle(socialNetwork.getEntryPointUrl(), token);
    }

    @Provides
    @SocialNetworkScope
    HypermediaClient provideHydraHypermediaClient(Bundle bundle) {
        LogHelper.d(LogHelper.TAG_MODULE, this.getClass().getSimpleName() + ".provideHydraHypermediaClient");
        return new HydraHypermediaClient(bundle.url, bundle.token);
    }

    static class Bundle {
        private String url;
        private String token;

        Bundle(String url, String token) {
            this.url = url;
            this.token = token;
        }
    }
}
