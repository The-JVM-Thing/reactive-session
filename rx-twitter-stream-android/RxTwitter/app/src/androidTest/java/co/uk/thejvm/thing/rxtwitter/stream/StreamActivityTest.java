package co.uk.thejvm.thing.rxtwitter.stream;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.core.deps.guava.collect.Lists;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.test.runner.AndroidJUnit4;

import com.google.common.base.Function;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;

import co.uk.thejvm.thing.rxtwitter.BaseActivity;
import co.uk.thejvm.thing.rxtwitter.BaseActivityRule;
import co.uk.thejvm.thing.rxtwitter.R;
import co.uk.thejvm.thing.rxtwitter.RxTwitterApplication;
import co.uk.thejvm.thing.rxtwitter.TestModule;
import co.uk.thejvm.thing.rxtwitter.common.BackPressureStrategy;
import co.uk.thejvm.thing.rxtwitter.common.di.ActivityModule;
import co.uk.thejvm.thing.rxtwitter.common.di.ApplicationModule;
import co.uk.thejvm.thing.rxtwitter.common.util.ExecutionScheduler;
import co.uk.thejvm.thing.rxtwitter.data.Tweet;
import co.uk.thejvm.thing.rxtwitter.data.TweetViewModel;
import co.uk.thejvm.thing.rxtwitter.espresso.DisableAnimationRule;
import co.uk.thejvm.thing.rxtwitter.tweets.TweetsRepository;
import co.uk.thejvm.thing.rxtwitter.tweets.TwitterAvatarRepository;
import io.reactivex.Flowable;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static co.uk.thejvm.thing.rxtwitter.espresso.CustomMatchers.withRecyclerViewSize;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class StreamActivityTest {

    @ClassRule
    public static DisableAnimationRule disableAnimationRule = new DisableAnimationRule();

    private TwitterStreamPresenter mockTwitterStreamPresenter = mock(TwitterStreamPresenter.class);
    private ArgumentCaptor<TwitterStreamView> viewArgumentCaptor = ArgumentCaptor.forClass(TwitterStreamView.class);

    Bitmap fakeBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);

    private static final boolean LAUNCH_ACTIVITY = false;
    private Context context = InstrumentationRegistry.getTargetContext();
    private CountingIdlingResource presenterIdlingResource;

    @Rule
    public BaseActivityRule<StreamActivity> activityTestRule =
        new BaseActivityRule<StreamActivity>(StreamActivity.class, LAUNCH_ACTIVITY) {

            @Override
            public ApplicationModule getApplicationModule() {

                RxTwitterApplication application = (RxTwitterApplication) context.getApplicationContext();

                return new TestModule(application) {

                    @Override
                    protected ActivityModule getActivityModule(BaseActivity baseActivity, BackPressureStrategy backPressureStrategy) {

                        return new ActivityModule(baseActivity, backPressureStrategy) {
                            @Override
                            public TwitterStreamPresenter provideTwitterStreamPresenter(TweetsRepository repository,
                                                                                        TwitterAvatarRepository avatarRepository,
                                                                                        ExecutionScheduler uiScheduler,
                                                                                        ExecutionScheduler tweetScheduler,
                                                                                        ExecutionScheduler imageScheduler,
                                                                                        Function<Flowable<Tweet>, Flowable<Tweet>> backPressureStrategyFunction) {
                                return mockTwitterStreamPresenter;
                            }
                        };
                    }
                };
            }
        };

    @Before
    public void setUp() throws Exception {
        presenterIdlingResource = new CountingIdlingResource("FooServerCalls");
        Espresso.registerIdlingResources(presenterIdlingResource);
    }

    /**
     * When twee received should render on recycler view.
     */
    @Test
    public void whenTweetReceived_ShouldRenderOnRecyclerView() {
        ResultRobot resultRobot = new StreamTweetActivityRobot().launchActivity().performSearch().verify();
        resultRobot.checkIfTweetTextIsVisibleOnScreen();
        resultRobot.checkIfTweetDateIsVisible();
    }

    /**
     * When search performed should clear the tweets list.
     */
    @Test
    public void whenSearchPerformed_ShouldClearTheTweetsList() {
        ResultRobot resultRobot = new StreamTweetActivityRobot().launchActivity().performSearch().verify();

        resultRobot.checkSizeOfTweetsList();
        resultRobot.verifyIfReconectedToStreamByNewTerm();
    }

    @Test
    public void whenRecyclerViewReceivedMoreTweetsWhenItCanHandle_ShouldClearSpaceToTakeNew() {
        ResultRobot resultRobot = new StreamTweetActivityRobot().launchActivity(30).performSearch().verify();
        resultRobot.checkSizeOfTweetsList(StreamActivity.MAX_TWEETS);
    }

    @Test
    public void whenActivityPaused_ShouldPausePresenter() {
        ResultRobot resultRobot = new StreamTweetActivityRobot().launchActivity().goAwayFromActivity().verify();
        resultRobot.ensurePresenterIsPaused();
    }

    private class StreamTweetActivityRobot {

        private List<TweetViewModel> tweets = Collections.EMPTY_LIST;

        public ResultRobot verify() {
            return new ResultRobot(tweets);
        }

        public StreamTweetActivityRobot launchActivity() {
            launchActivity(1);
            return this;
        }

        public StreamTweetActivityRobot launchActivity(int numberOfTweets) {

            captureView();
            tweets = stubStream(numberOfTweets);

            Intent startIntent = new Intent();
            activityTestRule.launchActivity(startIntent);
            return this;
        }

        private void captureView() {
            doNothing().when(mockTwitterStreamPresenter).setView(viewArgumentCaptor.capture());
        }

        private TwitterStreamView getTwitterStreamView() {
            return viewArgumentCaptor.getValue();
        }

        private List<TweetViewModel> stubStream(int numberOfTweets) {
            List<TweetViewModel> tweets = getTweetsList(numberOfTweets);

            doAnswer(invocation -> new Handler().postAtFrontOfQueue(() -> {
                presenterIdlingResource.increment();
                for (TweetViewModel tweet : tweets) {
                    getTwitterStreamView().renderTweet(tweet);
                }
                presenterIdlingResource.decrement();
            })).when(mockTwitterStreamPresenter).connectToStream(any());

            return tweets;
        }

        private List<TweetViewModel> getTweetsList(int numberOfTweets) {
            List<TweetViewModel> tweetViewModels = Lists.newArrayList();
            for (int i = 0; i < numberOfTweets; i++) {
                tweetViewModels.add(new TweetViewModel("go reactive or go home", fakeBitmap, "2017.05.11 21:00", "John Doe"));
            }
            return tweetViewModels;
        }

        public StreamTweetActivityRobot performSearch() {
            onView(withId(R.id.action_search)).perform(click());
            onView(withId(R.id.terms_search)).perform(pressImeActionButton());
            return this;
        }

        public StreamTweetActivityRobot goAwayFromActivity() {
            activityTestRule.getActivity().onPause();
            return this;
        }
    }

    private class ResultRobot {

        private final List<TweetViewModel> tweets;

        public ResultRobot(List<TweetViewModel> tweets) {
            this.tweets = tweets;
        }

        public void checkIfTweetTextIsVisibleOnScreen() {
            for (TweetViewModel tweet : tweets) {
                onView(withText(tweet.getContent())).check(matches(isDisplayed()));
            }
        }

        public void checkIfTweetDateIsVisible() {
            for (TweetViewModel tweet : tweets) {
                onView(withText(tweet.getDateLabel())).check(matches(isDisplayed()));
            }
        }

        public void checkSizeOfTweetsList() {
            checkSizeOfTweetsList(tweets.size());
        }

        public void checkSizeOfTweetsList(int expectedSize) {
            onView(withId(R.id.live_tweets_list)).check(matches(withRecyclerViewSize(expectedSize)));
        }

        public void verifyIfReconectedToStreamByNewTerm() {
            verify(mockTwitterStreamPresenter).onPause();
            verify(mockTwitterStreamPresenter).connectToStream(anyList());
        }

        public void ensurePresenterIsPaused() {
            verify(mockTwitterStreamPresenter).onPause();
        }
    }
}