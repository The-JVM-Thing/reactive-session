package co.uk.thejvm.thing.rxtwitter.tweets;

import co.uk.thejvm.thing.rxtwitter.common.util.TwitterMapper;
import co.uk.thejvm.thing.rxtwitter.data.Tweet;
import io.reactivex.FlowableEmitter;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

public class TweetListener implements StatusListener {
    final FlowableEmitter<Tweet> emitter;
    final TwitterMapper twitterMapper;

    public TweetListener(FlowableEmitter<Tweet> emitter, TwitterMapper twitterMapper) {
        this.emitter = emitter;
        this.twitterMapper = twitterMapper;
    }

    @Override
    public void onStatus(Status status) {
        emitter.onNext(twitterMapper.from(status));
    }

    @Override
    public void onException(Exception ex) {
        emitter.onError(ex);
    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
    }

    @Override
    public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
    }

    @Override
    public void onScrubGeo(long userId, long upToStatusId) {
    }

    @Override
    public void onStallWarning(StallWarning warning) {
    }
}