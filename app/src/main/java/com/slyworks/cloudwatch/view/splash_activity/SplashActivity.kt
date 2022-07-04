package com.slyworks.cloudwatch.view.splash_activity

import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.slyworks.cloudwatch.R
import com.slyworks.cloudwatch.databinding.ActivityOnBoardingBinding
import com.slyworks.cloudwatch.databinding.ActivitySplashBinding
import com.slyworks.cloudwatch.view.onboarding_activity.OnBoardingActivity
import kotlinx.coroutines.*

class SplashActivity : AppCompatActivity() {
    //region Vars
    private lateinit var binding:ActivitySplashBinding
    private lateinit var mHandler:Handler
    private lateinit var mJob:Job
    //endregion

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initFullScreen()
    }

    private fun initView(){
        setTheme(R.style.AppTheme_Theme)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun initFullScreen(){
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS )
    }

    override fun onResume() {
        super.onResume()
        mJob = CoroutineScope(Dispatchers.Main).launch {
            binding.progress.setMax(100)

            for(index in 1..4){
                binding.progress.setProgress(index * 5)
                delay(500)
            }

            _startActivity()
        }
    }

    override fun onStop() {
        super.onStop()
        mJob.cancel()
    }

    private fun _startActivity(){
        val intent: Intent = Intent(this, OnBoardingActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit)
    }
}