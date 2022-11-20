package app.slyworks.auth_feature

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.slyworks.cloudwatch.databinding.FragmentAuthEnterOtpBinding

class AuthEnterOTPFragment : Fragment() {
    //region Vars
    private lateinit var binding:FragmentAuthEnterOtpBinding
    private lateinit var authViewModel: AuthViewModel
    //endregion

    companion object {
        @JvmStatic
        fun newInstance(): AuthEnterOTPFragment = AuthEnterOTPFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        authViewModel = (context as AuthActivity).authViewModel
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAuthEnterOtpBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        initViews()
    }

    private fun initData(){}
    private fun initViews(){}
}