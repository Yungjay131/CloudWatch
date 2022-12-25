package app.slyworks.auth.interfaces

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import app.slyworks.models.Outcome
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

interface LoginRepository {
    fun bind(activity:AppCompatActivity)
    fun unbind()
    fun loginViaPhoneNumber(phoneNumber: String):Pair<PublishSubject<String>, Observable<Outcome>>
    fun loginViaEmail(email:String, password:String):Observable<Outcome>
    fun loginViaGoogle(context: Context):Observable<Outcome>
}
