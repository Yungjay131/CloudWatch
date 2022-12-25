package app.slyworks.utils.di

import android.content.Context
import app.slyworks.utils.PreferenceManager


/**
 * Created by Joshua Sylvanus, 3:29 PM, 01-Dec-22.
 */

@Module
class UtilsModule(private val context: Context) {
    @ApplicationScope
    @Provides
    fun providePreferenceManager(): PreferenceManager =
        PreferenceManager(context)
}