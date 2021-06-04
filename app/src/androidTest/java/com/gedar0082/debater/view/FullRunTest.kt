package com.gedar0082.debater.view


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.gedar0082.debater.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.*
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class FullRunTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun fullRunTest() {
        val appCompatEditText = onView(
                allOf(
                        withId(R.id.editTextTextPersonName),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.fragment),
                                        0
                                ),
                                6
                        ),
                        isDisplayed()
                )
        )
        appCompatEditText.perform(replaceText("gedar"), closeSoftKeyboard())

        val appCompatEditText2 = onView(
                allOf(
                        withId(R.id.editTextTextEmailAddress),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.fragment),
                                        0
                                ),
                                1
                        ),
                        isDisplayed()
                )
        )
        appCompatEditText2.perform(replaceText("gedar0082"), closeSoftKeyboard())

        val appCompatEditText3 = onView(
                allOf(
                        withId(R.id.editTextTextEmailAddress), withText("gedar0082"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.fragment),
                                        0
                                ),
                                1
                        ),
                        isDisplayed()
                )
        )
        appCompatEditText3.perform(pressImeActionButton())

        val appCompatEditText4 = onView(
                allOf(
                        withId(R.id.editTextTextPassword),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.fragment),
                                        0
                                ),
                                3
                        ),
                        isDisplayed()
                )
        )
        appCompatEditText4.perform(replaceText("12345"), closeSoftKeyboard())

        val appCompatEditText5 = onView(
                allOf(
                        withId(R.id.editTextTextPassword), withText("12345"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.fragment),
                                        0
                                ),
                                3
                        ),
                        isDisplayed()
                )
        )
        appCompatEditText5.perform(pressImeActionButton())

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

        val appCompatButton2 = onView(
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
        appCompatButton2.perform(click())

        val linearLayout2 = onData(anything())
            .inAdapterView(
                    allOf(
                            withId(R.id.graph),
                            childAtPosition(
                                    withClassName(`is`("com.otaliastudios.zoom.ZoomLayout")),
                                    0
                            )
                    )
            )
            .atPosition(2)
        linearLayout2.perform(click())

        val appCompatButton3 = onView(
                allOf(
                        withId(R.id.btn_answer), withText("Give a response"),
                        childAtPosition(
                                allOf(
                                        withId(R.id.thesis_open),
                                        childAtPosition(
                                                withClassName(`is`("android.widget.ScrollView")),
                                                0
                                        )
                                ),
                                6
                        )
                )
        )
        appCompatButton3.perform(scrollTo(), click())

        val appCompatEditText6 = onView(
                allOf(
                        withId(R.id.thesis_intro_input),
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
        appCompatEditText6.perform(scrollTo(), replaceText("F"), closeSoftKeyboard())

        val appCompatEditText7 = onView(
                allOf(
                        withId(R.id.thesis_definition_input),
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
        appCompatEditText7.perform(scrollTo(), replaceText("Ghk"), closeSoftKeyboard())

        val appCompatEditText8 = onView(
                allOf(
                        withId(R.id.thesis_problem_input),
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
        appCompatEditText8.perform(scrollTo(), replaceText("Zfg"), closeSoftKeyboard())

        val appCompatEditText9 = onView(
                allOf(
                        withId(R.id.thesis_plan_input),
                        childAtPosition(
                                allOf(
                                        withId(R.id.dialog_new_thesis_linear),
                                        childAtPosition(
                                                withId(R.id.dialog_new_thesis_scroll),
                                                0
                                        )
                                ),
                                8
                        )
                )
        )
        appCompatEditText9.perform(scrollTo(), replaceText("Sfh\nFgg\n"), closeSoftKeyboard())

        pressBack()

        val appCompatEditText10 = onView(
                allOf(
                        withId(R.id.thesis_case_intro_input),
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
        appCompatEditText10.perform(scrollTo(), replaceText("Cvn"), closeSoftKeyboard())

        pressBack()

        val appCompatEditText11 = onView(
                allOf(
                        withId(R.id.thesis_case_description_input),
                        childAtPosition(
                                allOf(
                                        withId(R.id.dialog_new_thesis_linear),
                                        childAtPosition(
                                                withId(R.id.dialog_new_thesis_scroll),
                                                0
                                        )
                                ),
                                12
                        )
                )
        )
        appCompatEditText11.perform(scrollTo(), replaceText("Vnl"), closeSoftKeyboard())

        pressBack()

        val appCompatButton4 = onView(
                allOf(
                        withId(R.id.btn_answer1), withText("Response to first argument"),
                        childAtPosition(
                                allOf(
                                        withId(R.id.dialog_new_thesis_linear),
                                        childAtPosition(
                                                withId(R.id.dialog_new_thesis_scroll),
                                                0
                                        )
                                ),
                                16
                        )
                )
        )
        appCompatButton4.perform(scrollTo(), click())

        val linearLayout3 = onData(anything())
            .inAdapterView(
                    allOf(
                            withId(R.id.argument_map_graph),
                            childAtPosition(
                                    withClassName(`is`("com.otaliastudios.zoom.ZoomLayout")),
                                    0
                            )
                    )
            )
            .atPosition(4)
        linearLayout3.perform(click())

        val appCompatEditText12 = onView(
                allOf(
                        withId(R.id.argument_statement_input),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("android.widget.ScrollView")),
                                        0
                                ),
                                3
                        )
                )
        )
        appCompatEditText12.perform(scrollTo(), replaceText("N"), closeSoftKeyboard())

        val appCompatEditText13 = onView(
                allOf(
                        withId(R.id.argument_clarification_input),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("android.widget.ScrollView")),
                                        0
                                ),
                                5
                        )
                )
        )
        appCompatEditText13.perform(scrollTo(), replaceText("L"), closeSoftKeyboard())

        val appCompatEditText14 = onView(
                allOf(
                        withId(R.id.argument_evidence_input),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("android.widget.ScrollView")),
                                        0
                                ),
                                7
                        )
                )
        )
        appCompatEditText14.perform(scrollTo(), replaceText("Z"), closeSoftKeyboard())

        val appCompatEditText15 = onView(
                allOf(
                        withId(R.id.argument_summary_input),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("android.widget.ScrollView")),
                                        0
                                ),
                                9
                        )
                )
        )
        appCompatEditText15.perform(scrollTo(), replaceText("G"), closeSoftKeyboard())

        pressBack()

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

        val appCompatButton6 = onView(
                allOf(
                        withId(R.id.btn_answer1), withText("Response to first argument"),
                        childAtPosition(
                                allOf(
                                        withId(R.id.dialog_new_thesis_linear),
                                        childAtPosition(
                                                withId(R.id.dialog_new_thesis_scroll),
                                                0
                                        )
                                ),
                                16
                        )
                )
        )
        appCompatButton6.perform(scrollTo(), click())

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
            .atPosition(5)
        linearLayout4.perform(click())

        val appCompatEditText16 = onView(
                allOf(
                        withId(R.id.argument_statement_input),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("android.widget.ScrollView")),
                                        0
                                ),
                                3
                        )
                )
        )
        appCompatEditText16.perform(scrollTo(), replaceText("V"), closeSoftKeyboard())

        val appCompatEditText17 = onView(
                allOf(
                        withId(R.id.argument_clarification_input),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("android.widget.ScrollView")),
                                        0
                                ),
                                5
                        )
                )
        )
        appCompatEditText17.perform(scrollTo(), replaceText("K"), closeSoftKeyboard())

        val appCompatEditText18 = onView(
                allOf(
                        withId(R.id.argument_evidence_input),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("android.widget.ScrollView")),
                                        0
                                ),
                                7
                        )
                )
        )
        appCompatEditText18.perform(scrollTo(), replaceText("Z"), closeSoftKeyboard())

        val appCompatEditText19 = onView(
                allOf(
                        withId(R.id.argument_summary_input),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("android.widget.ScrollView")),
                                        0
                                ),
                                9
                        )
                )
        )
        appCompatEditText19.perform(scrollTo(), replaceText("P"), closeSoftKeyboard())

        pressBack()

        val appCompatButton7 = onView(
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
        appCompatButton7.perform(scrollTo(), click())

        val appCompatButton8 = onView(
                allOf(
                        withId(R.id.btn_answer1), withText("Response to first argument"),
                        childAtPosition(
                                allOf(
                                        withId(R.id.dialog_new_thesis_linear),
                                        childAtPosition(
                                                withId(R.id.dialog_new_thesis_scroll),
                                                0
                                        )
                                ),
                                16
                        )
                )
        )
        appCompatButton8.perform(scrollTo(), click())

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
            .atPosition(6)
        linearLayout5.perform(click())

        val appCompatEditText20 = onView(
                allOf(
                        withId(R.id.argument_statement_input),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("android.widget.ScrollView")),
                                        0
                                ),
                                3
                        )
                )
        )
        appCompatEditText20.perform(scrollTo(), replaceText("J"), closeSoftKeyboard())

        val appCompatEditText21 = onView(
                allOf(
                        withId(R.id.argument_clarification_input),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("android.widget.ScrollView")),
                                        0
                                ),
                                5
                        )
                )
        )
        appCompatEditText21.perform(scrollTo(), replaceText("D"), closeSoftKeyboard())

        val appCompatEditText22 = onView(
                allOf(
                        withId(R.id.argument_evidence_input),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("android.widget.ScrollView")),
                                        0
                                ),
                                7
                        )
                )
        )
        appCompatEditText22.perform(scrollTo(), replaceText("P"), closeSoftKeyboard())

        val appCompatEditText23 = onView(
                allOf(
                        withId(R.id.argument_summary_input),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("android.widget.ScrollView")),
                                        0
                                ),
                                9
                        )
                )
        )
        appCompatEditText23.perform(scrollTo(), replaceText("C"), closeSoftKeyboard())

        pressBack()

        val appCompatButton9 = onView(
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
        appCompatButton9.perform(scrollTo(), click())

        val appCompatButton10 = onView(
                allOf(
                        withId(R.id.btn_answer1), withText("Response to first argument"),
                        childAtPosition(
                                allOf(
                                        withId(R.id.dialog_new_thesis_linear),
                                        childAtPosition(
                                                withId(R.id.dialog_new_thesis_scroll),
                                                0
                                        )
                                ),
                                16
                        )
                )
        )
        appCompatButton10.perform(scrollTo(), click())

        val linearLayout6 = onData(anything())
            .inAdapterView(
                    allOf(
                            withId(R.id.argument_map_graph),
                            childAtPosition(
                                    withClassName(`is`("com.otaliastudios.zoom.ZoomLayout")),
                                    0
                            )
                    )
            )
            .atPosition(7)
        linearLayout6.perform(click())

        val appCompatEditText24 = onView(
                allOf(
                        withId(R.id.argument_statement_input),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("android.widget.ScrollView")),
                                        0
                                ),
                                3
                        )
                )
        )
        appCompatEditText24.perform(scrollTo(), replaceText("B"), closeSoftKeyboard())

        val appCompatEditText25 = onView(
                allOf(
                        withId(R.id.argument_clarification_input),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("android.widget.ScrollView")),
                                        0
                                ),
                                5
                        )
                )
        )
        appCompatEditText25.perform(scrollTo(), replaceText("L"), closeSoftKeyboard())

        val appCompatEditText26 = onView(
                allOf(
                        withId(R.id.argument_evidence_input),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("android.widget.ScrollView")),
                                        0
                                ),
                                7
                        )
                )
        )
        appCompatEditText26.perform(scrollTo(), replaceText("Z"), closeSoftKeyboard())

        val appCompatEditText27 = onView(
                allOf(
                        withId(R.id.argument_summary_input),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("android.widget.ScrollView")),
                                        0
                                ),
                                9
                        )
                )
        )
        appCompatEditText27.perform(scrollTo(), replaceText("M"), closeSoftKeyboard())

        pressBack()

        val appCompatButton11 = onView(
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
        appCompatButton11.perform(scrollTo(), click())

        val appCompatButton12 = onView(
                allOf(
                        withId(R.id.btn_answer1), withText("Response to first argument"),
                        childAtPosition(
                                allOf(
                                        withId(R.id.dialog_new_thesis_linear),
                                        childAtPosition(
                                                withId(R.id.dialog_new_thesis_scroll),
                                                0
                                        )
                                ),
                                16
                        )
                )
        )
        appCompatButton12.perform(scrollTo(), click())

        val linearLayout7 = onData(anything())
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
        linearLayout7.perform(click())

        val appCompatEditText28 = onView(
                allOf(
                        withId(R.id.argument_statement_input),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("android.widget.ScrollView")),
                                        0
                                ),
                                3
                        )
                )
        )
        appCompatEditText28.perform(scrollTo(), replaceText("V"), closeSoftKeyboard())

        val appCompatEditText29 = onView(
                allOf(
                        withId(R.id.argument_clarification_input),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("android.widget.ScrollView")),
                                        0
                                ),
                                5
                        )
                )
        )
        appCompatEditText29.perform(scrollTo(), replaceText("M"), closeSoftKeyboard())

        val appCompatEditText30 = onView(
                allOf(
                        withId(R.id.argument_evidence_input),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("android.widget.ScrollView")),
                                        0
                                ),
                                7
                        )
                )
        )
        appCompatEditText30.perform(scrollTo(), replaceText("Z"), closeSoftKeyboard())

        val appCompatEditText31 = onView(
                allOf(
                        withId(R.id.argument_summary_input),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("android.widget.ScrollView")),
                                        0
                                ),
                                9
                        )
                )
        )
        appCompatEditText31.perform(scrollTo(), replaceText("N"), closeSoftKeyboard())

        pressBack()

        val appCompatButton13 = onView(
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
        appCompatButton13.perform(scrollTo(), click())

        val appCompatButton14 = onView(
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
        appCompatButton14.perform(scrollTo(), click())

        val linearLayout8 = onData(anything())
            .inAdapterView(
                    allOf(
                            withId(R.id.graph),
                            childAtPosition(
                                    withClassName(`is`("com.otaliastudios.zoom.ZoomLayout")),
                                    0
                            )
                    )
            )
            .atPosition(3)
        linearLayout8.perform(click())

        val linearLayout9 = onData(anything())
            .inAdapterView(
                    allOf(
                            withId(R.id.argument_map_graph),
                            childAtPosition(
                                    withClassName(`is`("com.otaliastudios.zoom.ZoomLayout")),
                                    0
                            )
                    )
            )
            .atPosition(11)
        linearLayout9.perform(click())
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
