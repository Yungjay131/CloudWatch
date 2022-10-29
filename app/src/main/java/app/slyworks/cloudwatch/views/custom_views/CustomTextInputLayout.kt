package app.slyworks.cloudwatch.views.custom_views

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


/**
 * Created by Joshua Sylvanus, 3:54 AM, 25-Oct-22.
 */
class CustomTextInputLayout
   @JvmOverloads
   constructor(context:Context,
               attrs:AttributeSet? = null,
               defStyle:Int = 0,
               defStyleAttrs:Int = 0): ConstraintLayout(context, attrs, defStyle, defStyleAttrs) {
       //region Vars
       private lateinit var view:ConstraintLayout
       private lateinit var til:TextInputLayout
       private lateinit var et:TextInputEditText
       //endregion

       init{
           /* TODO:check if its password type and set the icon accordingly */
       }

       fun clear():Unit? = et.text?.clear()
       fun getText():String = et.text.toString().trim()
       fun setText(text:String):Unit = et.setText(text)
}