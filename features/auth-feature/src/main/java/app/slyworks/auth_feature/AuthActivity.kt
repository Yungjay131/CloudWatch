package app.slyworks.auth_feature

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import app.slyworks.cloudwatch.databinding.ActivityAuthBinding
import app.slyworks.cloudwatch.views.activities.BaseActivity
import app.slyworks.navigator.Navigator
import app.slyworks.navigator.interfaces.FragmentContinuationStateful

class AuthActivity : BaseActivity() {
    //region Vars
    private lateinit var binding:ActivityAuthBinding
    private lateinit var navigator:FragmentContinuationStateful

    lateinit var authViewModel: AuthViewModel
    //endregion

    companion object{
        private var hasDaggerBeenInitialized:Boolean = false
    }

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
        hasDaggerBeenInitialized = false
        navigator.onDestroy()
        authViewModel.unbind()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if(!hasDaggerBeenInitialized){
            hasDaggerBeenInitialized = true
        }
        super.onCreate(savedInstanceState)

        /* TODO: add fullscreen code here from iGrades and extra from StackOverflow */
        /* TODO: add white/black gradient overlay asset, generated from adobeXD, inspiration pinterest*/
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initData()
        initViews()
    }

    private fun initData(){
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        authViewModel.bind(this)
        authViewModel.messageLiveData.observe(this){ displayMessage(it) }
        authViewModel.buttonProgressLiveData.observe(this){}
        authViewModel.progressLiveData.observe(this){ }
        authViewModel.loggedInLiveData.observe(this){}
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