package app.slyworks.cloudwatch.views.activities.auth_activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.slyworks.auth.LoginRepository
import app.slyworks.utils.plusAssign
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class AuthViewModel
@Inject
constructor(private val loginManager: LoginRepository) : ViewModel() {

    val loggedInLiveData: MutableLiveData<Boolean> = MutableLiveData(false)
    val loginButtonProgressLiveData:MutableLiveData<Boolean> = MutableLiveData(false)
    val progressLiveData :MutableLiveData<Boolean> = MutableLiveData(false)
    val messageLiveData:MutableLiveData<String> = MutableLiveData()
    private val disposables: CompositeDisposable = CompositeDisposable()

    fun bind(a:AppCompatActivity):Unit = loginManager.bind(a)
    fun unbind():Unit = loginManager.unbind()

    override fun onCleared() {
        super.onCleared()
        unbind()
        disposables.clear()
    }

    private fun MutableLiveData<Boolean>.updateLoginStatus(newValue:Boolean){
        progressLiveData.postValue(false)
        loginButtonProgressLiveData.postValue(false)
        loggedInLiveData.postValue(newValue)
    }

    private fun MutableLiveData<String>.updateErrorMessage(newValue:String){
        progressLiveData.postValue(false)
        loginButtonProgressLiveData.postValue(false)
        messageLiveData.postValue(newValue)
    }

    fun loginViaEmail(email:String, password:String){
        disposables +=
        loginManager.loginViaEmail(email, password)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe ({
                when{
                    it.isSuccess -> loggedInLiveData.updateLoginStatus(true)
                    it.isFailure ||
                    it.isError-> messageLiveData.updateErrorMessage(it.getAdditionalInfo()!!)
                }
            },{
                Timber.e(it)
                messageLiveData.updateErrorMessage(it.message!!)
            })
    }

    fun loginViaGoogle(context: Context){
        disposables +=
        loginManager.loginViaGoogle(context)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe ({
                when{
                    it.isSuccess -> loggedInLiveData.updateLoginStatus(true)
                    it.isFailure ||
                    it.isError-> messageLiveData.updateErrorMessage(it.getAdditionalInfo()!!)
                }
            },{
                Timber.e(it)
                messageLiveData.updateErrorMessage(it.message!!)
            })
    }

}