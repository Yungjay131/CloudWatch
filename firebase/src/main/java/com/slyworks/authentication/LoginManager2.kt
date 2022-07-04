package com.slyworks.authentication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
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
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.slyworks.constants.STATUS_SUCCESS
import com.slyworks.models.Outcome
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject


/**
 *Created by Joshua Sylvanus, 3:43 PM, 02-Jul-22.
 */

typealias FBLoginManager = com.facebook.login.LoginManager
class LoginManager2(private val firebaseAuth:FirebaseAuth) {
    //region Vars
    private val TAG: String? = LoginManager2::class.simpleName

    private lateinit var oneTapClient:SignInClient

    private val googleSignInObservable:PublishSubject<Outcome> = PublishSubject.create()
    private val facebookSignInObservable:PublishSubject<Outcome> = PublishSubject.create()

    private lateinit var googleResultLauncher1:ActivityResultLauncher<IntentSenderRequest>
    private lateinit var googleResultLauncher2:ActivityResultLauncher<Intent>
    //endregion

    inner class MActivityResultCallback(private val observable:PublishSubject<Outcome>)
        :ActivityResultCallback<ActivityResult>{
        override fun onActivityResult(result: ActivityResult?) {
            if(result == null){
                observable.onNext(
                    Outcome.FAILURE(value = "google sign in ActivityResult object should not be null"))
                return
            }

            when(result.resultCode){
                Activity.RESULT_OK ->{
                    try{
                        val credential:SignInCredential =
                            oneTapClient.getSignInCredentialFromIntent(result.data)
                        val IDToken:String? = credential.googleIdToken

                        when{
                            IDToken != null ->{
                                //got an ID back from Google. Use it to authenticate with Firebase
                                Timber.e(TAG, "onActivityResult: got ID token")

                                val firebaseCredential: AuthCredential = GoogleAuthProvider.getCredential(idToken, null)
                                verifyAuthCredential(
                                    activity,
                                    firebaseCredential,
                                    observable )
                            }
                            else ->{
                                //shouldn't happen, but all the same
                                Tmber.e(TAG, "onActivityResult: no ID token")
                                observable.onNext(
                                    Outcome.FAILURE(null, reason = "error occurred"))
                            }
                        }
                    }catch(e: Exception){
                        observable.onNext(
                            Outcome.ERROR<Nothing>(value = e.message))
                    }
                }
                else ->{}
            }
        }
    }

    private fun init(activity: AppCompatActivity){
        googleResultLauncher1 =
            activity.registerForActivityResult(
                ActivityResultContracts.StartIntentSenderForResult(),
                MActivityResultCallback(googleSignInObservable))

        googleResultLauncher2 =
            activity.registerForActivityResult(
                ActivityResultContracts.StartActivityForResult(),
                MActivityResultCallback(googleSignInObservable))
    }

    fun verifyAuthCredential(activity: Activity,
                             credential:AuthCredential,
                             observable:PublishSubject<Outcome>){

        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    val r:Outcome = Outcome.SUCCESS<Nothing>(additionalInfo = "sign in was successful")
                    observable.onNext(r)
                } else{
                    when(it.exception){
                        is FirebaseAuthInvalidCredentialsException ->{
                            // The verification code entered was invalid
                            val r:Outcome = Outcome.FAILURE<Nothing>(reason = "the verification code entered was invalid")
                            observable.onNext(r)
                        }
                        else ->{
                            val r:Outcome = Outcome.FAILURE<Nothing>( reason = it.exception?.message)
                            observable.onNext(r)
                        }
                    }
                }

