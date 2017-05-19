package co.uk.thejvm.thing.rxtwitter.tweets;

import android.graphics.Bitmap;
import android.view.View;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import io.reactivex.FlowableEmitter;

/**
 * Created by James on 14/05/2017.
 */

class AvatarListener extends SimpleImageLoadingListener {

    FlowableEmitter emitter;

    public AvatarListener(FlowableEmitter emitter) {
        this.emitter = emitter;
    }

    @Override
    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
        // Can we use default image here? dont want to end stream if the image cannot be obtained
        // Or use onErrorResumeNext?
        //observer.onError(failReason.getCause());
    }

    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
        emitter.onNext(loadedImage);
        emitter.onComplete();
    }
}