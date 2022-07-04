package com.slyworks.authentication

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.result.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import com.google.firebase.auth.AuthResult

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.OAuthProvider
import com.slyworks.constants.STATUS_DISPLAY_OTP_UI
import com.slyworks.constants.STATUS_SUCCESS
import com.slyworks.models.Outcome
import io.reactivex.rxjava3.core.ObservableEmitter
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import io.reactivex.rxjava3.subjects.BehaviorSubject

typealias FBLoginManager = com.facebook.login.LoginManager
object LoginManager {
    private val RESULT_CODE: Int = 2_000
    private val REQUEST_CODE: Int = 1_000
    private lateinit var mResendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var mStoredVerificationId: String

    //region Vars
    private val TAG: String? = LoginManager::class.simpleName

    private var mFirebaseAuth:FirebaseAuth = FirebaseAuth.getInstance()
    //endregion

    private fun _verifyAuthCredential(
        activity:Activity,
        credential:AuthCredential,
        emitter: ObservableEmitter<Outcome>){
        mFirebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    val r:Outcome = Outcome.SUCCESS(null, additionalInfo = "sign in was successful")
                    emitter.onNext(r)
                } else {
                    when(task.exception){
                        is FirebaseAuthInvalidCredentialsException ->{
                            // The verification code entered was invalid
                            val r:Outcome = Outcome.FAILURE(null,reason = "the verification code entered was invalid")
                            emitter.onNext(r)
                        }
                        else ->{
                            val r:Outcome = Outcome.ERROR(null, error = task.exception)
                            emitter.onNext(r)
                        }
                    }
                }

