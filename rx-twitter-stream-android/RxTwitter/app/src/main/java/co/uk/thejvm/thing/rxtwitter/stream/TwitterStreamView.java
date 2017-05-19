package co.uk.thejvm.thing.rxtwitter.stream;

import co.uk.thejvm.thing.rxtwitter.common.BaseView;
import co.uk.thejvm.thing.rxtwitter.data.TweetViewModel;

public interface TwitterStreamView extends BaseView {
    void renderTweet(TweetViewModel tweet);
}
