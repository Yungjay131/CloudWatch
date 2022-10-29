package app.slyworks.cloudwatch

import android.content.Context
import android.content.SharedPreferences
import android.text.Editable
import app.slyworks.utils.KEY_DEFAULT_SHARED_PREFS_NAME
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * Created by Joshua Sylvanus, 8:01 PM, 18/05/2022.
 */
class PreferenceManager(private val context: Context) {
    //region Vars
     val mPrefs:SharedPreferences by lazy(LazyThreadSafetyMode.NONE) {
         context.getSharedPreferences(KEY_DEFAULT_SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        //PreferenceManager.getDefaultSharedPreferences(context)
     }
    //endregion

    private fun SharedPreferences.edit(op:(SharedPreferences.Editor) -> Unit){
        with(this.edit()){
            op(this)
            apply()
        }
    }

    fun set(key:String, value:Any){
        CoroutineScope(Dispatchers.IO).launch {
            with(mPrefs){
                when(value){
                    is Editable -> {
                        if(value.toString().isNotEmpty())
                            edit { it.putString(key, value.toString()) }
                    }
                    is String -> {
                        if(value != "")
                            edit { it.putString(key, value) }
                    }
                    is Int -> edit { it.putInt(key, value) }
                    is Boolean -> edit { it.putBoolean(key, value) }
                    is Float -> edit { it.putFloat(key, value) }
                    is Long -> edit { it.putLong(key, value) }
                    else -> throw UnsupportedOperationException("Unsupported Operation")
                }
            }
        }
    }

    inline fun <reified T> get(key:String, defaultValue:T? = null):T{
        var t:T? = null
        with(mPrefs){
            t = when (T::class) {
                String::class -> getString(key, defaultValue as? String) as T?
                Int::class -> getInt(key, defaultValue as? Int ?: -1) as T?
                Boolean::class -> getBoolean(key, defaultValue as? Boolean ?: false) as T?
                Float::class -> getFloat(key, defaultValue as? Float ?: -1f) as T?
                Long::class -> getLong(key, defaultValue as? Long ?: -1) as T?
                else -> throw UnsupportedOperationException("Unsupported Operation")
            }
        }

        return t!!
    }
}