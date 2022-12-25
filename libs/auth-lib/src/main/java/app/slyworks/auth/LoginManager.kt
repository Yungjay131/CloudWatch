package app.slyworks.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import app.slyworks.auth.interfaces.LoginRepository
import app.slyworks.crypto.CryptoHelper
import app.slyworks.models.Outcome
import app.slyworks.utils.onNextAndComplete
import app.slyworks.utils.plusAssign
import com.google.android.gms.auth.api.identity.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import timber.log.Timber
import java.util.concurrent.TimeUnit


/**
 * Created by Joshua Sylvanus, 5:57 PM, 01-Oct-22.
 */
enum class OTPVerificationStage{
    ENTER_OTP, PROCESSING, VERIFICATION_SUCCESS,VERIFICATION_FAILURE
}

class LoginManager(private val firebaseAuth: FirebaseAuth,
                   private val cryptoHelper: CryptoHelper): LoginRepository {
    //region Vars
    private val googleSignInSubject: PublishRelay<Outcome> = PublishRelay.create()
    private lateinit var googleOneTapSignInClient:SignInClient
    private lateinit var googleSignInLauncher1:ActivityResultLauncher<IntentSenderRequest>
    private lateinit var googleSignInLauncher2:ActivityResultLauncher<Intent>

    private lateinit var verificationID:String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private val disposables:CompositeDisposable = CompositeDisposable()
    private val resendOTPSubject:PublishSubject<Boolean> = PublishSubject.create()
    private val otpInputSubject:PublishSubject<String> = PublishSubject.create()
    private val otpResultSubject:PublishSubject<Outcome> = PublishSubject.create()
    private val internalOTPSubject:PublishSubject<Outcome> = PublishSubject.create()
    //endregion

    override fun bind(activity: AppCompatActivity){
       googleOneTapSignInClient = Identity.getSignInClient(activity)
       googleSignInLauncher1 =
        activity.registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult(),
            MActivityResultCallback(googleSignInSubject))

        googleSignInLauncher2 =
        activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            MActivityResultCallback(googleSignInSubject))
    }

    override fun unbind(){
        googleSignInLauncher1.unregister()
        googleSignInLauncher2.unregister()
    }

    private fun loginWithFirebaseAuth(email: String, password: String):Observable<Outcome>
            = Observable.create { emitter ->
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful)
                    emitter.onNextAndComplete(Outcome.SUCCESS<Boolean>(true, "user successfully logged in"))
                else{
                    Timber.e("error occurred: ${it.exception?.message}")
                    emitter.onNextAndComplete(Outcome.FAILURE<Boolean>(false, it.exception?.message))
                }

            }
    }

    override fun loginViaEmail(email:String, password:String):Observable<Outcome>
    =   Observable.zip(Observable.just(email), cryptoHelper.hash(password), ::Pair)
            .flatMap{ it:Pair<String,String> -> loginWithFirebaseAuth(it.first, it.second) }
            .onErrorReturn {
                Timber.e("error occurred: ${it.message}")
                Outcome.ERROR<Boolean>(false, additionalInfo = it.message)
            }

    private fun buildPhoneAuthOptions(phoneNumber:String,
                                      resendToken:PhoneAuthProvider.ForceResendingToken? = null)
            : PhoneAuthOptions =
        PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60 * 1_000L, TimeUnit.MILLISECONDS)
            .setCallbacks(phoneAuthCallback)
            .apply {
                resendToken?.let { setForceResendingToken(it) }
            }
            .build()

    private fun verifyOTP(credential: PhoneAuthCredential){
       firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener {
               if(it.isSuccessful)
                   otpResultSubject.onNext(Outcome.SUCCESS(value = OTPVerificationStage.VERIFICATION_SUCCESS))
               else
                   otpResultSubject.onNext(Outcome.FAILURE(value = OTPVerificationStage.VERIFICATION_FAILURE, reason = it.exception?.message))
            }
    }

    override fun loginViaPhoneNumber(phoneNumber: String):Pair<PublishSubject<String>, Observable<Outcome>>{
        disposables +=
           otpInputSubject
               .subscribeOn(Schedulers.io())
               .observeOn(Schedulers.io())
               .subscribe { smsCode:String ->
                  val credential = PhoneAuthProvider.getCredential(verificationID, smsCode)
                  verifyOTP(credential)
           }

        disposables +=
            resendOTPSubject
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe{
                   PhoneAuthProvider.verifyPhoneNumber(
                     buildPhoneAuthOptions(phoneNumber, resendToken))
            }

        disposables +=
            internalOTPSubject
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe {
                    when{
                        it.isSuccess -> verifyOTP(it.getTypedValue())
                        it.isFailure -> otpResultSubject.onNext(it)
                    }
            }

        otpResultSubject.onErrorReturn {
            Outcome.FAILURE(value = "", reason = it.message)
        }

       PhoneAuthProvider.verifyPhoneNumber(
           buildPhoneAuthOptions(phoneNumber))


        /* a chance to use destructuring on the consuming end */
       return (otpInputSubject to otpResultSubject.hide())
    }

    override fun loginViaGoogle(context: Context):Observable<Outcome>{
        /* configure google sign in */
        val googleSignInOptions:GoogleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("")
                .requestEmail()
                .build()

        val googleSignInClient:GoogleSignInClient =
            GoogleSignIn.getClient(context,googleSignInOptions)

        val signInRequest:BeginSignInRequest
                = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions
                    .builder()
                    .setSupported(true)
                        /*fixme: get this server ID from Firebase */
                    .setServerClientId("928864674268-nphsnq4b7qh34j6lmvmv8gghctp2gn68.apps.googleusercontent.com")
                    .setFilterByAuthorizedAccounts(true)
                    .build())
            .build()

        googleOneTapSignInClient.beginSignIn(signInRequest)
            .addOnCompleteListener { task:Task<BeginSignInResult> ->
                if(task.isSuccessful){
                    val intentSenderRequest:IntentSenderRequest
                    = IntentSenderRequest.Builder(task.result.pendingIntent.intentSender)
                        .build()

                    googleSignInLauncher1.launch(intentSenderRequest)
                }
                else{
                    /* start the process??? */
                    val signInIntent: Intent = googleSignInClient.signInIntent
                    googleSignInLauncher2.launch(signInIntent)
                }
            }

        return googleSignInSubject
               .onErrorReturn{
                   Timber.e("error occurred: ${it.message}")
                   Outcome.ERROR<Boolean>(false, additionalInfo = it.message)
               }.hide()
    }

    private val phoneAuthCallback : PhoneAuthProvider.OnVerificationStateChangedCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                internalOTPSubject.onNext(Outcome.SUCCESS(value = p0))
                otpResultSubject.onNext(Outcome.SUCCESS(value = OTPVerificationStage.PROCESSING))
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                var message = "something went wrong verifying OTP"
                when (p0) {
                    is FirebaseAuthInvalidCredentialsException ->
                        message = "invalid OTP, please check and try again"
                    is FirebaseTooManyRequestsException ->
                        message = "something went wrong on our end. Please try again"
                }
                otpResultSubject.onNext(Outcome.FAILURE(value = OTPVerificationStage.VERIFICATION_FAILURE, message))
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                verificationID = p0
                resendToken = p1
                otpResultSubject.onNext(Outcome.SUCCESS(value = OTPVerificationStage.ENTER_OTP))
            }
        }

    inner class MActivityResultCallback(private val o:PublishRelay<Outcome>)
        : ActivityResultCallback<ActivityResult>{
        override fun onActivityResult(result: ActivityResult?) {
            if(result == null){
                o.accept(Outcome.FAILURE<Boolean>(false, "no result retrieved"))
                return
            }

            when(result.resultCode){
                Activity.RESULT_OK ->{
                    var credential:SignInCredential? = null
                    var idToken:String? = null
                    var firebaseCredential:AuthCredential? = null
                    try{
                      credential = googleOneTapSignInClient. getSignInCredentialFromIntent(result.data)
                      idToken = credential.googleIdToken

                      firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                    }catch(e:Exception){
                        Timber.e(e, "no idToken")
                        o.accept(Outcome.ERROR<Boolean>(false, e.message))
                        return
                    }

                    firebaseAuth.signInWithCredential(firebaseCredential)
                        .addOnCompleteListener {
                            if(it.isSuccessful)
                                o.accept(Outcome.SUCCESS<Boolean>(true, "sign-in with credential successful"))
                            else
                                o.accept(Outcome.FAILURE<Boolean>(false, it.exception?.message))
                        }
                }
                else -> o.accept(Outcome.FAILURE<Boolean>(false, "attempt to login was not successful"))
            }


        }
    }

}