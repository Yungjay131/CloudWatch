package app.slyworks.crypto

import java.security.SecureRandom

interface CryptoConf {
     val encryptionAlgorithmShort:String
     val encryptionAlgorithm:String
     val encryptionKeySize:Int
     val hashSalt:ByteArray
     val hashAlgorithm:String
     val hashIterationCount:Int
     val hashLength:Int
}
