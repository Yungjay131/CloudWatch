package com.slyworks.authentication

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.slyworks.models.Outcome
import com.slyworks.models.TempUser
import io.reactivex.rxjava3.core.Single


object RegistrationManager {
    //region Vars
    private val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val mFirebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
    private val mFirebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    //endregion

    fun register(user: TempUser):Single<Outcome> =
        createUser(user.IDToken, user.password)
            .flatMap {
                if (it.isSuccess) {
                    val r:AuthResult = it.getValue() as AuthResult
                    uploadUserDetails(r.user!!.uid, user)
                }else
                    Single.just(it)
            }

    private fun createUser(email:String, password:String):Single<Outcome> =
        Single.create<Outcome> { emitter ->
            mFirebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener {
                    if(it.isSuccessful)
                        emitter.onSuccess(Outcome.SUCCESS<AuthResult>(it.result))
                    else
                        emitter.onError(it.exception)
                }
        }

    private fun uploadUserDetails(uid:String, user: TempUser):Single<Outcome> =
        Single.create{emitter ->
            mFirebaseDatabase.reference
                .child("cloud_watch")
                .child("users")
                .child(uid)
                .child("details")
                .setValue(user)
                .addOnCompleteListener {
                    if(it.isSuccessful)
                        emitter.onSuccess(Outcome.SUCCESS(null))
                    else
                        emitter.onError(it.exception)
                }
        }

}