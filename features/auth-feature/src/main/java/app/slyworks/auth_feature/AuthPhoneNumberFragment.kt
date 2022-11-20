package app.slyworks.auth_feature

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class AuthPhoneNumberFragment : Fragment() {
    companion object {
        @JvmStatic
        fun newInstance() = AuthPhoneNumberFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_phone_number_auth, container, false)
    }


}