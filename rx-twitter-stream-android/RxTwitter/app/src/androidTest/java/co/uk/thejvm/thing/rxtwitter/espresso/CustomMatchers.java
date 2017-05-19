package co.uk.thejvm.thing.rxtwitter.espresso;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public final class CustomMatchers {

    public static Matcher<View> withRecyclerViewSize (final int size) {
        return new TypeSafeMatcher<View>() {
            @Override public boolean matchesSafely (final View view) {
                return ((RecyclerView) view).getAdapter().getItemCount() == size;
            }

            @Override public void describeTo (final Description description) {
                description.appendText ("RecyclerView should have " + size + " items");
            }
        };
    }

    private CustomMatchers() {

    }
}
