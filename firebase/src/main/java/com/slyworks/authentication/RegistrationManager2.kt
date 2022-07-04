package com.slyworks.authentication

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.slyworks.models.Outcome
import com.slyworks.models.TempUser
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single


/**
 *Created by Joshua Sylvanus, 3:31 PM, 02-Jul-22.
 */
class RegistrationManager2(private val firebaseAuth:FirebaseAuth,
                           private val firebaseDatabase: FirebaseDatabase) {

    fun register(user: TempUser):Observable<Outcome> =
        createUser(user.IDToken, user.password)
            .flatMap {
                if(it.isSuccess){
                    val r:AuthResult = it.getTypedValue<AuthResult>()
                    uploadUserDetails(r.user!!.uid, user)
                }else
                    Observable.just(it)
            }
    private fun createUser(email:String, password:String): Observable<Outcome> =
        Observable.create { emitter ->
            firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener {
                    if(it.isSuccessful)
                        emitter.onNext(Outcome.SUCCESS<AuthResult>(it.result))
                    else
                        emitter.onNext(Outcome.FAILURE(it.exception?.message))

                    emitter.onComplete()
                }
        }

    private fun uploadUserDetails(UID:String, user:TempUser):Observable<Outcome> =
        Observable.create { emitter ->
            firebaseDatabase.reference
                .child("/cloud_watch/users/$UID/details")
                .setValue(user)
                .addOnCompleteListener {
                    if(it.isSuccessful)
                        emitter.onNext(Outcome.SUCCESS<Nothing>())
                    else
                        emitter.onNext(Outcome.FAILURE(it.exception?.message))

                    emitter.onComplete()
                }
        }
}