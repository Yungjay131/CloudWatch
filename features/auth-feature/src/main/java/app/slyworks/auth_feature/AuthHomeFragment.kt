package app.slyworks.auth_feature

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.slyworks.cloudwatch.databinding.FragmentAuthHomeBinding

class AuthHomeFragment : Fragment() {
    //region Vars
    private lateinit var binding:FragmentAuthHomeBinding
    private lateinit var activity: AuthActivity
    //endregion

    companion object {
        @JvmStatic
        fun newInstance() = AuthHomeFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as AuthActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAuthHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        initViews()
    }

    private fun initData(){}
    private fun initViews(){
        binding.btnLogin.setOnClickListener{ activity.inflateFragment(AuthLoginFragment.newInstance()) }
        binding.btnSignUp.setOnClickListener{ activity.inflateFragment(AuthSignupFragment.newInstance()) }
        binding.btnLoginPhoneNumber.setOnClickListener{ activity.inflateFragment(AuthPhoneNumberFragment.newInstance())}
        binding.btnLoginGoogle.setOnClickListener{ }
        binding.btnLoginFacebook.setOnClickListener{ }
        binding.btnLoginTwitter.setOnClickListener{ }
    }

}