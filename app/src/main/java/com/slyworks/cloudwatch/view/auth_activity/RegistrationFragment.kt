package com.slyworks.cloudwatch.view.auth_activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.slyworks.authentication.FirebaseStore
import com.slyworks.models.TempUser
import com.slyworks.cloudwatch.R
import io.reactivex.rxjava3.disposables.CompositeDisposable

class RegistrationFragment : Fragment() {
    //region Vars
    private val mSubscriptions:CompositeDisposable = CompositeDisposable()
    //endregion

    companion object {
        @JvmStatic
        fun newInstance(): RegistrationFragment {
            return RegistrationFragment()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        mSubscriptions.clear()
    }

    override fun onResume() {
        super.onResume()
        FirebaseStore.setContentResolver(requireActivity().contentResolver)
    }

    override fun onStop() {
        super.onStop()
        FirebaseStore.nullifyContentResolver()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_registration, container, false)
    }

    private fun initViews(view:View){}
    private fun register(user: TempUser){}

    private fun handleRegistrationResponse(result: Outcome){
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

}