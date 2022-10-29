package app.slyworks.cloudwatch.views.auth_activity

import android.view.View
import androidx.lifecycle.MutableLiveData
import app.slyworks.auth.LoginManager
import app.slyworks.models.Outcome
import app.slyworks.mvi.Intent
import app.slyworks.mvi.ModelStore
import app.slyworks.mvi.ModelStore2
import app.slyworks.mvi.intent
import app.slyworks.utils.plusAssign
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.lang.UnsupportedOperationException
import javax.inject.Inject

fun <T> MutableLiveData<T>.updateLoginStatus(newValue:T):Unit{

}

class AuthViewModel
@Inject
constructor(private val loginManager: LoginManager) : ModelStore2<AuthState> {

    private val loginLiveData: MutableLiveData<Boolean> = MutableLiveData(false)
    private
    private val disposables:CompositeDisposable = CompositeDisposable()

    override var initialState: AuthState = AuthState(ViewState.LOADING)

    init {
        disposables +=
        getModelState()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe { state:AuthState ->
                when(state.viewState){
                    is ViewState.LOADING -> {
                       state.currentTask!!
                           .ongoingOperation
                           .subscribe{

                           }
                    }
                    is ViewState.SUCCESS -> {
                       when(state.currentTask){
                           is AuthTask.EmailLogin -> {}
                           is AuthTask.GoogleLogin -> {}
                       }
                    }
                    is ViewState.FAILURE -> {
                        when(state.currentTask){
                            is AuthTask.EmailLogin -> {}
                            is AuthTask.GoogleLogin -> {}
                        }
                    }
                }
            }
    }

    override fun getModelState(): Observable<AuthState> = super.getModelState()

    override fun process(intent: Intent<AuthState>):Unit = super.process(intent)

    fun processViewEvent(viewEvent:AuthViewEvent):Unit = process(mapViewEventToIntent(viewEvent))

    private fun mapViewEventToIntent(viewEvent:AuthViewEvent):Intent<AuthState>{
        return when(viewEvent){
            is AuthViewEvent.LoginButtonClicked -> buildLoginIntent(viewEvent)
            is AuthViewEvent.LoginViaGoogleButtonClicked -> buildLoginViaGoogleIntent(viewEvent)
            else -> throw UnsupportedOperationException()
        }
    }

    private fun buildLoginIntent(viewEvent:AuthViewEvent.LoginButtonClicked)
    :Intent<AuthState> =
        object : Intent<AuthState>{
            override fun reduce(oldState: AuthState): AuthState {
                val o:Observable<Outcome> = loginManager.loginViaEmail(viewEvent.email, viewEvent.password)
                return oldState.copy(viewState = ViewState.LOADING,
                                     currentTask = AuthTask.EmailLogin(o))
            }
        }

    private fun buildLoginViaGoogleIntent(viewEvent: AuthViewEvent.LoginViaGoogleButtonClicked)
    :Intent<AuthState> =
        object : Intent<AuthState> {
            override fun reduce(oldState: AuthState): AuthState {
                val o:Observable<Outcome> = loginManager.loginViaGoogle(viewEvent.context)
                return oldState.copy(viewState = ViewState.LOADING,
                                     currentTask = AuthTask.GoogleLogin(o))
            }
        }


}
