package app.slyworks.auth

import app.slyworks.models.Outcome
import io.reactivex.rxjava3.core.Observable

interface RegistrationRepository {
  fun registerViaEmail(email:String, password:String): Observable<Outcome>
}