                emitter.onComplete()
            }
    }
    private fun _verifyAuthCredential(
        activity:Activity,
        credential:AuthCredential,
        emitter: BehaviorSubject<Outcome>){
        mFirebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    val r:Outcome = Outcome.SUCCESS(STATUS_SUCCESS,additionalInfo = "sign in was successful")
                    emitter.onNext(r)
                } else {
                    when(task.exception){
                        is FirebaseAuthInvalidCredentialsException ->{
                            // The verification code entered was invalid
                            val r:Outcome = Outcome.FAILURE(null,reason = "the verification code entered was invalid")
                            emitter.onNext(r)
                        }
                        else ->{
                            val r:Outcome = Outcome.ERROR(null,error = task.exception)
                            emitter.onNext(r)
                        }
                    }
                }

                emitter.onComplete()
            }
    }

    fun verifyPhoneLoginCode(activity: Activity, o:BehaviorSubject<Outcome>, code:String){
        /*could also be gotten from onVerificationCompleted*/
        val credential:PhoneAuthCredential = PhoneAuthProvider.getCredential(mStoredVerificationId, code)
        _verifyAuthCredential(activity,credential,o)
    }

    fun loginViaEmail(email:String, password:String):Observable<Outcome>{
        val o:Observable<Outcome> = Observable.create { emitter ->
            mFirebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        val r:Outcome = Outcome.SUCCESS(null)
                        emitter.onNext(r)
                    } else{
                        val r:Outcome = Outcome.ERROR(null,error = it.exception)
                        emitter.onNext(r)
                    }

                    emitter.onComplete()
                }
        }
        
        o.doOnError {
            Log.e(TAG, "loginViaEmail: error occurred ", it )
        }.subscribeOn(Schedulers.io())

        return o
    }

    // STOPSHIP: 4/13/2022 would not be in the prod version, simply for testing purposes
    fun loginViaPhoneNumber(phoneNumber:String, activity: Activity):Observable<Outcome>{
        //TODO:to avoid memory leaks dispose of this Observable<> in LoginFragment as soon as possible???
        val o:BehaviorSubject<Outcome> = BehaviorSubject.create<Outcome>{ emitter ->

            val callback:PhoneAuthProvider.OnVerificationStateChangedCallbacks =
                object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    _verifyAuthCredential(activity,credential,emitter)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    when(e){
                        is FirebaseAuthInvalidCredentialsException ->{
                            // Invalid request
                            val r:Outcome = Outcome.FAILURE(null,reason = "invalid request")
                            emitter.onNext(r)
                        }
                        is FirebaseTooManyRequestsException ->{
                            // The SMS quota for the project has been exceeded
                            val r:Outcome = Outcome.FAILURE(null,reason = "oh oh something went wrong on our end")
                            emitter.onNext(r)
                        }
                    }

                      emitter.onComplete()
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    // Save verification ID and resending token so we can use them later
                    mStoredVerificationId = verificationId
                    mResendToken = token

                    val r:Outcome = Outcome.SUCCESS(STATUS_DISPLAY_OTP_UI)
                    emitter.onNext(r)
                }

                override fun onCodeAutoRetrievalTimeOut(p0: String) {
                    super.onCodeAutoRetrievalTimeOut(p0)
                    /*should time out after 2min or 120secs*/
                    /*would not be used because i'll have a manual countdown for 120_000ms*/
                }
            }

            val options = PhoneAuthOptions.newBuilder(mFirebaseAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(120L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callback)
                .build()

            PhoneAuthProvider.verifyPhoneNumber(options)
        } as BehaviorSubject<Outcome>

        o.doOnError {
            Log.e(TAG, "loginViaPhoneNumber: error occurred",it)
        }.subscribeOn(Schedulers.io())


        return o
    }

    fun loginViaGoogle(activity:Fragment):Observable<Outcome>{
        val oneTapClient:SignInClient = Identity.getSignInClient(activity.requireContext())
        val googleSignInClient: GoogleSignInClient
        val webClientID = activity.getString(R.string.web_client_id)

        //configure Google Sign In
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientID)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(activity.requireActivity(), googleSignInOptions)

        fun _authenticate(result:ActivityResult?, emitter:ObservableEmitter<Outcome>){
            requireNotNull(result) { "_authenticate: ActivityResult object should not be null" }

            when(result.resultCode){
                Activity.RESULT_OK ->{
                    try {
                        val credential: SignInCredential = oneTapClient.getSignInCredentialFromIntent(result.data)
                        val idToken:String? = credential.googleIdToken
                        when {
                            idToken != null -> {
                                //got an ID back from Google. Use it to authenticate with Firebase
                                Log.e(TAG, "onActivityResult: got ID token")

                                val firebaseCredential:AuthCredential = GoogleAuthProvider.getCredential(idToken, null)
                                _verifyAuthCredential(activity.requireActivity(),firebaseCredential,emitter)
                            }else -> {
                                //shouldn't happen
                                Log.e(TAG, "onActivityResult: no ID token")
                                val r:Outcome = Outcome.FAILURE(null,reason = "error occurred retrieving user token. Please try again")
                                emitter.onNext(r)
                                emitter.onComplete()
                            }
                        }
                    }catch(ae: ApiException){
                        val r:Outcome = Outcome.ERROR(null,error = ae)
                        emitter.onNext(r)
                        emitter.onComplete()
                    }catch(e:Exception){
                        val r:Outcome = Outcome.ERROR(null,error = e)
                        emitter.onNext(r)
                        emitter.onComplete()
                    }
                }else->{
                    Log.e(TAG, "onActivityResult: request failed")

                    val r:Outcome = Outcome.FAILURE(null,reason = "request failed")
                    emitter.onNext(r)
                    emitter.onComplete()
                }
            }
        }


        val o:Observable<Outcome> = Observable.create(object:ObservableOnSubscribe<Outcome>{
            override fun subscribe(emitter: ObservableEmitter<Outcome>?) {
                val resultLauncher1:ActivityResultLauncher<IntentSenderRequest> = activity.registerForActivityResult(
                    ActivityResultContracts.StartIntentSenderForResult(),
                    object: ActivityResultCallback<ActivityResult>{
                        override fun onActivityResult(result: ActivityResult?) {
                            _authenticate(result, emitter!!)
                        }
                    })

                val signInRequest:BeginSignInRequest = BeginSignInRequest.builder()
                    .setGoogleIdTokenRequestOptions(
                        BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                            .setSupported(true)
                            //your server's clientID not your android client ID
                            .setServerClientId(webClientID)
                            //only show accounts previously used to sign in
                            .setFilterByAuthorizedAccounts(true)
                            .build())
                    .build()

                oneTapClient.beginSignIn(signInRequest)
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful){
                            val intentSenderRequest:IntentSenderRequest =
                                IntentSenderRequest.Builder(task.result.pendingIntent.intentSender)
                                    .build()

                            resultLauncher1.launch(intentSenderRequest)
                        } else{
                            /*start the process???*/
                            val resultLauncher2:ActivityResultLauncher<Intent> = activity.registerForActivityResult(
                                ActivityResultContracts.StartActivityForResult(),
                                object : ActivityResultCallback<ActivityResult> {
                                    override fun onActivityResult(result: ActivityResult) {
                                        _authenticate(result,emitter!!)
                                    }
                                })

                            val signInIntent:Intent = googleSignInClient.signInIntent
                            resultLauncher2.launch(signInIntent)
                        }
                    }
            }
        })


        o.subscribeOn(Schedulers.io())
        return o
    }

    fun loginViaFacebook(context:Fragment, callbackManager: CallbackManager):Observable<Outcome>{
        val o:Observable<Outcome> = Observable.create { emitter ->

            val facebookCallback = object: FacebookCallback<LoginResult>{
                override fun onCancel() {
                    val r:Outcome = Outcome.FAILURE(null,reason = "facebook authentication request was cancelled")
                    emitter.onNext(r)
                    emitter.onComplete()
                }
                override fun onError(error: FacebookException) {
                    val r:Outcome = Outcome.ERROR(null,error = error)
                    emitter.onNext(r)
                    emitter.onComplete()
                }

                override fun onSuccess(result: LoginResult) {
                    val credential:AuthCredential = FacebookAuthProvider.getCredential(result.accessToken.token)
                    _verifyAuthCredential(context.requireActivity(),credential, emitter)
                }
            }

            /*calling login() should run the whole >>resultLauncher.launch(intent) process???*/
            FBLoginManager.getInstance().registerCallback(callbackManager,facebookCallback)
            FBLoginManager.getInstance().logIn(context, mutableListOf("email","public"))
        }

        o.subscribeOn(Schedulers.io())

        return o
    }

    fun loginViaTwitter(activity:Activity):Observable<Outcome>{
        val oauthProvider:OAuthProvider.Builder = OAuthProvider.newBuilder("twitter.com")

        val o:Observable<Outcome> = Observable.create { emitter ->
            /*would open chrome tab:NB don't reference your activity as scope for addOnCompleteListener*/
            val pendingResultTask: Task<AuthResult>? = mFirebaseAuth.getPendingAuthResult()

            if (pendingResultTask != null) {
                 /*There's something already here! Finish the sign-in for your user*/
                    pendingResultTask.addOnCompleteListener {
                        if(it.isSuccessful) {
                            val r: Outcome = Outcome.SUCCESS(STATUS_SUCCESS)
                            emitter.onNext(r)
                        }else{
                            val r: Outcome = Outcome.FAILURE(null,reason = it.exception?.message)
                            emitter.onNext(r)
                        }

                        emitter.onComplete()
                    }
            } else {
                 /*There's no pending result so you need to start the sign-in flow.*/
                mFirebaseAuth.startActivityForSignInWithProvider(activity, oauthProvider.build())
                    .addOnCompleteListener {
                        if(it.isSuccessful) {
                            val r: Outcome = Outcome.SUCCESS(STATUS_SUCCESS)
                            emitter.onNext(r)
                        }else{
                            val r: Outcome = Outcome.FAILURE(null,reason = it.exception?.message)
                            emitter.onNext(r)
                        }

                        emitter.onComplete()
                    }
            }
        }

        o.doOnError {
            Log.e(TAG, "loginViaTwitter: error occurred", it)
        }.subscribeOn(Schedulers.io())

        return o
    }

    fun loginViaGitHub(activity: Activity):Observable<Outcome> {
        val oauthProvider: OAuthProvider.Builder = OAuthProvider.newBuilder("github.com")

        //OPTIONAL: Request read access to a user's email addresses.
        // This must be preconfigured in the app's API permissions.
        val scopes: List<String> = mutableListOf("user:email")
        oauthProvider.setScopes(scopes)

        val o: Observable<Outcome> = Observable.create { emitter ->
            val pendingResultTask: Task<AuthResult>? = mFirebaseAuth.getPendingAuthResult()
            if (pendingResultTask != null) {
                /*There's something already here! Finish the sign-in for your user*/
                pendingResultTask.addOnCompleteListener {
                    if(it.isSuccessful) {
                        val r: Outcome = Outcome.SUCCESS(STATUS_SUCCESS)
                        emitter.onNext(r)
                    }else{
                        val r: Outcome = Outcome.FAILURE(null,reason = it.exception?.message)
                        emitter.onNext(r)
                    }

                    emitter.onComplete()
                }
            } else {
                 /*There's no pending result so you need to start the sign-in flow.*/
                mFirebaseAuth.startActivityForSignInWithProvider(activity, oauthProvider.build())
                    .addOnCompleteListener{
                        if(it.isSuccessful) {
                            val r: Outcome = Outcome.SUCCESS(STATUS_SUCCESS)
                            emitter.onNext(r)
                        }else{
                            val r: Outcome = Outcome.FAILURE(null,reason = it.exception?.message)
                            emitter.onNext(r)
                        }

                        emitter.onComplete()
                    }
            }
        }

        o.doOnError {
            Log.e(TAG, "loginViaGitHub: error occurred",it )
        }.subscribeOn(Schedulers.io())

        return o
    }

    private fun saveUserDetails(){
        /*TODO:save user details like profile picture to DataStore*/
    }
}