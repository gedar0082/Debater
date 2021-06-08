package com.gedar0082.debater.view


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.gedar0082.debater.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.*
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun mainActivityTest() {
        val appCompatButton = onView(
            allOf(
                withId(R.id.btn_login), withText("Login"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.fragment),
                        0
                    ),
                    8
                ),
                isDisplayed()
            )
        )
        appCompatButton.perform(click())

        pressBack()

        val appCompatButton2 = onView(
            allOf(
                withId(R.id.btn_login), withText("Login"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.fragment),
                        0
                    ),
                    8
                ),
                isDisplayed()
            )
        )
        appCompatButton2.perform(click())

        val linearLayout = onView(
            allOf(
                withId(R.id.list_item_layout),
                childAtPosition(
                    allOf(
                        withId(R.id.debate_card_view),
                        childAtPosition(
                            withId(R.id.one_debate),
                            0
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        linearLayout.perform(click())

        val linearLayout2 = onView(
            allOf(
                withId(R.id.list_item_layout),
                childAtPosition(
                    allOf(
                        withId(R.id.debate_card_view),
                        childAtPosition(
                            withId(R.id.one_debate),
                            0
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        linearLayout2.perform(click())

        val appCompatButton3 = onView(
            allOf(
                withId(R.id.debate_open_btn_enter), withText("Join"),
                childAtPosition(
                    allOf(
                        withId(R.id.debate_open),
                        childAtPosition(
                            withId(android.R.id.custom),
                            0
                        )
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        appCompatButton3.perform(click())

        val linearLayout3 = onData(anything())
            .inAdapterView(
                allOf(
                    withId(R.id.graph),
                    childAtPosition(
                        withClassName(`is`("com.otaliastudios.zoom.ZoomLayout")),
                        0
                    )
                )
            )
            .atPosition(0)
        linearLayout3.perform(longClick())

        val appCompatEditText = onView(
            allOf(
                withId(R.id.thesis_title_input),
                childAtPosition(
                    allOf(
                        withId(R.id.dialog_new_thesis_linear),
                        childAtPosition(
                            withId(R.id.dialog_new_thesis_scroll),
                            0
                        )
                    ),
                    2
                )
            )
        )
        appCompatEditText.perform(scrollTo(), replaceText("Пр"), closeSoftKeyboard())

        val appCompatEditText2 = onView(
            allOf(
                withId(R.id.thesis_short_input),
                childAtPosition(
                    allOf(
                        withId(R.id.dialog_new_thesis_linear),
                        childAtPosition(
                            withId(R.id.dialog_new_thesis_scroll),
                            0
                        )
                    ),
                    4
                )
            )
        )
        appCompatEditText2.perform(scrollTo(), replaceText("Пр"), closeSoftKeyboard())

        val appCompatEditText3 = onView(
            allOf(
                withId(R.id.thesis_statement_input),
                childAtPosition(
                    allOf(
                        withId(R.id.dialog_new_thesis_linear),
                        childAtPosition(
                            withId(R.id.dialog_new_thesis_scroll),
                            0
                        )
                    ),
                    6
                )
            )
        )
        appCompatEditText3.perform(scrollTo(), replaceText("Ар"), closeSoftKeyboard())

        pressBack()

        val textView = onView(
            allOf(
                withId(R.id.thesis_title_text), withText("Thesis title"),
                withParent(
                    allOf(
                        withId(R.id.dialog_new_thesis_linear),
                        withParent(withId(R.id.dialog_new_thesis_scroll))
                    )
                ),
                isDisplayed()
            )
        )
        textView.check(matches(isDisplayed()))

        val textView2 = onView(
            allOf(
                withId(R.id.thesis_title_text), withText("Thesis title"),
                withParent(
                    allOf(
                        withId(R.id.dialog_new_thesis_linear),
                        withParent(withId(R.id.dialog_new_thesis_scroll))
                    )
                ),
                isDisplayed()
            )
        )
        textView2.check(matches(isDisplayed()))

        val appCompatButton4 = onView(
            allOf(
                withId(R.id.btn_answer1), withText("Write argument"),
                childAtPosition(
                    allOf(
                        withId(R.id.dialog_new_thesis_linear),
                        childAtPosition(
                            withId(R.id.dialog_new_thesis_scroll),
                            0
                        )
                    ),
                    10
                )
            )
        )
        appCompatButton4.perform(scrollTo(), click())

        val linearLayout4 = onData(anything())
            .inAdapterView(
                allOf(
                    withId(R.id.argument_map_graph),
                    childAtPosition(
                        withClassName(`is`("com.otaliastudios.zoom.ZoomLayout")),
                        0
                    )
                )
            )
            .atPosition(0)
        linearLayout4.perform(click())

        val textView3 = onView(
            allOf(
                withId(R.id.Dialog_text), withText("Create New"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
                isDisplayed()
            )
        )
        textView3.check(matches(isDisplayed()))

        val linearLayout5 = onData(anything())
            .inAdapterView(
                allOf(
                    withId(R.id.argument_map_graph),
                    childAtPosition(
                        withClassName(`is`("com.otaliastudios.zoom.ZoomLayout")),
                        0
                    )
                )
            )
            .atPosition(0)
        linearLayout5.perform(click())

        val appCompatEditText4 = onView(
            allOf(
                withId(R.id.argument_title_input),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    3
                )
            )
        )
        appCompatEditText4.perform(scrollTo(), replaceText("Тч"), closeSoftKeyboard())

        val appCompatEditText5 = onView(
            allOf(
                withId(R.id.argument_statement_input),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    5
                )
            )
        )
        appCompatEditText5.perform(scrollTo(), replaceText("Сб"), closeSoftKeyboard())

        val appCompatButton5 = onView(
            allOf(
                withId(android.R.id.button1), withText("Create"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    3
                )
            )
        )
        appCompatButton5.perform(scrollTo(), click())

        val textView4 = onView(
            allOf(
                withId(R.id.Dialog_text), withText("Create new"),
                withParent(
                    allOf(
                        withId(R.id.dialog_new_thesis_linear),
                        withParent(withId(R.id.dialog_new_thesis_scroll))
                    )
                ),
                isDisplayed()
            )
        )
        textView4.check(matches(isDisplayed()))

        val appCompatButton6 = onView(
            allOf(
                withId(android.R.id.button1), withText("Create"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    3
                )
            )
        )
        appCompatButton6.perform(scrollTo(), click())
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
