package app.slyworks.crypto

import io.reactivex.rxjava3.core.Observable


/**
 * Created by Joshua Sylvanus, 7:04 AM, 08-Oct-22.
 */
class CryptoManager(private val conf:CryptoConf) {
    fun hash(str:String): Observable<String>
    = Observable.fromCallable {""}

    fun confirmHash(str:String):Observable<String>
    = Observable.fromCallable {""}

    fun encrypt(str:String):Observable<String>
    = Observable.fromCallable {""}

    fun decrypt(str:String):Observable<String>
    = Observable.fromCallable {""}
}