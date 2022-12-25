package app.slyworks.splash_feature.di

import app.slyworks.splash_feature.SplashActivity
import app.slyworks.ui_commons_lib.di.UICommonsModule
import app.slyworks.utils.di.UtilsModule
import dagger.Component


/**
 * Created by Joshua Sylvanus, 3:32 PM, 01-Dec-22.
 */
@Component(modules = [
    UICommonsModule::class,
    UtilsModule::class
])
interface SplashComponent {

    fun injectActivity(activity: SplashActivity)

}