package app.slyworks.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Base64.NO_WRAP
import io.reactivex.rxjava3.core.Observable
import java.nio.charset.Charset
import java.security.KeyStore
import java.security.SecureRandom
import java.security.spec.KeySpec
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec


/**
 * Created by Joshua Sylvanus, 7:04 AM, 08-Oct-22.
 */
class CryptoManager(private val conf:CryptoConf) {
    //region Vars
    private val secureRandom:SecureRandom = SecureRandom()
    private val salt:ByteArray = ByteArray(16).also{ secureRandom.nextBytes(it) }
    private val secretKeyFactory:SecretKeyFactory = SecretKeyFactory.getInstance(conf.hashAlgorithm) //"PBKDF2WithHmacSHA1"

    private val keyGen:KeyGenerator = KeyGenerator.getInstance(conf.encryptionAlgorithmShort).also{ it.init(256) } //"AES"

    private val key:SecretKey = keyGen.generateKey()
    private val cipher:Cipher = Cipher.getInstance(conf.encryptionAlgorithm)//"AES/CBC/PKCS5PADDING"
    //endregion

    fun hash(str:String): Observable<String>
    = Observable.fromCallable {
        val keySpec: KeySpec = PBEKeySpec(str.toCharArray(), salt, conf.hashIterationCount /*15500 65536*/, conf.hashLength /*128*/ )
        val hash:ByteArray = secretKeyFactory.generateSecret(keySpec).getEncoded()
        return@fromCallable hash.toString()
    }

    fun encrypt(str:String):Observable<String>
    = Observable.fromCallable {
        val strByteArray:ByteArray = str.toByteArray()
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val encryptedText:ByteArray = cipher.doFinal(strByteArray)
        return@fromCallable String(Base64.encode(encryptedText, Base64.DEFAULT), Charset.forName("UTF-8") )
    }

    fun decrypt(str:String):Observable<String>
    = Observable.fromCallable {
        val strByteArray:ByteArray = str.toByteArray()
        cipher.init(Cipher.DECRYPT_MODE, key)
        val decryptedText:ByteArray = cipher.doFinal(strByteArray)
        return@fromCallable String(Base64.encode(decryptedText, Base64.DEFAULT), Charset.forName("UTF-8") )
    }


    /*
    val cipher:Cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, getSymmetricKey())

        val iv:ByteArray = Base64.getEncoder().encode(cipher.iv, Base64.NO_WRAP).run { toByteArray() }
        val data = Base64.getEncoder().encodeToString(cipher.doFinal(str.toByteArray()), Base64.NO_WRAP).run{ toByteArray() }

    *  val ANDROID_KEYSTORE = ""
        val KEY_ALIAS = ""
        val keyStore:KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        val keyGenerator:KeyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        val keyGenParameterSpec:KeyGenParameterSpec =
        KeyGenParameterSpec.Builder(
             KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
            .setRandomizedEncryptionRequired(true)
            .build()

        keyGenerator.init(keyGenParameterSpec)

        val secretKey = keyGenerator.generateKey()

         private fun doesKeyExist(keyStore: KeyStore):Boolean{
        val aliases = keyStore.aliases()
        while(aliases.hasMoreElements())
            return KEY_ALIAS == aliases.nextElement()

        return false
    }
    * */
}