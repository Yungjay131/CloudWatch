package app.slyworks.ui_commons_lib.di

import app.slyworks.ui_commons_lib.ThemeManager
import app.slyworks.utils.PreferenceManager
import dagger.Module
import dagger.Provides


/**
 * Created by Joshua Sylvanus, 3:27 PM, 01-Dec-22.
 */

@Module
object UICommonsModule {
    @Provides
    @ApplicationScope
    fun provideThemeManager(preferenceManager: PreferenceManager):ThemeManager =
        ThemeManager(preferenceManager)

}