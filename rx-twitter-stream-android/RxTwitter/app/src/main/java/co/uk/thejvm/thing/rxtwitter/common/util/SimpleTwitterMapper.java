package co.uk.thejvm.thing.rxtwitter.common.util;

import android.graphics.Bitmap;

import java.text.SimpleDateFormat;
import java.util.Date;

import co.uk.thejvm.thing.rxtwitter.data.Tweet;
import twitter4j.Status;

public class SimpleTwitterMapper implements TwitterMapper {

    private static final String PATTERN = "yyyy.MM.dd HH:mm:ss";

    @Override
    public Tweet from(Status status) {
        return new Tweet(status.getText(), formatDate(status.getCreatedAt()), status.getUser().getProfileImageURL(), status.getUser().getName());
    }

    private String formatDate(Date date) {
        return new SimpleDateFormat(PATTERN).format(date);
    }
}
