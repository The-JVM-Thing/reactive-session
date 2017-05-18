package co.uk.thejvm.thing.rxtwitter.common.util;

public final class UiUtils {

    /**
     * This is the last resource to determine if code running under instrumentation tests.
     * If usual tricks like disabling animation by setting ANIMATION_SCALE doesn't work, use this check
     * and then do whatever you need to disable animation.
     *
     * @return true if espresso tests running otherwise false
     */
    public static boolean isRunningTest() {
        try {
            Class.forName("android.support.test.espresso.Espresso");
            return true;
        } catch (ClassNotFoundException e) {
            // Unable to find test class.
        }
        return false;
    }

    private UiUtils() {

    }
}
