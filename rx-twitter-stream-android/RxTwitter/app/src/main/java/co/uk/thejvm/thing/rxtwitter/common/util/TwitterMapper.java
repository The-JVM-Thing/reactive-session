package co.uk.thejvm.thing.rxtwitter.common.util;

import co.uk.thejvm.thing.rxtwitter.data.Tweet;
import twitter4j.Status;

public interface TwitterMapper {
    Tweet from(Status status);
}
