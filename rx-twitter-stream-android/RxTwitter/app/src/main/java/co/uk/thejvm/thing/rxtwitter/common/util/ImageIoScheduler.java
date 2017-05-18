package co.uk.thejvm.thing.rxtwitter.common.util;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

public class ImageIoScheduler implements ExecutionScheduler {
    @Override
    public Scheduler getScheduler() {
        return Schedulers.computation();
    }
}
