package app.slyworks.auth_feature

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.slyworks.cloudwatch.databinding.FragmentAuthSignupBinding
import app.slyworks.cloudwatch.views.activities.MainActivity
import app.slyworks.navigator.Navigator
import app.slyworks.utils.plusAssign
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.util.*

class AuthSignupFragment : Fragment() {
    //region Vars
    private lateinit var binding:FragmentAuthSignupBinding
    private lateinit var authViewModel: AuthViewModel

    private val disposables:CompositeDisposable = CompositeDisposable()
    //endregion

    companion object {
        @JvmStatic
        fun newInstance() = AuthSignupFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        authViewModel = (context as AuthActivity).authViewModel
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
       binding = FragmentAuthSignupBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        initViews()
    }

    private fun initData(){
       authViewModel.buttonProgressLiveData.observe(viewLifecycleOwner, binding.cbtnSignup::setEnabledStatus)
       authViewModel.messageLiveData.observe(viewLifecycleOwner){
           displayMessage(it, binding.root)
       }
       authViewModel.registrationSuccessfulLiveData.observe(viewLifecycleOwner){
           Navigator.intentFor<MainActivity>(requireActivity())
               .newAndClearTask()
               .navigate()
       }

    }

    private fun initViews(){
        disposables +=
            Observable.combineLatest(binding.ctilEmail.observeTextChanges(),
                                     binding.ctilPassword.observeTextChanges(),
                                     binding.ctilConfirmPassword.observeTextChanges(),
                { email:String, password:String , confirmPassword:String ->
                    email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()
                })
                .subscribe({ binding.cbtnSignup.setEnabledStatus(it)}, {})

        binding.cbtnSignup.setOnClickListener {
            requireActivity().closeKeyboard3()

            val email:String = binding.ctilEmail.getText().toLowerCase(Locale.getDefault())
            val password:String = binding.ctilPassword.getText()
            val confirmPassword:String = binding.ctilConfirmPassword.getText()

            if(!check(email, password, confirmPassword))
                return@setOnClickListener

            authViewModel.register(email, password)
        }
    }

    private fun check(email:String, password:String, confirmPassword:String):Boolean{
        var status = true

        if(TextUtils.isEmpty(email)){
            displayMessage("please enter your email", binding.root)
            status = false
        } else if(TextUtils.isEmpty(password)){
            displayMessage("please enter your password", binding.root)
            status = false
        } else if(TextUtils.isEmpty(confirmPassword)){
            displayMessage("please repeat the entered password", binding.root)
            status = false
        }else if(!email.contains("@")){
            displayMessage("please enter a valid email address", binding.root)
            status = false
        }else if(password != email){
            displayMessage("Passwords do not match, please check and try again", binding.root)
            status = false
        }

        return status
    }
}
