package co.uk.thejvm.thing.rxtwitter.common.di;

import co.uk.thejvm.thing.rxtwitter.BaseActivity;
import co.uk.thejvm.thing.rxtwitter.common.BackPressureStrategy;

public class ModuleBootstrapper {

    public ActivityModule getNewActivityModule(BaseActivity baseActivity, BackPressureStrategy backPressureStrategy) {
        return new ActivityModule(baseActivity, backPressureStrategy);
    }
}
