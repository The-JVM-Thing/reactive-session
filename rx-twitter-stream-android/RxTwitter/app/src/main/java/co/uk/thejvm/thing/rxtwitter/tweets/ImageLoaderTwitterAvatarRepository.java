package co.uk.thejvm.thing.rxtwitter.tweets;

import android.graphics.Bitmap;
import android.util.Log;

import com.nostra13.universalimageloader.core.ImageLoader;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;

public class ImageLoaderTwitterAvatarRepository implements TwitterAvatarRepository {

    ImageLoader imgLoader = ImageLoader.getInstance();

    @Override
    public Flowable<Bitmap> getAvatar(String imageUri) {
        return Flowable.create(e -> {
            try {
                Thread.sleep(50);
            } catch (Exception exception) {

            }
            AvatarListener listener = new AvatarListener(e);
            imgLoader.loadImage(imageUri, listener);
            // https://github.com/nostra13/Android-Universal-Image-Loader/issues/586
            // e.setCancellable(() -> Log.d("ImgLoadTwitt", "cancelling "  + imageUri ));
        }, BackpressureStrategy.ERROR);
    }

}
