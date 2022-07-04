package com.slyworks.cloudwatch.view.auth_activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.slyworks.cloudwatch.R

class PhoneNumberAuthFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_phone_number_auth, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() = PhoneNumberAuthFragment()

    }
}