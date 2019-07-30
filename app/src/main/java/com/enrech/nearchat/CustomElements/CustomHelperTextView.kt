package com.enrech.nearchat.CustomElements

import android.content.Context
import android.util.AttributeSet
import android.view.View

class CustomHelperTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0) : androidx.appcompat.widget.AppCompatTextView(context, attrs, defStyleAttr)  {

    init {
        this.visibility = View.GONE
    }
}