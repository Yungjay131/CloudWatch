package app.slyworks.crypto.di

import app.slyworks.crypto.CryptoConfig
import app.slyworks.crypto.CryptoHelper
import dagger.Module
import dagger.Provides


/**
 * Created by Joshua Sylvanus, 3:14 PM, 01-Dec-22.
 */

@Module
class CryptoModule(private val config:CryptoConfig) {
    @Provides
    @ApplicationScope
    fun provideCryptoHelper():CryptoHelper =
        CryptoHelper(config)
}