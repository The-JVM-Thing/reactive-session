package co.uk.thejvm.thing.rxtwitter.common.di;

import co.uk.thejvm.thing.rxtwitter.BaseActivity;
import co.uk.thejvm.thing.rxtwitter.stream.StreamActivity;
import dagger.Component;

@ActivityScope
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {
    void inject(BaseActivity baseActivity);
    void inject(StreamActivity streamActivity);
}
