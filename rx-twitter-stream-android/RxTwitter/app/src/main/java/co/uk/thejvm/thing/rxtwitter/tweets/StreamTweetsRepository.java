package co.uk.thejvm.thing.rxtwitter.tweets;

import android.graphics.Bitmap;

import java.util.List;

import co.uk.thejvm.thing.rxtwitter.common.util.TwitterMapper;
import co.uk.thejvm.thing.rxtwitter.data.Tweet;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import twitter4j.Status;
import twitter4j.TwitterStream;

import static io.reactivex.Observable.just;

public class StreamTweetsRepository implements TweetsRepository {

    private final TwitterStream twitterStream;
    private final TwitterMapper twitterMapper;

    public StreamTweetsRepository(TwitterStream twitterStream, TwitterMapper twitterMapper) {
        this.twitterStream = twitterStream;
        this.twitterMapper = twitterMapper;
    }

    public Flowable<Tweet> getTweets(List<String> terms) {
        return Flowable.create(emitter -> {
             TweetListener listener = new TweetListener(emitter, twitterMapper);
             emitter.setCancellable(() -> twitterStream.removeListener(listener));
             twitterStream.addListener(listener);
             twitterStream.filter(terms.toArray(new String[terms.size()]));
         }, BackpressureStrategy.ERROR);
    }
}
