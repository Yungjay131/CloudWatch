package app.slyworks.auth

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import app.slyworks.models.Outcome
import io.reactivex.rxjava3.core.Observable

interface LoginRepository {
    fun bind(activity:AppCompatActivity)
    fun unbind()
    fun loginViaEmail(email:String, password:String):Observable<Outcome>
    fun loginViaGoogle(context: Context):Observable<Outcome>
}
