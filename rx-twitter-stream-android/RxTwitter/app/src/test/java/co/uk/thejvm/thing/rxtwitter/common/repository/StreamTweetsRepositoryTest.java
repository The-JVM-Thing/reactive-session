package co.uk.thejvm.thing.rxtwitter.common.repository;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import co.uk.thejvm.thing.rxtwitter.common.util.TwitterMapper;
import co.uk.thejvm.thing.rxtwitter.tweets.StreamTweetsRepository;
import co.uk.thejvm.thing.rxtwitter.tweets.TweetListener;
import twitter4j.Status;
import twitter4j.TwitterStream;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class StreamTweetsRepositoryTest {

    @Mock private TwitterStream twitterStream;
    @Mock private TwitterMapper twitterMapper;
    @Mock private Status status;
    @Captor private ArgumentCaptor<TweetListener> listenerCaptor;
    private StreamTweetsRepository streamTweetsRepository;
    private List<String> terms = Lists.newArrayList("android", "rxjava");

    @Before
    public void setUp() throws Exception {
        streamTweetsRepository = new StreamTweetsRepository(twitterStream, twitterMapper);
    }

    @Test
    public void whenGetFlowableTweetsThenStartListeningToTwitterStream() {
        streamTweetsRepository.getTweets(terms).subscribe();
        verify(twitterStream).filter(terms.toArray(new String[terms.size()]));
    }
}