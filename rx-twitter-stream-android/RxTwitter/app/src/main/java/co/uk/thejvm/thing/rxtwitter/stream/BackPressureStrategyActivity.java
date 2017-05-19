package co.uk.thejvm.thing.rxtwitter.stream;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.collect.Lists;

import java.util.List;

import co.uk.thejvm.thing.rxtwitter.BaseActivity;
import co.uk.thejvm.thing.rxtwitter.R;
import co.uk.thejvm.thing.rxtwitter.common.BackPressureStrategy;

public class BackPressureStrategyActivity extends BaseActivity {

    private Toolbar toolbar;
    private RecyclerView backPressureOptions;
    private BackPressureStrategyOptionsAdapter adapter = new BackPressureStrategyOptionsAdapter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backpressure);

        toolbar = (Toolbar) findViewById(R.id.rx_twitter_toolbar);
        setSupportActionBar(toolbar);

        toolbar.setTitleTextColor(Color.WHITE);

        backPressureOptions = (RecyclerView) findViewById(R.id.backpressure_strategy_options);
        backPressureOptions.setAdapter(adapter);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        backPressureOptions.setLayoutManager(mLayoutManager);

    }

    @Override
    protected void setUpDependencies() {

    }

    private class BackPressureStrategyOptionsAdapter extends RecyclerView.Adapter<BackPressureStrategyOptionView> {

        private List<BackPressureStrategyOption> options = Lists.newArrayList(
                new BackPressureStrategyOption("No Strategy", BackPressureStrategy.NO_STRATEGY),
                new BackPressureStrategyOption("Buffer", BackPressureStrategy.BUFFER),
                new BackPressureStrategyOption("Drop", BackPressureStrategy.DROP),
                new BackPressureStrategyOption("Latest", BackPressureStrategy.LATEST)
        );

        @Override
        public BackPressureStrategyOptionView onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_backpressure_option, parent, false);
            return new BackPressureStrategyOptionView(itemView);
        }

        @Override
        public void onBindViewHolder(BackPressureStrategyOptionView holder, int position) {
            holder.setOption(options.get(position));
        }

        @Override
        public int getItemCount() {
            return options.size();
        }
    }

    private class BackPressureStrategyOptionView extends RecyclerView.ViewHolder {

        TextView optionLabel;

        public BackPressureStrategyOptionView(View itemView) {
            super(itemView);
            optionLabel = (TextView) itemView.findViewById(R.id.option_label);
        }

        void setOption(BackPressureStrategyOption option) {
            optionLabel.setText(option.label);
            itemView.setOnClickListener(view -> {
                Intent intent = StreamActivity.createIntent(itemView.getContext(), option.strategy);
                startActivity(intent);
            });
        }
    }

    private class BackPressureStrategyOption {
        String label;
        BackPressureStrategy strategy;

        public BackPressureStrategyOption(String label, BackPressureStrategy strategy) {
            this.label = label;
            this.strategy = strategy;
        }
    }
}
