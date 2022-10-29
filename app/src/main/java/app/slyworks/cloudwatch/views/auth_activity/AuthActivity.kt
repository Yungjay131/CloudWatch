package app.slyworks.cloudwatch.views.auth_activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import app.slyworks.auth.LoginManager
import app.slyworks.cloudwatch.databinding.ActivityAuthBinding
import app.slyworks.mvi.EventObservable
import app.slyworks.mvi.StateSubscriber
import app.slyworks.navigator.Navigator
import app.slyworks.navigator.interfaces.FragmentContinuationStateful
import javax.inject.Inject

class AuthActivity : AppCompatActivity(),
    StateSubscriber<ViewState>,
    EventObservable<> {
    //region Vars
    private lateinit var binding:ActivityAuthBinding
    private lateinit var navigator:FragmentContinuationStateful

    @Inject
    lateinit var loginManager: LoginManager

    @Inject
    lateinit var authViewModel:AuthViewModel
    //endregion

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onRetainCustomNonConfigurationInstance(): Any? {
        return super.onRetainCustomNonConfigurationInstance()
    }

    override fun onDestroy() {
        super.onDestroy()
        navigator.onDestroy()
        loginManager.unbind()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* TODO: add fullscreen code here from iGrades and extra from StackOverflow */
        /* TODO: add white/black gradient overlay asset, generated from adobeXD, inspiration pinterest*/
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initData()
        initViews()
    }

    private fun initData(){
        loginManager.bind(this)
    }

    private fun initViews(){
        navigator =
        Navigator.transactionWithStateFrom(this.supportFragmentManager)
                 .also {
                     it.into(binding.root.id)
                     it.show(AuthHomeFragment.newInstance())
                     it.navigate()
                 }
    }

    fun inflateFragment(f: Fragment){
        navigator.show(f)
            .hideCurrent()
            .navigate()
    }
}