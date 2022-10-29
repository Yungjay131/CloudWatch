package app.slyworks.cloudwatch.views.auth_activity

import android.content.Context
import app.slyworks.models.Outcome
import io.reactivex.rxjava3.core.Observable


/**
 * Created by Joshua Sylvanus, 4:22 AM, 27-Oct-22.
 */

data class AuthState(val viewState: ViewState,
                     val currentTask: AuthTask? = null)

sealed class AuthTask(val ongoingOperation: Observable<Outcome>,
                      var result: Outcome){
    data class GoogleLogin(val ongoingOp: Observable<Outcome>,
                           var res: Outcome): AuthTask(ongoingOp, res)
    data class EmailLogin(val ongoingOp: Observable<Outcome>,
                          var res: Outcome): AuthTask(ongoingOp, res)
}

sealed class ViewState{
    object SUCCESS : ViewState()
    object LOADING : ViewState()
    object FAILURE : ViewState()
}

sealed class AuthViewEvent{
    data class LoginButtonClicked(val email: String, val password: String) : AuthViewEvent()
    data class LoginViaGoogleButtonClicked(val context: Context) : AuthViewEvent()
}