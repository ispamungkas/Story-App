package com.example.submissionaplikasistory.view

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.submissionaplikasistory.ConvertJson
import com.example.submissionaplikasistory.R
import com.example.submissionaplikasistory.datasource.api.ApiConfiguration
import com.example.submissionaplikasistory.utils.ExpressIdlingResource
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class LoginActivityTest {

    private val webMock = MockWebServer()
    private lateinit var myActivity : LoginActivity

    @get:Rule
    val activity = ActivityScenarioRule(LoginActivity::class.java)

    @Before
    fun initiate() {
        activity.scenario.onActivity {
            myActivity = it
        }
        webMock.start(8080)
        ApiConfiguration.api_key = "http://127.0.0.1:8080/"
        IdlingRegistry.getInstance().register(ExpressIdlingResource.countingIdlingResource)
    }

    @After
    fun down() {
        webMock.shutdown()
        IdlingRegistry.getInstance().unregister(ExpressIdlingResource.countingIdlingResource)
    }

    @Test
    fun login_action() {
        Intents.init()
        onView(withId(R.id.ed_login_email)).perform(ViewActions.typeText("maspam2@gmail.com"))
        onView(withId(R.id.ed_login_password)).perform(ViewActions.typeText("maspam123"))
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(ConvertJson.readStringFromFile("login_success_response.json"))
        webMock.enqueue(mockResponse)
        onView(withId(R.id.btn_Login)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_Login)).perform(click())
    }

}