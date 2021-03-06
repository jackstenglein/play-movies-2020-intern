package com.google.moviestvsentiments.usecase.signin;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static com.google.moviestvsentiments.assertions.RecyclerViewItemCountAssertion.withItemCount;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

import android.app.Activity;
import android.app.Instrumentation.ActivityResult;
import android.content.Intent;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;
import com.google.moviestvsentiments.R;
import com.google.moviestvsentiments.ToastApplication;
import com.google.moviestvsentiments.di.DatabaseModule;
import com.google.moviestvsentiments.di.WebModule;
import com.google.moviestvsentiments.usecase.addAccount.AddAccountActivity;
import com.google.moviestvsentiments.usecase.navigation.SentimentsNavigationActivity;

@UninstallModules({DatabaseModule.class, WebModule.class})
@HiltAndroidTest
public class SigninActivityTest {

    IntentsTestRule<SigninActivity> intentsRule = new IntentsTestRule<>(SigninActivity.class, false,
            false);

    @Rule
    public RuleChain rule = RuleChain.outerRule(new HiltAndroidRule(this))
            .around(intentsRule)
            .around(new InstantTaskExecutorRule());

    @Test
    public void serverError_displaysToast() {
        ((ToastApplication)getInstrumentation().getTargetContext().getApplicationContext())
                .resetToastDisplayed();
        intentsRule.launchActivity(null);

        onView(withText(R.string.offlineToast)).inRoot(withDecorView(not(intentsRule.getActivity()
                .getWindow().getDecorView()))).check(matches(isDisplayed()));
    }

    @Test
    public void signinActivity_displaysOnlyAddAccount() {
        intentsRule.launchActivity(null);

        onView(withId(R.id.accountList)).check(withItemCount(1));
        onView(withId(R.id.accountTextView)).check(matches(withText("Add Account")));
    }

    @Test
    public void addAccount_displaysCorrectIcon() {
        intentsRule.launchActivity(null);

        onView(withId(R.id.accountIcon)).check(matches(withTagValue(equalTo(
                R.drawable.ic_baseline_person_add_24))));
    }

    @Test
    public void addAccount_hidesIconTextView() {
        intentsRule.launchActivity(null);

        onView(withId(R.id.accountIconText)).check(matches(withEffectiveVisibility(
                ViewMatchers.Visibility.GONE)));
    }

    @Test
    public void signinActivity_clickAddAccount_sendsIntentToAddAccountActivity() {
        intentsRule.launchActivity(null);

        onView(withId(R.id.accountTextView)).perform(click());

        intended(hasComponent(AddAccountActivity.class.getName()));
    }

    @Test
    public void signinActivity_addAccountResultCanceled_displaysOnlyAddAccount() {
        intentsRule.launchActivity(null);
        ActivityResult result = new ActivityResult(Activity.RESULT_CANCELED, new Intent());
        intending(hasComponent(AddAccountActivity.class.getName())).respondWith(result);

        onView(withId(R.id.accountTextView)).perform(click());

        onView(withId(R.id.accountList)).check(withItemCount(1));
        onView(withId(R.id.accountTextView)).check(matches(withText("Add Account")));
    }

    @Test
    public void signinActivity_addAccountResultOk_displaysNewAccount() {
        intentsRule.launchActivity(null);
        Intent intent = new Intent();
        intent.putExtra(AddAccountActivity.EXTRA_ACCOUNT_NAME, "Account Name");
        ActivityResult result = new ActivityResult(Activity.RESULT_OK, intent);
        intending(hasComponent(AddAccountActivity.class.getName())).respondWith(result);

        onView(withId(R.id.accountTextView)).perform(click());

        onView(withId(R.id.accountList)).check(withItemCount(2));
        onView(allOf(withId(R.id.accountIconText), withEffectiveVisibility(
                ViewMatchers.Visibility.VISIBLE))).check(matches(withText("A")));
    }

    @Test
    public void signinActivity_clickAccountName_sendsIntentToSentimentsNavigationActivity() {
        intentsRule.launchActivity(null);
        Intent intent = new Intent();
        intent.putExtra(AddAccountActivity.EXTRA_ACCOUNT_NAME, "Account Name");
        ActivityResult result = new ActivityResult(Activity.RESULT_OK, intent);
        intending(hasComponent(AddAccountActivity.class.getName())).respondWith(result);
        onView(withId(R.id.accountTextView)).perform(click());

        onView(allOf(withId(R.id.accountTextView), withText("Account Name"))).perform(click());

        intended(allOf(hasComponent(SentimentsNavigationActivity.class.getName()),
                hasExtra(SigninActivity.EXTRA_ACCOUNT_NAME, "Account Name")));
    }
}
