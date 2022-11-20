package app.slyworks.ui_commons_lib

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar


/**
 * Created by Joshua Sylvanus, 9:02 PM, 09/11/2022.
 */

    fun View.setChildViewsStatus(status: Boolean) {
        isEnabled = status

        if (this is ViewGroup) {
            val viewGroup: ViewGroup? = this as? ViewGroup

            for (i in 0 until getChildCount()) {
                //recursively searching the child view, if its a parent too
                val child: View = getChildAt(i)
                child.setChildViewsStatus(status)
            }
    }

    fun Activity.closeKeyboard() {
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    fun Activity.closeKeyboard2() {
        //to show soft keyboard
        val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    fun Activity.closeKeyboard3() {
        val inputManager = getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(currentFocus?.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS)
    }

    fun Activity.closeKeyboard4(rootView: View) {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(rootView.windowToken, 0)
    }

    fun displayMessage(message:String, view:View):Unit =
       Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()

}