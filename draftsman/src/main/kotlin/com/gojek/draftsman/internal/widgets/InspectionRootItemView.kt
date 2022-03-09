package com.gojek.draftsman.internal.widgets

import android.content.Context
import android.graphics.Color
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
import android.widget.TextView
import com.gojek.draftsman.internal.utils.DimensionConverter

internal class InspectionRootItemView(context: Context) : LinearLayout(context) {

    init {
        val padding = DimensionConverter.getPxOfDp(8f, context).toInt()
        setPadding(padding, padding, padding, padding)
        val textView = TextView(context).apply {
            layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            textSize = 18f
            setTextColor(Color.parseColor("#555555"))
            setPadding(DimensionConverter.getPxOfDp(16f, context).toInt(), 0, 0, 0)
        }
        addView(textView)
    }

    fun setText(text: String) {
        (getChildAt(0) as TextView).text = text
    }
}