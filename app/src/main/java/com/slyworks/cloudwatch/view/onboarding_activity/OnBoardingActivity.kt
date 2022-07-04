package com.slyworks.cloudwatch.view.onboarding_activity

import android.animation.AnimatorInflater
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
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.slyworks.cloudwatch.R
import com.slyworks.cloudwatch.databinding.ActivityOnBoardingBinding
import com.slyworks.cloudwatch.view.auth_activity.AuthActivity
import kotlinx.coroutines.*
import okhttp3.Dispatcher
import java.util.concurrent.atomic.AtomicInteger

private val mHandler: Handler
    get() {
        return Handler(Looper.myLooper()!!)
    }

class OnBoardingActivity : AppCompatActivity() {
    //region Vars
    private lateinit var binding:ActivityOnBoardingBinding

    private lateinit var vpAdapter:VPAdapter_OnBoarding
    private lateinit var mJob: Job

    private var mIndex:AtomicInteger = AtomicInteger(0)
    private val mTextMap:List<String> = mutableListOf(
        getString(R.string.onboarding_text_1),
        getString(R.string.onboarding_text_2),
        getString(R.string.onboarding_text_3),
        getString(R.string.onboarding_text_4))
    //endregion

    companion object{
        private const val FORWARD = 1_000;
        private const val BACKWARD = 2_000;

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
        initFullScreen()
        init()
    }

    private fun initSplashScreen(){
        installSplashScreen().setOnExitAnimationListener{
            val alphaAnimation = ObjectAnimator.ofFloat(
                it.view,
                "alpha",
                1f,
                0f)

            alphaAnimation.duration = 500
            alphaAnimation.doOnEnd { it2 -> it.remove() }
            alphaAnimation.start()
        };
    }

    private fun initView(){
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
    private fun initFullScreen(){
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS )
    }


    private fun init(){
        binding.btnNextOnboarding.setOnClickListener {
            mJob.cancel()
            goToFragment(FORWARD)
            initJob()
        }

        binding.btnPreviousOnboarding.setOnClickListener {
            mJob.cancel()
            goToFragment(BACKWARD)
            initJob()
        }

        binding.btnGetStartedOnboarding.setOnClickListener { _startActivity() }

        vpAdapter = VPAdapter_OnBoarding(supportFragmentManager, this.lifecycle)
        binding.vpMainOnboarding.adapter = vpAdapter
        binding.vpMainOnboarding.registerOnPageChangeCallback(
            object:ViewPager2.OnPageChangeCallback(){
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }

            override fun onPageSelected(position: Int) {
                mIndex.set(position)

                binding.tvText.text = mTextMap[position]
                val textAnimation = AnimatorInflater.loadAnimator(this@OnBoardingActivity, R.animator.text_animator) as ObjectAnimator
                textAnimation.target = binding.tvText
                textAnimation.start()

                toggleButtonsVisibility()
            }
        })

        TabLayoutMediator(binding.tabLayoutOnboarding, binding.vpMainOnboarding,
            object:TabLayoutMediator.TabConfigurationStrategy{
                override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {}
            }).attach()

    }

    override fun onResume() {
        super.onResume()

        initJob()
    }

    override fun onStop() {
        super.onStop()
        mHandler.removeCallbacksAndMessages(null)
    }

    private tailrec suspend fun loopThroughOnBoardingScreens(){
        if(mIndex.get() < 3)
            mIndex.getAndIncrement()
        else
            mIndex.set(0)

        delay(10_000)
        binding.vpMainOnboarding.currentItem = mIndex.get()

        loopThroughOnBoardingScreens()
    }

    private fun loopThroughOnBoardingScreens2(){
        if(mIndex.get() < 3)
            mIndex.getAndIncrement()
        else
            mIndex.set(0)

        binding.vpMainOnboarding.currentItem = mIndex.get()
    }

    private fun goToFragment(direction:Int){
        when(direction){
            FORWARD ->{
                if(mIndex.get() == 3) return
                mIndex.getAndIncrement()
            }
            BACKWARD ->{
                if(mIndex.get() == 0) return
                mIndex.getAndDecrement()
            }
        }

        binding.vpMainOnboarding.setCurrentItem(mIndex.get(), true)
    }

    private fun initJob(){
        mJob = CoroutineScope(Dispatchers.Main).launch { loopThroughOnBoardingScreens() }
    }

    private fun initJob2(){
        val runnable:Runnable = Runnable(object: Runnable, () -> Unit{
            override fun run() { loopThroughOnBoardingScreens2() }
            override fun invoke() { run() }
        })

        mHandler.post(runnable)
    }

    private fun toggleButtonsVisibility(){
        when (mIndex.get()) {
            0 -> {
                binding.btnPreviousOnboarding.visibility = View.GONE
                binding.btnNextOnboarding.visibility = View.VISIBLE
            }
            3 -> {
                binding.btnPreviousOnboarding.visibility = View.VISIBLE
                binding.btnNextOnboarding.visibility = View.GONE
            }
            else -> {
                binding.btnPreviousOnboarding.visibility = View.VISIBLE
                binding.btnNextOnboarding.visibility = View.VISIBLE
            }
        }
    }

    private fun _startActivity(){
        val intent: Intent = Intent(this, AuthActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        //overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit)
       finish()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit)
    }
}