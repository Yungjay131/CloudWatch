package app.slyworks.ui_commons_lib

import android.app.Activity
import android.content.Intent
import androidx.annotation.IdRes
import app.slyworks.utils.KEY_IS_DARK_THEME
import app.slyworks.utils.PreferenceManager


/**
 * Created by Joshua Sylvanus, 8:04 PM, 14/10/2022.
 */
class ThemeManager(private val preferenceManager: PreferenceManager) {
    private val DEFAULT_THEME:Int
    get(){
        if(preferenceManager.get(KEY_IS_DARK_THEME, false))
            return R.style.AppTheme_Light
        else
            return R.style.AppTheme_Dark
    }

    fun resetTheme(a:Activity, @IdRes newTheme:Int? = null):Unit = a.setTheme(DEFAULT_THEME)

    fun toggleThemeMode(a:Activity){
        if(preferenceManager.get(KEY_IS_DARK_THEME, false))
            turnOnDarkMode(a)
        else
            turnOnLightMode(a)
        restartApp(a)
    }

    /* TODO: make activityComponent survive config changes */
    fun turnOnLightMode(a:Activity){
        a.setTheme(R.style.AppTheme_Light)
        preferenceManager.set(KEY_IS_DARK_THEME, false)
        restartApp(a)
    }

    fun turnOnDarkMode(a:Activity){
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