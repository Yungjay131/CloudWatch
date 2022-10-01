package app.slyworks.auth


/**
 *Created by Joshua Sylvanus, 5:57 PM, 01-Oct-22.
 */
class LoginManager {
    fun loginViaEmail(email:String, password:String):Observable<Outcome>
    fun loginViaGoogle():Observable<Outcome>
    fun loginViaFacebook():Observable<Outcome>
    fun loginViaTwitter():Observable<Outcome>
}