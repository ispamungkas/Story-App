package com.example.submissionaplikasistory.view.home

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.submissionaplikasistory.R
import com.example.submissionaplikasistory.utils.ExpressIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val activity = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun initiate() {
        IdlingRegistry.getInstance().register(ExpressIdlingResource.countingIdlingResource)
        Intents.init()
    }

    @After
    fun down() {
        IdlingRegistry.getInstance().unregister(ExpressIdlingResource.countingIdlingResource)
        Intents.release()
    }

    @Test
    fun logout_action() {
        onView(withId(R.id.action_logout)).check(matches(isDisplayed()))
        onView(withId(R.id.action_logout)).perform(click())
    }
}