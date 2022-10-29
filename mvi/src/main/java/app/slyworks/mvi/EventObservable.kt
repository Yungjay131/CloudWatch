package app.slyworks.mvi

import io.reactivex.rxjava3.core.Observable


/**
 * Created by Joshua Sylvanus, 9:17 PM, 22/10/2022.
 */
interface EventObservable<T> {
    fun events(): Observable<T>
}