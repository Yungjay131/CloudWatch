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
import com.google.android.gms.auth.api.identity.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.core.Observable
import timber.log.Timber


/**
 * Created by Joshua Sylvanus, 5:57 PM, 01-Oct-22.
 */
class LoginManager(private val firebaseAuth: FirebaseAuth,
                   private val cryptoManager:CryptoManager) {
    //region Vars
    private val googleSignInSubject: PublishRelay<Outcome> = PublishRelay.create()
    private lateinit var googleOneTapSignInClient:SignInClient
    private lateinit var googleSignInLauncher1:ActivityResultLauncher<IntentSenderRequest>
    private lateinit var googleSignInLauncher2:ActivityResultLauncher<Intent>
    //endregion

    fun bind(activity: AppCompatActivity){
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

    fun unbind(){
        googleSignInLauncher1.unregister()
        googleSignInLauncher2.unregister()
    }

    fun loginViaEmail(email:String, password:String):Observable<Outcome>
    = cryptoManager.hashPassword(password)
            .zipWith(Observable.just(email), Pair<String,String>::new)
            .flatMap{ loginWithFirebaseAuth(it) }
            .returnOnError {}

    private fun loginWithFirebaseAuth(params:Pair<String,String>):Observable<Outcome>
    = Observable.create { emitter ->
        firebaseAuth.signInWithEmailAndPassword(it.second, it.first)
            .addOnCompleteListener {
                if (it.isSuccessful)
                    emitter.onNext()
                else{
                   Timber.e("error occurred: ${it.exception?.message}")
                    emitter.onNext()
                }

                emitter.onComplete()
            }
    }

    fun loginViaGoogle(context: Context):Observable<Outcome>{
        /* configure google sign in */
        val googleSignInOptions:GoogleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken()
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

        return googleSignInSubject.hide()
    }

    inner class MActivityResultCallback(private val o:PublishRelay<Outcome>)
        : ActivityResultCallback<ActivityResult>{
        override fun onActivityResult(result: ActivityResult?) {
            if(result == null){
                o.accept()
                return
            }

            when(result.resultCode){
                Activity.RESULT_OK ->{
                    var credential:SignInCredential? = null
                    var idToken:String? = null
                    try{
                      credential = googleOneTapSignInClient. getSignInCredentialFromIntent(result.data)
                      idToken = credential.googleIdToken
                    }catch(e:Exception){
                        Timber.e(e, "no idToken")
                        o.accept()
                        return
                    }

                    val firebaseCredential:AuthCredential
                     = GoogleAuthProvider.getCredential(idToken, null)
                    firebaseAuth.signInWithCredential(firebaseCredential)
                        .addOnCompleteListener {
                            o.accept()
                        }
                }
                else -> o.accept()

            }


        }
    }

}