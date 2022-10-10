package app.slyworks.utils

import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable


/**
 * Created by Joshua Sylvanus, 2:19 PM, 09-Oct-22.
 */

operator fun CompositeDisposable.plusAssign(d: Disposable){ add(d) }