package app.slyworks.auth

import app.slyworks.auth.interfaces.RegistrationRepository
import app.slyworks.crypto.CryptoHelper
import app.slyworks.models.Outcome
import app.slyworks.utils.onNextAndComplete
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.rxjava3.core.Observable
import timber.log.Timber


/**
 * Created by Joshua Sylvanus, 7:07 AM, 08-Oct-22.
 */
class RegistrationManager(private val firebaseAuth:FirebaseAuth,
                          private val cryptoHelper: CryptoHelper) :
    RegistrationRepository {

    private fun registerEmail(email:String, password:String):Observable<Outcome> =
        Observable.create<Outcome> { emitter ->
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    val r:Outcome
                    if(it.isSuccessful)
                        emitter.onNextAndComplete(Outcome.SUCCESS(value = it.result, additionalInfo = "registration succesful" ))
                    else
                        emitter.onNextAndComplete(Outcome.FAILURE(value = "registration was not successful", reason = it.exception?.message))
                }
            }
            .onErrorReturn {
                Timber.e("error occurred: ${it.message}")
                Outcome.ERROR<Boolean>(false, additionalInfo = it.message)
            }

    override fun registerViaEmail(email:String, password:String):Observable<Outcome> =
        Observable.zip(Observable.just(email), cryptoHelper.hash(password), ::Pair)
           .flatMap { it: Pair<String, String> -> registerEmail(it.first, it.second) }
           .onErrorReturn {
               Timber.e("error occurred: ${it.message}")
               Outcome.ERROR<Boolean>(false, additionalInfo = it.message)
           }
}