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
    String provideEntryPointUrl(PreferencesManager preferencesManager) {
        SelectedSocialNetwork socialNetwork = preferencesManager.retrieveSelectedSocialNetwork();
        LogHelper.d(LogHelper.TAG_MODULE, this.getClass().getSimpleName() + ".provideEntryPointUrl" + ", Selected network: " + socialNetwork);
        return socialNetwork.getEntryPointUrl();
    }

    @Provides
    @SocialNetworkScope
    HypermediaClient provideHydraHypermediaClient(String entryPointUrl) {
        LogHelper.d(LogHelper.TAG_MODULE, this.getClass().getSimpleName() + ".provideHydraHypermediaClient");
        return new HydraHypermediaClient(entryPointUrl);
    }
}
