package app.slyworks.cloudwatch.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import app.slyworks.cloudwatch.databinding.ActivitySplashBinding
import app.slyworks.navigator.Navigator
import app.slyworks.utils.plusAssign
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit

class SplashActivity : AppCompatActivity() {
    //region Vars
    private lateinit var splashActivityBinding:ActivitySplashBinding
    private val disposables:CompositeDisposable = CompositeDisposable()
    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashActivityBinding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(splashActivityBinding.root)
    }

    override fun onResume() {
        super.onResume()
        startActivityNavigation()
    }

    override fun onPause() {
        super.onPause()
        pauseActivityNavigation()
    }

    private fun startActivityNavigation() {
         disposables +=
         Observable.interval(2_000, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError {
                Timber.e("error occurred in interval: ${it.message}")
                Navigator.intentFor<MainActivity>(this)
                    .newAndClearTask()
                    .navigate()
            }
            .subscribe {
                Navigator.intentFor<MainActivity>(this)
                    .newAndClearTask()
                    .navigate()
            }
    }

    private fun pauseActivityNavigation():Unit = disposables.clear()
}