package com.semyon.keyholder

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.view_floating_text_view.view.hint_text
import kotlinx.android.synthetic.main.view_floating_text_view.view.input

class FloatingTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var expanded = false

    var hint: Hint = Hint("")
        set(value) {
            field = value
            if (value.hasOptional()) {
                val span = SpannableString(value.toString())
                span.setSpan(ForegroundColorSpan(
                    ContextCompat.getColor(context, R.color.test)),
                    value.text.length + 1, value.text.length + (value.optional?.length ?: 0) + 2,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                hint_text.text = span
            } else {
                hint_text.text = value.toString()
            }
        }

    init {
        View.inflate(context, R.layout.view_floating_text_view, this)

        hint_text.setOnClickListener {
            TransitionManager.beginDelayedTransition(this, AutoTransition().apply { duration = 200L })
            expanded = true
            input.visibility = View.VISIBLE
            input.requestFocus()
        }

        input.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                hint_text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
                hint_text.setTextColor(ContextCompat.getColor(context, R.color.test2))
            } else {
                if (input.text.isNullOrEmpty()) {
                    hint_text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                    input.visibility = View.GONE
                }
            }
        }
    }


}

data class Hint(
    val text: String,
    val optional: String? = null
) {
    fun hasOptional() = !optional.isNullOrEmpty()

    override fun toString() = if (hasOptional()) {
        "$text, $optional"
    } else {
        text
    }
}