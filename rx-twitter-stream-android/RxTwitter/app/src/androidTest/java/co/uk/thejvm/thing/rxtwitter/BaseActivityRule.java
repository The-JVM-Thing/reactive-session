package co.uk.thejvm.thing.rxtwitter;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;

import co.uk.thejvm.thing.rxtwitter.common.di.ApplicationComponent;
import co.uk.thejvm.thing.rxtwitter.common.di.ApplicationModule;
import co.uk.thejvm.thing.rxtwitter.common.di.DaggerApplicationComponent;

/**
 * {@link BaseActivity} activity rule
 *
 * @param <T> type of activity for which rule is applied
 */
public abstract class BaseActivityRule<T extends BaseActivity> extends ActivityTestRule<T> {

    /**
     * Create a test rule
     *
     * @param activityClass  operating activity class
     * @param launchActivity if true activity will be launched before each test method
     *                       and destroyed after each test, if false activity must be launched
     *                       manually by test rule
     */
    public BaseActivityRule(Class<T> activityClass, boolean launchActivity) {
        super(activityClass, false, launchActivity);
    }

    @Override
    protected void beforeActivityLaunched() {
        RxTwitterApplication application = (RxTwitterApplication) InstrumentationRegistry.getTargetContext()
            .getApplicationContext();

        ApplicationComponent component = DaggerApplicationComponent.builder()
            .applicationModule(getApplicationModule())
            .build();

        application.setApplicationComponent(component);
    }

    /**
     * Provide {@link ApplicationModule} implementation
     *
     * @return Application module
     */
    public abstract ApplicationModule getApplicationModule();
}
