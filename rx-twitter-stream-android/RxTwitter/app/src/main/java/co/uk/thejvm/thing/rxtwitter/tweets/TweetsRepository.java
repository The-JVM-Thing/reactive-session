package co.uk.thejvm.thing.rxtwitter.tweets;

import java.util.List;

import co.uk.thejvm.thing.rxtwitter.data.Tweet;
import io.reactivex.Flowable;

public interface TweetsRepository {
    Flowable<Tweet> getTweets(List<String> terms);
}
