package app.slyworks.cloudwatch.mvi

import io.reactivex.rxjava3.core.Observable


/**
 *Created by Joshua Sylvanus, 9:21 PM, 22/10/2022.
 */
interface Model<S> {
    /**
     * Model will receive intents to be processed via this function.
     *
     * ModelState is immutable. Processed intents will work much like `copy()`
     * and create a new (modified) modelState from an old one.
     */
    fun process(intent:Intent<S>)

    /**
     * Observable stream of changes to ModelState
     *
     * Every time a modelState is replaced by a new one, this observable will
     * fire.
     *
     * This is what views will subscribe to.
     */
    fun modelState(): Observable<S>
}