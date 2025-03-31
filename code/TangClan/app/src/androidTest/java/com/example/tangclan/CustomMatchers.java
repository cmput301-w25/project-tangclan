package com.example.tangclan;

import android.view.View;
import android.widget.ImageButton;

import androidx.test.espresso.matcher.BoundedMatcher;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class CustomMatchers {
    public static Matcher<View> withSelectedBackground() {
        return new BoundedMatcher<View, ImageButton>(ImageButton.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has selected background");
            }

            @Override
            protected boolean matchesSafely(ImageButton item) {
                return item.getBackground() != null &&
                        item.getBackground().getConstantState() != null &&
                        item.getBackground().getConstantState().equals(
                                item.getContext().getResources().getDrawable(R.drawable.selected_button_background).getConstantState());
            }
        };
    }

    public static Matcher<View> withUnselectedBackground() {
        return new BoundedMatcher<View, ImageButton>(ImageButton.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has no selected background");
            }

            @Override
            protected boolean matchesSafely(ImageButton item) {
                return item.getBackground() == null ||
                        !item.getBackground().getConstantState().equals(
                                item.getContext().getResources().getDrawable(R.drawable.selected_button_background).getConstantState());
            }
        };
    }
}