package co.uk.thejvm.thing.rxtwitter.common.util;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class UIScheduler implements ExecutionScheduler {
    @Override
    public Scheduler getScheduler() {
        return AndroidSchedulers.mainThread();
    }
}
