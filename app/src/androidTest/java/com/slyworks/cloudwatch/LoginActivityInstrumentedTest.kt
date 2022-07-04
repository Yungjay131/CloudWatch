package com.slyworks.cloudwatch

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.slyworks.models.IDTokenType
import com.slyworks.models.TempUser

import com.slyworks.cloudwatch.view.auth_activity.AuthActivity
import org.hamcrest.Matcher

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class LoginActivityInstrumentedTest {
    //region Vars
    @Rule
    @JvmField
    val activity = AuthActivity::class.java /*ActivityTestRule(AuthActivity::class.java)*/
    //endregion

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.slyworks.cloudwatch", appContext.packageName)
    }

    @Test
    fun test_toTestInstrumentedTesting(){
        Espresso.onView(withId(R.id.btnLogin))
            .perform(click())

       /* Espresso.onView(withId(R.id.))
            .perform(typeText("hello world"))*/

        val user: com.slyworks.models.TempUser = com.slyworks.models.TempUser("", "", "", "", "", idTokenType = com.slyworks.models.IDTokenType.EMAIL)
        val matcher_1:Matcher<com.slyworks.models.TempUser> = instanceOf(com.slyworks.models.TempUser::class.java)
        val matcher_2:Matcher<com.slyworks.models.TempUser> = equalTo(user)
        val matcher:Matcher<com.slyworks.models.TempUser> = allOf(matcher_1, matcher_2)
        Espresso.onData(matcher)
            .perform(click(), ViewActions.closeSoftKeyboard())

        Espresso.pressBack()
    }
}