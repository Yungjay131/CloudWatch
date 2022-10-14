package app.slyworks.cloudwatch.ui.auth_activity

import android.app.Activity
import android.content.Intent
import app.slyworks.cloudwatch.R


/**
 * Created by Joshua Sylvanus, 8:04 PM, 14/10/2022.
 */
class ThemeManager(private val preferenceManager: PreferenceManager) {
    fun toggleMode(a:Activity){
        val condition:Boolean = true
        if(condition)
            toggleDarkMode(a)
        else
            toggleLightMode(a)
        restartApp(a)
    }
    /* TODO: make activityComponent survive config changes */
    fun toggleLightMode(a:Activity){
        a.setTheme(R.style.AppTheme_Light)
        //save to preferences
    }
    fun toggleDarkMode(a:Activity){
        a.setTheme(R.style.AppTheme_Dark)
        //save to preferences
    }
    private fun restartApp(a: Activity){
        a.startActivity(Intent(a.applicationContext, a::class.java))
        a.finish()
    }
}