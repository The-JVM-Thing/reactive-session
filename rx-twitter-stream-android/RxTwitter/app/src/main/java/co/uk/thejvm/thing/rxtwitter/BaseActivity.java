package co.uk.thejvm.thing.rxtwitter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import co.uk.thejvm.thing.rxtwitter.common.BackPressureStrategy;
import co.uk.thejvm.thing.rxtwitter.common.di.ActivityComponent;
import co.uk.thejvm.thing.rxtwitter.common.di.ApplicationComponent;
import co.uk.thejvm.thing.rxtwitter.common.di.DaggerActivityComponent;

public abstract class BaseActivity extends AppCompatActivity {

    /**
     * The constant BACKPRESSURE_STRATEGY_EXTRA_KEY.
     */
    public static final String BACKPRESSURE_STRATEGY_EXTRA_KEY = "backpressure_strategy_option";

    private ActivityComponent activityComponent;
    private BackPressureStrategy backPressureStrategy = BackPressureStrategy.NO_STRATEGY;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent.hasExtra(BACKPRESSURE_STRATEGY_EXTRA_KEY)) {
            backPressureStrategy = (BackPressureStrategy) intent.getSerializableExtra(BACKPRESSURE_STRATEGY_EXTRA_KEY);
        }

        inject();
    }

    private void inject() {
        ApplicationComponent applicationComponent = ((RxTwitterApplication) getApplicationContext()).getApplicationComponent();
        activityComponent = DaggerActivityComponent.builder()
                .applicationComponent(applicationComponent)
                .activityModule(applicationComponent.getModuleBootstrapper().getNewActivityModule(this, backPressureStrategy))
                .build();

        activityComponent.inject(this);
        setUpDependencies();
    }

    protected ActivityComponent getActivityComponent() {
        return activityComponent;
    }

    protected abstract void setUpDependencies();
}
