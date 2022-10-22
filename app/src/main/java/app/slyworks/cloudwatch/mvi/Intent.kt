package app.slyworks.cloudwatch.mvi


/**
 *Created by Joshua Sylvanus, 9:24 PM, 22/10/2022.
 */
interface Intent<T> {
    fun reduce(oldState:T):T
}