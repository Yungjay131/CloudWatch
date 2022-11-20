package app.slyworks.ui_commons_lib.custom_views

import android.content.Context
import android.content.res.TypedArray
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import app.slyworks.cloudwatch.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject


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
       private val INPUT_TYPE_PASSWORD = 1
       private val INPUT_TYPE_EMAIL = 2
       private val INPUT_TYPE_PHONE = 4
       private val INPUT_TYPE_NAME = 8
       private var inputType:Int = INPUT_TYPE_NAME
       private var hint:String
       private var view:ConstraintLayout
       private var til:TextInputLayout
       private var et:TextInputEditText
       private var textWatcher:TextWatcher? = null
       //endregion

       init{
           /* TODO:check if its password type and set the icon accordingly */
           val a:TypedArray =
           context.getTheme().obtainStyledAttributes(attrs,R.styleable.CustomTextInputLayout,0,0)

           try{
               inputType = a.getInteger(R.styleable.CustomTextInputLayout_ctil_input_type, INPUT_TYPE_NAME)
               hint = a.getString(R.styleable.CustomTextInputLayout_ctil_hint) ?: ""
           }finally {
               a.recycle()
           }

           view = inflate(context,R.layout.layout_custom_text_input_layout,this) as ConstraintLayout
           til = view.findViewById(R.id.til)
           et = view.findViewById(R.id.et)

           /* TODO:add IME actions */
           til.setHint(hint)
           et.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
       }

       fun clear():Unit? = et.text?.clear()
       fun getText():String = et.text.toString().trim()
       fun setText(text:String):Unit = et.setText(text)
       fun observeTextChanges(): Observable<String>{
           val p:PublishSubject<String> = PublishSubject.create()

           textWatcher = object : TextWatcher{
               override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
               override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
               override fun afterTextChanged(p0: Editable?) = p.onNext(p0.toString())
           }

           et.addTextChangedListener(textWatcher!!)
           return p;
       }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        textWatcher?.let {  et.removeTextChangedListener(it) }
    }
}