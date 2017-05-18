package co.uk.thejvm.thing.rxtwitter.common.di;

import android.app.Application;
import android.content.Context;

import co.uk.thejvm.thing.rxtwitter.common.repository.HardCodedSecretsStorage;
import co.uk.thejvm.thing.rxtwitter.common.repository.SecretsStorage;
import dagger.Module;
import dagger.Provides;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

@Module
public class ApplicationModule {

    private final Application application;

    public ApplicationModule(Application application) {
        this.application = application;
    }

    @Provides
    @PerApp
    public Context provideContext() {
        return application.getApplicationContext();
    }

    @Provides
    @PerApp
    public SecretsStorage provideSecretsStorage() {
        return new HardCodedSecretsStorage();
    }

    @Provides
    @PerApp
    public Configuration provideConfiguration(SecretsStorage secretsStorage) {
        return new ConfigurationBuilder()
            .setDebugEnabled(true)
            .setOAuthConsumerKey(secretsStorage.getConsumerKey())
            .setOAuthConsumerSecret(secretsStorage.getConsumerSecret())
            .setOAuthAccessToken(secretsStorage.getToken())
            .setOAuthAccessTokenSecret(secretsStorage.getTokenSecret())
            .build();
    }

    @Provides
    @PerApp
    public TwitterStream provideTwitterStream(Configuration configuration) {
        return new TwitterStreamFactory(configuration).getInstance();
    }

    @Provides
    @PerApp
    protected ModuleBootstrapper provideModuleBootstrapper() {
        return new ModuleBootstrapper();
    }
}
