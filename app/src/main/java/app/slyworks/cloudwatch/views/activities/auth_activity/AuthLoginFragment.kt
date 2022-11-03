package app.slyworks.cloudwatch.views.activities.auth_activity

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.slyworks.cloudwatch.R
import app.slyworks.cloudwatch.databinding.FragmentAuthLoginBinding

class AuthLoginFragment : Fragment() {
    //region Vars
    private lateinit var binding:FragmentAuthLoginBinding
    private lateinit var activity: AuthActivity
    //endregion

    companion object {
        @JvmStatic
        fun newInstance() = AuthLoginFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as AuthActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAuthLoginBinding.inflate(layoutInflater, container, false)
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