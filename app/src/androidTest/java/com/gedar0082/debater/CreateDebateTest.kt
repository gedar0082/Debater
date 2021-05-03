package com.gedar0082.debater

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import org.junit.Rule
import org.junit.Test
import androidx.test.rule.ActivityTestRule
import com.gedar0082.debater.view.MainActivity


class CreateDebateTest {

    @get:Rule
    var mActivityTestRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    @Test
    fun createDebateTest(){
        onView(withId(R.id.btn_login)).perform(click())
    }
}