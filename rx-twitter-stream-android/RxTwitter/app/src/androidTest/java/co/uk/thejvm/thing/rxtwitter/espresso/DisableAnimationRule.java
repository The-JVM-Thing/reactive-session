package co.uk.thejvm.thing.rxtwitter.espresso;

import android.os.IBinder;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.lang.reflect.Method;
import java.util.Arrays;

public class DisableAnimationRule implements TestRule {

    private Method setAnimationScalesMethod;
    private Method getAnimationScalesMethod;
    private Object windowManagerObject;

    public DisableAnimationRule() {
        try {
            Class<?> windowManagerStubClass = Class.forName("android.view.IWindowManager$Stub");
            Class<?> serviceManagerClass = Class.forName("android.os.ServiceManager");
            Class<?> windowManagerClass = Class.forName("android.view.IWindowManager");

            Method asInterface = windowManagerStubClass.getDeclaredMethod("asInterface", IBinder.class);
            Method getService = serviceManagerClass.getDeclaredMethod("getService", String.class);

            setAnimationScalesMethod = windowManagerClass.getDeclaredMethod("setAnimationScales", float[].class);
            getAnimationScalesMethod = windowManagerClass.getDeclaredMethod("getAnimationScales");

            IBinder windowManagerBinder = (IBinder) getService.invoke(null, "window");
            windowManagerObject = asInterface.invoke(null, windowManagerBinder);
        } catch (Exception e) {
            throw new FailedToAccessAnimationMethods();
        }
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                setAnimationScaleFactors(0.0f);
                try {
                    base.evaluate();
                } finally {
                    setAnimationScaleFactors(1.0f);
                }
            }
        };
    }

    private void setAnimationScaleFactors(float scaleFactor) throws Exception {
        float[] scaleFactors = (float[]) getAnimationScalesMethod.invoke(windowManagerObject);
        Arrays.fill(scaleFactors, scaleFactor);
        setAnimationScalesMethod.invoke(windowManagerObject, scaleFactors);
    }

    public static class FailedToAccessAnimationMethods extends RuntimeException {
    }
}