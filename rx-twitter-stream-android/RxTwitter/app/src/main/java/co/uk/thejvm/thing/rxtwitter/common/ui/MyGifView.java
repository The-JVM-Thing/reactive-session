package co.uk.thejvm.thing.rxtwitter.common.ui;

import android.content.Context;
import android.util.AttributeSet;

import com.cunoraz.gifview.library.GifView;

import co.uk.thejvm.thing.rxtwitter.common.util.UiUtils;


public class MyGifView extends GifView {
    public MyGifView(Context context) {
        this(context, null);
    }

    public MyGifView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyGifView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (UiUtils.isRunningTest()) {
            this.pause();
        }
    }
}
