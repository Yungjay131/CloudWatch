package app.slyworks.crypto

import android.util.Base64
import io.reactivex.rxjava3.core.Observable
import java.nio.charset.Charset
import java.security.spec.KeySpec
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Created by Joshua Sylvanus, 7:04 AM, 08-Oct-22.
 */
class CryptoHelper(private val config:CryptoConfig) {
    //region Vars
    private val secretKeyFactory:SecretKeyFactory = SecretKeyFactory.getInstance(config.hashAlgorithm)
    private val cipher:Cipher = Cipher.getInstance(config.encryptionAlgorithm)
    //endregion

    fun hash(str:String): Observable<String>
    = Observable.fromCallable {
        val keySpec: KeySpec = PBEKeySpec(str.toCharArray(), config.hashSalt, config.hashIterationCount /*15500 65536*/, config.hashLength /*128*/ )
        val hash:ByteArray = secretKeyFactory.generateSecret(keySpec).getEncoded()
        return@fromCallable String(Base64.encode(hash, Base64.DEFAULT), Charset.forName("UTF-8") ).trim()
    }

    fun encrypt(str:String):Observable<String>
    = Observable.fromCallable {
        val strByteArray:ByteArray = str.toByteArray()
        cipher.init(Cipher.ENCRYPT_MODE, config.encryptionKey, config.encryptionSpec)
        val encryptedText:ByteArray = cipher.doFinal(strByteArray)
        return@fromCallable String(Base64.encode(encryptedText, Base64.DEFAULT), Charset.forName("UTF-8") ).trim()
    }

    fun decrypt(str:String):Observable<String>
    = Observable.fromCallable {
        val strByteArray:ByteArray = Base64.decode(str, Base64.DEFAULT)
        cipher.init(Cipher.DECRYPT_MODE, config.encryptionKey, config.encryptionSpec)
        val decryptedText:ByteArray = cipher.doFinal(strByteArray)
        return@fromCallable String(decryptedText, Charset.forName("UTF-8") ).trim()
    }
}