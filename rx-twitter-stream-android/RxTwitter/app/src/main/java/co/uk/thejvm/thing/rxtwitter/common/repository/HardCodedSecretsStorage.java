package co.uk.thejvm.thing.rxtwitter.common.repository;

/**
 * All secrects are provided on Twitter developer project so, copy and paste them here.
 */
public class HardCodedSecretsStorage implements SecretsStorage {

    @Override
    public String getConsumerKey() {
        return "xBz7PXn20RfaBFBG46Gyjg";
    }

    @Override
    public String getConsumerSecret() {
        return "HdIEcbFcTH88BJmCHKWvjo8EONcAl119aOkyHyNupE4";
    }

    @Override
    public String getToken() {
        return "410008204-U73IZZ707ScSaLB04ZiFO3R5cGJFvg3widi2owdW";
    }

    @Override
    public String getTokenSecret() {
        return "WNIetyKFX9ufNb8qftijLsEX8PLRwcxfX51m8WC8CSCkc";
    }
}
