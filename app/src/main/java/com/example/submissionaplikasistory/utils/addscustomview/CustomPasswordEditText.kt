package com.example.submissionaplikasistory.utils.addscustomview

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.example.submissionaplikasistory.R
import com.google.android.material.textfield.TextInputEditText

class CustomPasswordEditText(context: Context, attr: AttributeSet?) : TextInputEditText(context, attr) {

    init {
        addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length < 8) {
                    setError(ContextCompat.getString(context, R.string.error_edit_text_password), null)
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }


}