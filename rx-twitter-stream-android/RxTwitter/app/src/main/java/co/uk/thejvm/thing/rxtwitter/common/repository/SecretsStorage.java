package co.uk.thejvm.thing.rxtwitter.common.repository;

public interface SecretsStorage {
    String getConsumerKey();
    String getConsumerSecret();
    String getToken();
    String getTokenSecret();
}
