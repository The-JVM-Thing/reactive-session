package co.uk.thejvm.thing.rxtwitter.common.di;

import dagger.Component;
import twitter4j.TwitterStream;

@PerApp
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    TwitterStream getTwitterStream();
    ModuleBootstrapper getModuleBootstrapper();
}
