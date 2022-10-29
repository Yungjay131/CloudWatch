package app.slyworks.mvi

import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.observables.ConnectableObservable
import timber.log.Timber


/**
 *Created by Joshua Sylvanus, 2:15 AM, 28-Oct-22.
 */
interface ModelStore2<S> {
    var initialState:S
    private val intents: PublishRelay<Intent<S>>
        get() = PublishRelay.create<Intent<S>>()
    private val store: ConnectableObservable<S>
        get() = intents.observeOn(AndroidSchedulers.mainThread())
                       .scan(initialState){ oldState:S, intent:Intent<S> ->
                           intent.reduce(oldState)
                       }
                       .replay()
                       .apply { connect() }

   private val internalDisposable: Disposable
      get() = store.subscribe(::handleInternalLogging, ::handleCrash)

    private fun handleInternalLogging(state:S):Unit = Timber.e("$state")
    private fun handleCrash(t:Throwable):Unit = throw t

    fun process(intent: Intent<S>):Unit = intents.accept(intent)
    fun getModelState(): Observable<S> = store
}