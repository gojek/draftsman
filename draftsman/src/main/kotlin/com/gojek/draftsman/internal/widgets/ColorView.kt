package com.gojek.draftsman.internal.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.util.AttributeSet
import android.view.View
import com.gojek.draftsman.internal.utils.DimensionConverter

internal class ColorView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleRes: Int = 0
) : View(
    context,
    attributeSet,
    defStyleRes
) {
    private val strokePaint = Paint(ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = DimensionConverter.getPxOfDp(1f, context)
        color = Color.WHITE
    }

    var color: Int? = null
        set(value) {
            field = value
            invalidate()
        }

    override fun onDraw(canvas: Canvas) {
        color?.let {
            canvas.run {
                drawColor(it)
                drawRect(
                    strokePaint.strokeWidth,
                    strokePaint.strokeWidth,
                    width - strokePaint.strokeWidth,
                    height - strokePaint.strokeWidth,
                    strokePaint
                )
            }
        }
    }
}