package co.uk.thejvm.thing.rxtwitter.common.di;

import android.util.Log;

import com.google.common.base.Function;

import javax.inject.Named;

import co.uk.thejvm.thing.rxtwitter.BaseActivity;
import co.uk.thejvm.thing.rxtwitter.common.BackPressureStrategy;
import co.uk.thejvm.thing.rxtwitter.common.util.ExecutionScheduler;
import co.uk.thejvm.thing.rxtwitter.common.util.ImageIoScheduler;
import co.uk.thejvm.thing.rxtwitter.common.util.TweetIOScheduler;
import co.uk.thejvm.thing.rxtwitter.common.util.SimpleTwitterMapper;
import co.uk.thejvm.thing.rxtwitter.common.util.TwitterMapper;
import co.uk.thejvm.thing.rxtwitter.common.util.UIScheduler;
import co.uk.thejvm.thing.rxtwitter.data.Tweet;
import co.uk.thejvm.thing.rxtwitter.stream.TwitterStreamPresenter;
import co.uk.thejvm.thing.rxtwitter.tweets.ImageLoaderTwitterAvatarRepository;
import co.uk.thejvm.thing.rxtwitter.tweets.StreamTweetsRepository;
import co.uk.thejvm.thing.rxtwitter.tweets.TweetsRepository;
import co.uk.thejvm.thing.rxtwitter.tweets.TwitterAvatarRepository;
import dagger.Module;
import dagger.Provides;
import io.reactivex.BackpressureOverflowStrategy;
import io.reactivex.Flowable;
import twitter4j.TwitterStream;

@Module
public class ActivityModule {

    private final BaseActivity activity;
    private final BackPressureStrategy backPressureStrategy;

    public ActivityModule(BaseActivity activity, BackPressureStrategy backPressureStrategy) {
        this.activity = activity;
        this.backPressureStrategy = backPressureStrategy;
    }

    @ActivityScope
    @Provides
    public TwitterMapper provideTwitterMapper() {
        return new SimpleTwitterMapper();
    }

    @ActivityScope
    @Provides
    public TweetsRepository provideTweetsRepository(TwitterStream twitterStream, TwitterMapper twitterMapper) {
        return new StreamTweetsRepository(twitterStream, twitterMapper);
    }

    @ActivityScope
    @Provides
    public TwitterAvatarRepository provideTwitterAvatarRepository() {
        return new ImageLoaderTwitterAvatarRepository();
    }

    @ActivityScope
    @Provides @Named("ui")
    public ExecutionScheduler getUiExecutionScheduler() {
        return new UIScheduler();
    }

    @ActivityScope
    @Provides @Named("tweetio")
    public ExecutionScheduler getTweetIoExecutionScheduler() {
        return new TweetIOScheduler();
    }

    @ActivityScope
    @Provides @Named("imageio")
    public ExecutionScheduler getImageIoExecutionScheduler() {
        return new ImageIoScheduler();
    }

    @ActivityScope
    @Provides
    public TwitterStreamPresenter provideTwitterStreamPresenter(TweetsRepository repository,
                                                                TwitterAvatarRepository avatarRepository,
                                                                @Named("ui") ExecutionScheduler uiScheduler,
                                                                @Named("tweetio") ExecutionScheduler tweetIoScheduler,
                                                                @Named("imageio") ExecutionScheduler imageIoScheduler,
                                                                Function<Flowable<Tweet>, Flowable<Tweet>> function) {

        return new TwitterStreamPresenter(repository, avatarRepository, uiScheduler, tweetIoScheduler, imageIoScheduler, function);
    }

    @ActivityScope
    @Provides
    public Function<Flowable<Tweet>, Flowable<Tweet>> provideBackPressureStrategy() {
        Function<Flowable<Tweet>, Flowable<Tweet>> backPressureStrategyFunction;
        switch (backPressureStrategy) {
            case BUFFER:
                backPressureStrategyFunction = flowable -> flowable.onBackpressureBuffer(50, () -> Log.d("", "Buffer full, dropping tweets"), BackpressureOverflowStrategy.DROP_OLDEST);
                break;
            case DROP:
                backPressureStrategyFunction = flowable -> flowable.onBackpressureDrop();
                break;
            case LATEST:
                backPressureStrategyFunction = flowable -> flowable.onBackpressureLatest();
                break;
            default:
                backPressureStrategyFunction = flowable -> flowable;
                break;
        }

        return backPressureStrategyFunction;
    }

}
