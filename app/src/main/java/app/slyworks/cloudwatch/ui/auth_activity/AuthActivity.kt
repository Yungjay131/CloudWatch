package app.slyworks.cloudwatch.ui.auth_activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import app.slyworks.cloudwatch.R
import app.slyworks.cloudwatch.databinding.ActivityAuthBinding
import app.slyworks.cloudwatch.ui.MainActivity

class AuthActivity : AppCompatActivity() {
    //region Vars
    private lateinit var binding:ActivityAuthBinding
    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* TODO: add fullscreen code here from iGrades and extra from StackOverflow */
        /* TODO: add white/black gradient overlay asset, generated from adobeXD, inspiration pinterest*/
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun initDarkMode(){
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        /*2*/
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            //setTheme(R.style.AppTheme_Light)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            restartApp()
        }else
            setTheme(R.style.AppTheme_Dark)
    }

    private fun restartApp(){
        val intent = Intent(applicationContext,AuthActivity::class.java)
        startActivity(intent)
        finish()
    }
}