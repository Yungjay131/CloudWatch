package app.slyworks.auth.di

import app.slyworks.auth.LoginManager
import app.slyworks.auth.interfaces.LoginRepository
import app.slyworks.auth.RegistrationManager
import app.slyworks.auth.interfaces.RegistrationRepository
import app.slyworks.crypto.CryptoHelper
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides


/**
 * Created by Joshua Sylvanus, 3:18 PM, 01-Dec-22.
 */

@Module
object AuthModule {
  @Provides
  @ApplicationScope
  fun provideLoginRepository(firebaseAuth: FirebaseAuth,
                             cryptoHelper: CryptoHelper): LoginRepository =
      LoginManager(firebaseAuth, cryptoHelper)


  @Provides
  @ApplicationScope
  fun provideRegistrationRepository(firebaseAuth: FirebaseAuth,
                                    cryptoHelper: CryptoHelper): RegistrationRepository =
      RegistrationManager(firebaseAuth, cryptoHelper)
}