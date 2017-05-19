package co.uk.thejvm.thing.rxtwitter.common.util;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Calendar;
import java.util.Date;

import co.uk.thejvm.thing.rxtwitter.data.Tweet;
import twitter4j.ExtendedMediaEntity;
import twitter4j.GeoLocation;
import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.Place;
import twitter4j.RateLimitStatus;
import twitter4j.Scopes;
import twitter4j.Status;
import twitter4j.SymbolEntity;
import twitter4j.URLEntity;
import twitter4j.User;
import twitter4j.UserMentionEntity;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SimpleTwitterMapperTest {

    @Mock private Bitmap mockBitmap;
    @Mock private static User user;

    private SimpleTwitterMapper simpleTwitterMapper;

    private static final String TWEET_TEXT = "GO REACTIVE OR GO HOME";
    private static final Status FAKE_STATUS = new FakeStatus();
    private static final String EXPECTED_USER_NAME = "John Doe";
    private static final String EXPECTED_DATE_LABEL = "2017.05.11 21:55:11";
    private static final String EXPECTED_IMAGE_URI = "http://twitter/image.jpg";

    private Tweet expectedTweet;

    @Before
    public void setUp() {
        when(user.getProfileImageURL()).thenReturn(EXPECTED_IMAGE_URI);
        when(user.getName()).thenReturn(EXPECTED_USER_NAME);
        expectedTweet = new Tweet(TWEET_TEXT, EXPECTED_DATE_LABEL, EXPECTED_IMAGE_URI, EXPECTED_USER_NAME);
        simpleTwitterMapper = new SimpleTwitterMapper();
    }

    @Test
    public void whenTweetReceived_ShouldMapCorrectly() {
        Tweet tweet = simpleTwitterMapper.from(FAKE_STATUS);
        assertEquals(expectedTweet, tweet);
    }

    private static Date getTweetDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(2017, Calendar.MAY, 11, 21, 55, 11);
        return cal.getTime();
    }

    private static class FakeStatus implements Status {

        @Override
        public Date getCreatedAt() {
            return getTweetDate();
        }

        @Override
        public long getId() {
            return 0;
        }

        @Override
        public String getText() {
            return TWEET_TEXT;
        }

        @Override
        public String getSource() {
            return null;
        }

        @Override
        public boolean isTruncated() {
            return false;
        }

        @Override
        public long getInReplyToStatusId() {
            return 0;
        }

        @Override
        public long getInReplyToUserId() {
            return 0;
        }

        @Override
        public String getInReplyToScreenName() {
            return null;
        }

        @Override
        public GeoLocation getGeoLocation() {
            return null;
        }

        @Override
        public Place getPlace() {
            return null;
        }

        @Override
        public boolean isFavorited() {
            return false;
        }

        @Override
        public boolean isRetweeted() {
            return false;
        }

        @Override
        public int getFavoriteCount() {
            return 0;
        }

        @Override
        public User getUser() {
            return user;
        }

        @Override
        public boolean isRetweet() {
            return false;
        }

        @Override
        public Status getRetweetedStatus() {
            return null;
        }

        @Override
        public long[] getContributors() {
            return new long[0];
        }

        @Override
        public int getRetweetCount() {
            return 0;
        }

        @Override
        public boolean isRetweetedByMe() {
            return false;
        }

        @Override
        public long getCurrentUserRetweetId() {
            return 0;
        }

        @Override
        public boolean isPossiblySensitive() {
            return false;
        }

        @Override
        public String getLang() {
            return null;
        }

        @Override
        public Scopes getScopes() {
            return null;
        }

        @Override
        public String[] getWithheldInCountries() {
            return new String[0];
        }

        @Override
        public long getQuotedStatusId() {
            return 0;
        }

        @Override
        public Status getQuotedStatus() {
            return null;
        }

        @Override
        public int compareTo(@NonNull Status o) {
            return 0;
        }

        @Override
        public UserMentionEntity[] getUserMentionEntities() {
            return new UserMentionEntity[0];
        }

        @Override
        public URLEntity[] getURLEntities() {
            return new URLEntity[0];
        }

        @Override
        public HashtagEntity[] getHashtagEntities() {
            return new HashtagEntity[0];
        }

        @Override
        public MediaEntity[] getMediaEntities() {
            return new MediaEntity[0];
        }

        @Override
        public ExtendedMediaEntity[] getExtendedMediaEntities() {
            return new ExtendedMediaEntity[0];
        }

        @Override
        public SymbolEntity[] getSymbolEntities() {
            return new SymbolEntity[0];
        }

        @Override
        public RateLimitStatus getRateLimitStatus() {
            return null;
        }

        @Override
        public int getAccessLevel() {
            return 0;
        }
    }
}