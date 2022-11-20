package app.slyworks.auth_feature

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.slyworks.auth.LoginRepository
import app.slyworks.auth.OTPVerificationStage
import app.slyworks.auth.RegistrationRepository
import app.slyworks.utils.plusAssign
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject

class AuthViewModel
@Inject
constructor(private val registrationManager: RegistrationRepository,
            private val loginManager: LoginRepository) : ViewModel() {

    val registrationSuccessfulLiveData:MutableLiveData<Boolean> = MutableLiveData(false)

    val loggedInLiveData: MutableLiveData<Boolean> = MutableLiveData(false)
    val buttonProgressLiveData:MutableLiveData<Boolean> = MutableLiveData(false)
    val progressLiveData :MutableLiveData<Boolean> = MutableLiveData(false)
    val messageLiveData:MutableLiveData<String> = MutableLiveData()
    val enterOTPLiveData:MutableLiveData<Boolean> = MutableLiveData()

    private lateinit var otpInputSubject:PublishSubject<String>
    private val disposables: CompositeDisposable = CompositeDisposable()

    fun bind(a:AppCompatActivity):Unit = loginManager.bind(a)
    fun unbind():Unit = loginManager.unbind()

    override fun onCleared() {
        super.onCleared()
        unbind()
        disposables.clear()
    }

    private fun MutableLiveData<Boolean>.updateRegistrationSuccessStatus(){
        progressLiveData.postValue(false)
        buttonProgressLiveData.postValue(false)
        registrationSuccessfulLiveData.postValue(true)
    }

    private fun MutableLiveData<Boolean>.updateNavigateToOTP(){
        progressLiveData.postValue(false)
        buttonProgressLiveData.postValue(false)
        enterOTPLiveData.postValue(true)
    }

    private fun MutableLiveData<Boolean>.updateProgressStatus(newValue: Boolean){
        progressLiveData.postValue(newValue)
        buttonProgressLiveData.postValue(newValue)
    }

    private fun MutableLiveData<Boolean>.updateLoginStatus(){
        progressLiveData.postValue(false)
        buttonProgressLiveData.postValue(false)
        loggedInLiveData.postValue(true)
    }

    private fun MutableLiveData<String>.updateErrorMessage(newValue:String){
        progressLiveData.postValue(false)
        buttonProgressLiveData.postValue(false)
        messageLiveData.postValue(newValue)
    }

    fun register(email:String, password: String){
        progressLiveData.updateProgressStatus(true)

        disposables +=
            registrationManager.registerViaEmail(email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe({
                    when{
                        it.isSuccess -> registrationSuccessfulLiveData.updateRegistrationSuccessStatus()
                        it.isFailure -> messageLiveData.updateErrorMessage(it.getAdditionalInfo()!!)
                    }
                },{
                    Timber.e(it)
                    messageLiveData.updateErrorMessage(it.message!!)
                })
    }

    fun loginViaEmail(email:String, password:String){
        progressLiveData.updateProgressStatus(true)

        disposables +=
        loginManager.loginViaEmail(email, password)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe ({
                when{
                    it.isSuccess -> loggedInLiveData.updateLoginStatus()
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
                    it.isSuccess -> loggedInLiveData.updateLoginStatus()
                    it.isFailure ||
                    it.isError-> messageLiveData.updateErrorMessage(it.getAdditionalInfo()!!)
                }
            },{
                Timber.e(it)
                messageLiveData.updateErrorMessage(it.message!!)
            })
    }

    fun enterOTP(otp:String):Unit = otpInputSubject.onNext(otp)

    fun loginViaPhoneNumber(phoneNumber:String){
       progressLiveData.updateProgressStatus(true)

       val (oInput, oResult) = loginManager.loginViaPhoneNumber(phoneNumber)
       otpInputSubject = oInput

       disposables +=
       oResult.subscribeOn(Schedulers.io())
           .observeOn(Schedulers.io())
           .subscribe({
               when{
                   it.isSuccess ->{
                       when(it.getTypedValue<OTPVerificationStage>()){
                         OTPVerificationStage.ENTER_OTP -> enterOTPLiveData.updateNavigateToOTP()
                         OTPVerificationStage.PROCESSING -> progressLiveData.updateProgressStatus(true)
                         OTPVerificationStage.VERIFICATION_SUCCESS -> loggedInLiveData.updateLoginStatus()
                         OTPVerificationStage.VERIFICATION_FAILURE -> messageLiveData.updateErrorMessage(it.getAdditionalInfo()!!)
                       }
                   }
                   it.isFailure -> messageLiveData.updateErrorMessage(it.getAdditionalInfo()!!)
               }
           },{
               Timber.e(it)
               messageLiveData.updateErrorMessage(it.message!!)
           })
    }

}