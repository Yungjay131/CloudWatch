package com.slyworks.cloudwatch.view.auth_activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.slyworks.cloudwatch.R

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        inflateLoginFragment()
    }

    private fun inflateLoginFragment(){
        val f: Fragment = LoginFragment.newInstance()

        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)

        if(f.isAdded) transaction.show(f)
        else transaction.replace(R.id.fragment_container_auth, f)

        transaction.commit()
    }
}