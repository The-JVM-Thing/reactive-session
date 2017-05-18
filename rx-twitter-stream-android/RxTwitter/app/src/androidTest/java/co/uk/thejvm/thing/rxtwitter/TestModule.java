package co.uk.thejvm.thing.rxtwitter;

import android.app.Application;

import co.uk.thejvm.thing.rxtwitter.common.BackPressureStrategy;
import co.uk.thejvm.thing.rxtwitter.common.di.ActivityModule;
import co.uk.thejvm.thing.rxtwitter.common.di.ApplicationModule;
import co.uk.thejvm.thing.rxtwitter.common.di.ModuleBootstrapper;
import twitter4j.TwitterStream;
import twitter4j.conf.Configuration;

import static org.mockito.Mockito.mock;

/**
 * Dagger2 test module used for espresso tests
 */
public class TestModule extends ApplicationModule {

    private TwitterStream mockTwitterStream = mock(TwitterStream.class);

    public TestModule(Application application) {
        super(application);
    }

    @Override
    protected ModuleBootstrapper provideModuleBootstrapper() {
        return new ModuleBootstrapper() {
            @Override
            public ActivityModule getNewActivityModule(BaseActivity baseActivity, BackPressureStrategy backPressureStrategy) {
                return getActivityModule(baseActivity, backPressureStrategy);
            }
        };
    }

    protected ActivityModule getActivityModule(BaseActivity baseActivity, BackPressureStrategy strategy) {
        return new ActivityModule(baseActivity, strategy);
    }

    @Override
    public TwitterStream provideTwitterStream(Configuration configuration) {
        return mockTwitterStream;
    }
}
