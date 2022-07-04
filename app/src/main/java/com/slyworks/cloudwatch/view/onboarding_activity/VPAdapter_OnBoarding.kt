package com.slyworks.cloudwatch.view.onboarding_activity

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter


/**
 * Created by Joshua Sylvanus, 9:22 AM, 14/4/2022.
 */
class VPAdapter_OnBoarding(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle){
    companion object{
        private val FRAGMENT_COUNT = 4
    }

    override fun getItemCount(): Int {
        return FRAGMENT_COUNT
    }
    override fun createFragment(position:Int): Fragment {
        return when(position) {
            0 -> OnBoarding_1_Fragment.newInstance();
            1 -> OnBoarding_2_Fragment.newInstance();
            2 -> OnBoarding_3_Fragment.newInstance();
            3 -> OnBoarding_4_Fragment.newInstance();
            else -> throw IllegalArgumentException()
        }
    }


}