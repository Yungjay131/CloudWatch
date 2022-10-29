package app.slyworks.mvi

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable


/**
 *Created by Joshua Sylvanus, 9:18 PM, 22/10/2022.
 */

/**
 * Consumers of a given state source often need to create fine-grained subscriptions
 * to control performance and frequency of updates.
 */
interface StateSubscriber<S> {
  fun Observable<S>.subscribeToState(): Disposable
}