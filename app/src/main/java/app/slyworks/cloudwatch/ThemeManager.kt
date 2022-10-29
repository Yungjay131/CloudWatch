package app.slyworks.cloudwatch

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatDelegate
import app.slyworks.utils.KEY_IS_DARK_THEME


/**
 * Created by Joshua Sylvanus, 8:04 PM, 14/10/2022.
 */
class ThemeManager(private val preferenceManager: PreferenceManager) {
    fun toggleThemeMode(a:Activity){
        if(preferenceManager.get(KEY_IS_DARK_THEME, false))
            toggleDarkMode(a)
        else
            toggleLightMode(a)
        restartApp(a)
    }

    /* TODO: make activityComponent survive config changes */
    fun toggleLightMode(a:Activity){
        a.setTheme(R.style.AppTheme_Light)
        preferenceManager.set(KEY_IS_DARK_THEME, false)
        restartApp(a)
    }

    fun toggleDarkMode(a:Activity){
        a.setTheme(R.style.AppTheme_Dark)
        preferenceManager.set(KEY_IS_DARK_THEME, true)
        restartApp(a)
    }

    private fun restartApp(a: Activity){
        a.startActivity(Intent(a.applicationContext, a::class.java))
        a.finish()
    }

    /*
    *  private fun initDarkMode(){
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        /*2*/
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            //setTheme(R.style.AppTheme_Light)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            restartApp()
        }else
            setTheme(R.style.AppTheme_Dark)
    }
    * */
}