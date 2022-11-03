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
import app.slyworks.crypto.CryptoManager
import app.slyworks.models.Outcome
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
                   private val cryptoManager: CryptoManager): LoginRepository {
    //region Vars
    private val googleSignInSubject: PublishRelay<Outcome> = PublishRelay.create()
    private lateinit var googleOneTapSignInClient:SignInClient
    private lateinit var googleSignInLauncher1:ActivityResultLauncher<IntentSenderRequest>
    private lateinit var googleSignInLauncher2:ActivityResultLauncher<Intent>
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

    override fun loginViaEmail(email:String, password:String):Observable<Outcome>
    = Observable.just(email)
            .zipWith(cryptoManager.hash(password), ::Pair)
            .flatMap{ it:Pair<String,String> -> loginWithFirebaseAuth(it) }
            .onErrorReturn {
                Timber.e("error occurred: ${it.message}")
                Outcome.ERROR<Boolean>(false, additionalInfo = it.message)
            }

    private fun loginWithFirebaseAuth(params:Pair<String,String>):Observable<Outcome>
    = Observable.create { emitter ->
        firebaseAuth.signInWithEmailAndPassword(params.second, params.first)
            .addOnCompleteListener {
                if (it.isSuccessful)
                    emitter.onNext(Outcome.SUCCESS<Boolean>(true, "user successfully logged in"))
                else{
                    Timber.e("error occurred: ${it.exception?.message}")
                    emitter.onNext(Outcome.FAILURE<Boolean>(false, it.exception?.message))
                }

                emitter.onComplete()
            }
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