package app.slyworks.crypto

import java.security.SecureRandom
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.SecretKey

interface CryptoConfig {
     val encryptionAlgorithmShort:String
     val encryptionAlgorithm:String
     val encryptionKey: SecretKey
     val encryptionKeySize:Int
     val encryptionIV:String
     val encryptionSpec:AlgorithmParameterSpec
     val hashSalt:ByteArray
     val hashAlgorithm:String
     val hashIterationCount:Int
     val hashLength:Int
}
