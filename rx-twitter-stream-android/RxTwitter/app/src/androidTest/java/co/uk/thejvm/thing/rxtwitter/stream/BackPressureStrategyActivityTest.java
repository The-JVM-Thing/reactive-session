package co.uk.thejvm.thing.rxtwitter.stream;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;

import co.uk.thejvm.thing.rxtwitter.BaseActivity;
import co.uk.thejvm.thing.rxtwitter.BaseActivityRule;
import co.uk.thejvm.thing.rxtwitter.R;
import co.uk.thejvm.thing.rxtwitter.RxTwitterApplication;
import co.uk.thejvm.thing.rxtwitter.TestModule;
import co.uk.thejvm.thing.rxtwitter.common.BackPressureStrategy;
import co.uk.thejvm.thing.rxtwitter.common.di.ApplicationModule;
import co.uk.thejvm.thing.rxtwitter.espresso.DisableAnimationRule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class BackPressureStrategyActivityTest {

    @ClassRule
    public static DisableAnimationRule disableAnimationRule = new DisableAnimationRule();

    private static final boolean LAUNCH_ACTIVITY = false;

    public BaseActivityRule<BackPressureStrategyActivity> activityTestRule =
            new BaseActivityRule<BackPressureStrategyActivity>(BackPressureStrategyActivity.class, LAUNCH_ACTIVITY) {
                @Override
                public ApplicationModule getApplicationModule() {
                    return new TestModule((RxTwitterApplication) InstrumentationRegistry.getContext().getApplicationContext());
                }
            };

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void whenSelectedNoBackpressureOption_ShouldLaunchWithActivityWithSuchOption() {
        ResultRobot resultRobot = new BackpressureTestRobot().launchActivity().selectOption(0).verify();
        resultRobot.checkRecordedIntent(BackPressureStrategy.NO_STRATEGY);
    }

    @Test
    public void whenSelectedBufferBackpressureOption_ShouldLaunchWithActivityWithSuchOption() {
        ResultRobot resultRobot = new BackpressureTestRobot().launchActivity().selectOption(1).verify();
        resultRobot.checkRecordedIntent(BackPressureStrategy.BUFFER);
    }

    @Test
    public void whenSelectedDropBackpressureOption_ShouldLaunchWithActivityWithSuchOption() {
        ResultRobot resultRobot = new BackpressureTestRobot().launchActivity().selectOption(2).verify();
        resultRobot.checkRecordedIntent(BackPressureStrategy.DROP);
    }

    @Test
    public void whenSelectedLatestBackpressureOption_ShouldLaunchWithActivityWithSuchOption() {
        ResultRobot resultRobot = new BackpressureTestRobot().launchActivity().selectOption(3).verify();
        resultRobot.checkRecordedIntent(BackPressureStrategy.LATEST);
    }

    private class BackpressureTestRobot {
        public ResultRobot verify() {
            return new ResultRobot();
        }

        public BackpressureTestRobot selectOption(int option) {
            onView(withId(R.id.backpressure_strategy_options)).perform(RecyclerViewActions.actionOnItemAtPosition(option, click()));
            return this;
        }

        public BackpressureTestRobot launchActivity() {
            activityTestRule.launchActivity(new Intent());
            return this;
        }
    }

    private class ResultRobot {
        public void checkRecordedIntent(BackPressureStrategy strategy) {
            intended(hasExtra(BaseActivity.BACKPRESSURE_STRATEGY_EXTRA_KEY, strategy));
        }
    }
}