                observable.onComplete()
            }
    }

    fun loginViaEmail(email:String, password:String):Observable<Outcome> =
        Observable.create<Outcome> { emitter ->
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if(it.isSuccessful)
                        emitter.onNext(Outcome.SUCCESS<Nothing>())
                    else
                        emitter.onNext(Outcome.FAILURE<Nothing>())

                    emitter.onComplete()
                }
        }
        .doOnError{
            Timber
            Log.e(TAG, "loginViaEmail: an error occurred", it)
        }

        /*would actually be from a Fragment???*/
    fun loginViaGoogle(activity: Activity):Observable<Outcome> =
        getGoogleSignInLauncherObservable(activity)



    private fun getGoogleSignInLauncherObservable(activity: Activity):Observable<Outcome> =
        Observable.create { emitter ->
            oneTapClient = Identity.getSignInClient(activity)
            val googleSignInClient:GoogleSignInClient
            val webClientID:String = BuildConfig.WEB_CLIENT_ID

            /*configure Google Sign-In*/
            val googleSignInOptions:GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(webClientID)
                .requestEmail()
                .build()

            googleSignInClient = GoogleSignIn.getClient(activity, googleSignInOptions)

           val signInRequest:BeginSignInRequest =
               BeginSignInRequest.builder()
                   .setGoogleIdTokenRequestOptions(
                       BeginSignInRequest.GoogleIdTokenRequestOptions
                           .builder()
                           .setSupported(true)
                           .setServerClientId(webClientID)
                           .setFilterByAuthorizedAccounts(true)
                           .build())
               .build()

            googleSignInObservable
                .subscribe {
                    emitter.onNext(it)
                    emitter.onComplete()
                }

            oneTapClient.beginSignIn(signInRequest)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        val intentSenderRequest:IntentSenderRequest =
                            IntentSenderRequest.Builder(task.result.pendingIntent.intentSender)
                                .build()

                        googleResultLauncher1.launch(intentSenderRequest)
                    }else{
                        /*start the process*/
                        val signInIntent: Intent = googleSignInClient.signInIntent
                        googleResultLauncher2.launch(signInIntent)
                    }
                }
        }

    fun loginViaFacebook(context: Fragment, callbackManager:CallbackManager): Observable<Outcome> =
        getFacebookObservable(context, callbackManager)


    private fun getFacebookObservable(context:Fragment, callbackManager:CallbackManager):Observable<Outcome> =
        Observable.create { emitter ->
            val facebookCallback: FacebookCallback<LoginResult> =
                object:FacebookCallback<LoginResult>{
                    override fun onCancel() {
                        val r:Outcome = Outcome.FAILURE<Nothing>(reason = "facebook authentication request was cancelled")
                        emitter.onNext(r)
                        emitter.onComplete()
                    }
                    override fun onError(error: FacebookException) {
                        val r:Outcome = Outcome.FAILURE<Nothing>(reason = error.message)
                        emitter.onNext(r)
                        emitter.onComplete()
                    }

                    override fun onSuccess(result: LoginResult) {
                        facebookSignInObservable
                            .subscribe {
                                emitter.onNext(it)
                                emitter.onComplete()
                            }


                        val credential:AuthCredential = FacebookAuthProvider.getCredential(result.accessToken.token)
                        verifyAuthCredential(context.requireActivity(), credential, facebookSignInObservable)
                    }
                }


            /*calling login() should run the whole >>resultLauncher.launch(intent) process???*/
            FBLoginManager.getInstance().registerCallback(callbackManager,facebookCallback)
            FBLoginManager.getInstance().logIn(context, mutableListOf("email","public"))
        }

    fun loginViaTwitter(activity:Activity):Observable<Outcome> =
        getTwitterLoginObservable(activity)

    private fun getTwitterLoginObservable(activity:Activity):Observable<Outcome> =
        Observable.create { emitter ->
            val oauthProvider:OAuthProvider.Builder = OAuthProvider.newBuilder("twitter.com")

            /*would open chrome tab:NB don't reference your activity as scope for addOnCompleteListener*/
            val pendingResultTask: Task<AuthResult>? = firebaseAuth.getPendingAuthResult()

            if (pendingResultTask != null) {
                /*There's something already here! Finish the sign-in for your user*/
                pendingResultTask.addOnCompleteListener {
                    if(it.isSuccessful) {
                        val r: Outcome = Outcome.SUCCESS(STATUS_SUCCESS)
                        emitter.onNext(r)
                    }else{
                        val r: Outcome = Outcome.FAILURE<Nothing>(reason = it.exception?.message)
                        emitter.onNext(r)
                    }

                    emitter.onComplete()
                }
            } else {
                /*There's no pending result so you need to start the sign-in flow.*/
                firebaseAuth.startActivityForSignInWithProvider(activity, oauthProvider.build())
                    .addOnCompleteListener {
                        if(it.isSuccessful) {
                            val r: Outcome = Outcome.SUCCESS(STATUS_SUCCESS)
                            emitter.onNext(r)
                        }else{
                            val r: Outcome = Outcome.FAILURE<Nothing>( reason = it.exception?.message)
                            emitter.onNext(r)
                        }

                        emitter.onComplete()
                    }
            }
        }
}