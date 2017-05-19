package co.uk.thejvm.thing.rxtwitter.tweets;

import android.graphics.Bitmap;

import java.util.List;

import io.reactivex.Flowable;

public interface TwitterAvatarRepository {

    Flowable<Bitmap> getAvatar(String imageUri);
}
