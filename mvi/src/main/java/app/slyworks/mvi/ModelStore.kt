package app.slyworks.mvi

import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.observables.ConnectableObservable
import timber.log.Timber


/**
 *Created by Joshua Sylvanus, 9:20 PM, 22/10/2022.
 */
open class ModelStore<S>(initialState: S) : Model<S> {
    private val intents = PublishRelay.create<Intent<S>>();
    private val store: ConnectableObservable<S> =
        intents
            .observeOn(AndroidSchedulers.mainThread())
            .scan(initialState){ oldState:S, intent: Intent<S> -> intent.reduce(oldState) }
            .replay(1)
            .apply{ connect() }

    /**
     *  Allows me to react to problems within the ModelStore???
     */
    private val internalDisposable: Disposable =
        store.subscribe(::handleInternalLogging, ::handleCrash)

    private fun handleInternalLogging(state:S):Unit = Timber.e("$state")
    private fun handleCrash(t:Throwable):Unit = throw t

    /**
     * Model will receive intents to be processed via this function.
     *
     * ModelState is immutable. Processed intents will work much like `copy()`
     * and create a new (modified) modelState from an old one.
     */
    override fun process(intent: Intent<S>):Unit = intents.accept(intent)

    /**
     * Observable stream of changes to ModelState
     *
     * Every time a modelState is replaced by a new one, this observable will
     * fire.
     *
     * This is what views will subscribe to.
     */
    override fun getModelState(): Observable<S> = store
}