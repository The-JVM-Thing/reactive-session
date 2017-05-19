package co.uk.thejvm.thing.rxtwitter.stream;

import android.util.Log;

import com.google.common.base.Function;

import java.util.List;

import co.uk.thejvm.thing.rxtwitter.common.BasePresenter;
import co.uk.thejvm.thing.rxtwitter.common.util.ExecutionScheduler;
import co.uk.thejvm.thing.rxtwitter.data.Tweet;
import co.uk.thejvm.thing.rxtwitter.data.TweetViewModel;
import co.uk.thejvm.thing.rxtwitter.tweets.TweetsRepository;
import co.uk.thejvm.thing.rxtwitter.tweets.TwitterAvatarRepository;
import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subscribers.DisposableSubscriber;

import static io.reactivex.Flowable.just;
import static io.reactivex.Flowable.zip;

public class TwitterStreamPresenter implements BasePresenter<TwitterStreamView> {

    private static final String TAG = "TwitterStreamPresenter";

    private TwitterStreamView twitterStreamView;
    private final TweetsRepository tweetsRepository;
    private final TwitterAvatarRepository avatarRepository;
    private final ExecutionScheduler uiScheduler, tweetIo, imageIo;
    private CompositeDisposable disposable = new CompositeDisposable();
    private final Function<Flowable<Tweet>, Flowable<Tweet>> backPressureStrategyFunction;

    public TwitterStreamPresenter(TweetsRepository tweetsRepository, TwitterAvatarRepository avatarRepository,
                                  ExecutionScheduler uiScheduler, ExecutionScheduler tweetIo, ExecutionScheduler imageIo,
                                  Function<Flowable<Tweet>, Flowable<Tweet>> backPressureStrategyFunction) {


        this.tweetsRepository = tweetsRepository;
        this.avatarRepository = avatarRepository;
        this.uiScheduler = uiScheduler;
        this.backPressureStrategyFunction = backPressureStrategyFunction;
        this.tweetIo = tweetIo;
        this.imageIo = imageIo;
    }

    public void connectToStream(List<String> terms) {
        twitterStreamView.showLoading();

        backPressureStrategyFunction.apply(tweetsRepository.getTweets(terms))
            .subscribeOn(tweetIo.getScheduler())
            .observeOn(imageIo.getScheduler())
            .flatMap(rawTweet ->
                zip(
                    just(rawTweet), avatarRepository.getAvatar(rawTweet.getImageUri()),
                    (tweet, bitmap) -> new TweetViewModel(tweet.getContent(), bitmap, tweet.getDateLabel(), tweet.getUserName())
                )
            )
            .observeOn(uiScheduler.getScheduler())
            .subscribe(new TweetStreamSubcriber());
    }

    @Override
    public void setView(TwitterStreamView view) {
        this.twitterStreamView = view;
    }

    @Override
    public void onPause() {
        disposable.clear();
    }

    public boolean isDisposed() {
        return disposable.size() == 0;
    }

    private class TweetStreamSubcriber extends DisposableSubscriber<TweetViewModel> {

        public TweetStreamSubcriber() {
            disposable.add(this);
        }

        @Override
        public void onNext(@NonNull TweetViewModel tweet) {
            twitterStreamView.renderTweet(tweet);
        }

        @Override
        public void onError(@NonNull Throwable e) {
            Log.e(TAG, e.toString());
            twitterStreamView.hideLoading();
            twitterStreamView.showError();
        }

        @Override
        public void onComplete() {
            twitterStreamView.hideLoading();
        }
    }
}
