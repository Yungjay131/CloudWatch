package com.slyworks.authentication

import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class LoginManagerUnitTest {
    //region Vars
    private val mInput:String by lazy(LazyThreadSafetyMode.NONE){ "Joshua.123" }
    private lateinit var mInput2:String
    //endregion

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Before
    fun setup_text_entered_isValid_params(){
       mInput2 = mInput + "true that"
    }

    @Test
    fun text_entered_isValid(){
        //val input:String = "password.123"
        val input:String = mInput2
        val criteria_1:Boolean = input.length >= 8
        val criteria_2:Boolean = input.contains(".")

        assertTrue(criteria_1 && criteria_2)
    }
}