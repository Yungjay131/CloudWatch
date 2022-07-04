package com.slyworks.cloudwatch.view.auth_activity

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import androidx.transition.Transition
import com.facebook.CallbackManager
import com.google.android.material.transition.MaterialSharedAxis
import com.slyworks.authentication.LoginManager
import com.slyworks.cloudwatch.R
import com.slyworks.cloudwatch.databinding.FragmentLoginBinding
import com.slyworks.models.Outcome
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

class LoginFragment : Fragment(R.layout.fragment_login) {
    //region Vars
    private val TAG: String? = LoginFragment::class.simpleName

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val mSubscriptions:CompositeDisposable = CompositeDisposable()

    /*TODO:inject this 2 fields using Dagger, scope LoginManager to this Fragment*/
    private lateinit var mCallbackManager:CallbackManager
    private lateinit var mLoginManager:LoginManager
    //endregion

    companion object {
        @JvmStatic
        fun newInstance(): LoginFragment {
            return LoginFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initTransitions()
    }

    private fun initTransitions(){
       val enterTransition: Transition = MaterialSharedAxis(MaterialSharedAxis.X, false)
       enterTransition.duration = 700

       val exitTransition:Transition = MaterialSharedAxis(MaterialSharedAxis.X, true)

        setEnterTransition(enterTransition)
        setReenterTransition(enterTransition)
        setExitTransition(exitTransition)
        setReturnTransition(exitTransition)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
       _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        initAnimations()
        initViews()
    }

    private fun init(){
        mCallbackManager = CallbackManager.Factory.create()
    }
    private fun initAnimations(){
        binding.tvLogoFragLogin.alpha = 0f

        val logoAnimator:ValueAnimator = ValueAnimator.ofFloat(0f,1f)
        logoAnimator.duration = 3_000
        logoAnimator.interpolator = OvershootInterpolator()

        logoAnimator.addUpdateListener {
            val animatorValue:Float = it.animatedValue as Float

            binding.tvLogoFragLogin.alpha = animatorValue
            binding.tvLogoFragLogin.scaleX = animatorValue
            binding.tvLogoFragLogin.scaleY = animatorValue
        }

        logoAnimator.start()

        val coverAnimation:Animation = AnimationUtils.loadAnimation(requireContext(), R.anim.cover_layout_anim)
        binding.layout1.startAnimation(coverAnimation)
    }

    private fun initViews(){
        binding.btnSignUp.setOnClickListener {
          //TODO:inflate signup fragment
        }

        binding.btnLogin.setOnClickListener {
            val d:Disposable =
                LoginManager.loginViaEmail()
                    .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(::handleLoginResponse)

            mSubscriptions.add(d)
        }

        binding.btnLoginPhoneNumber.setOnClickListener {
            val d:Disposable = LoginManager.loginViaPhoneNumber()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(::handleLoginResponse)

            mSubscriptions.add(d)
        }

        binding.btnLoginGoogle.setOnClickListener {
            val d:Disposable =
                LoginManager.loginViaGoogle(this)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(::handleLoginResponse)

            mSubscriptions.add(d)
        }

        binding.btnLoginFacebook.setOnClickListener {
            val d:Disposable =
                LoginManager.loginViaFacebook(this, mCallbackManager)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(::handleLoginResponse)
        }

        binding.btnLoginTwitter.setOnClickListener {
            val d:Disposable =
                LoginManager.loginViaTwitter(requireActivity())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(::handleLoginResponse)
        }


    }


    private fun handleLoginResponse(result: Outcome){
        when{
            result.isSuccess ->{
               val s:Int = result.getValue() as Int
            }
            result.isFailure ->{
                val s:String = result.getValue() as String
            }
            result.isError ->{
                val s:String = result.getAdditionalInfo()!!
            }
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
}