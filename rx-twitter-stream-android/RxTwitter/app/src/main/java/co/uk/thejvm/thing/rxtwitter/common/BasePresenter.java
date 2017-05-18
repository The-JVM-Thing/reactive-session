package co.uk.thejvm.thing.rxtwitter.common;

public interface BasePresenter<V extends BaseView> {
    void setView(V view);
    void onPause();
}
