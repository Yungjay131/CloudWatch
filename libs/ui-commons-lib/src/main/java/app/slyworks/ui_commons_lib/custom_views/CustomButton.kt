package app.slyworks.ui_commons_lib.custom_views

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import app.slyworks.cloudwatch.R


/**
 *Created by Joshua Sylvanus, 6:08 AM, 30-Oct-22.
 */
class CustomButton
    @JvmOverloads
    constructor(context: Context,
                attrs: AttributeSet? = null,
                defStyleAttr: Int = 0,
                defStyleRes: Int = 0): ConstraintLayout(context, attrs, defStyleAttr, defStyleRes){
    private var view:ConstraintLayout
    private lateinit var btn:Button
    private lateinit var progress:ProgressBar
    private var btnText:String = ""

    init {
        var text: String = ""
        val isBtnEnabled: Boolean

        val a: TypedArray =
            context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomButton, 0, 0)

        try {
            text = a.getString(R.styleable.CustomButton_cb_text) ?: ""
            isBtnEnabled = a.getBoolean(R.styleable.CustomButton_cb_enabled, true)
        } finally {
            a.recycle()
        }

        view = LayoutInflater.from(context)
            .inflate(R.layout.layout_custom_button, this) as ConstraintLayout

        btn = view.findViewById(R.id.btn)
        progress = view.findViewById(R.id.progress)

        btn.setText(text)
        btn.setEnabled(isBtnEnabled)
    }

    fun setLoadingStatus(status: Boolean) {
        if (status) {
            btnText = btn.text.trim().toString()
            btn.text = ""
            progress.visibility = View.VISIBLE
        } else {
            btn.text = btnText
            progress.visibility = View.GONE
        }
    }

    fun setOnClickListener(listener: () -> Unit): Unit =
        btn.setOnClickListener { listener.invoke() }

    fun setEnabledStatus(status: Boolean): Unit = btn.setEnabled(status)
}