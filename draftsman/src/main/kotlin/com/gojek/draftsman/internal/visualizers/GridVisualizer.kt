package com.gojek.draftsman.internal.visualizers

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.view.View
import com.gojek.draftsman.internal.Config
import com.gojek.draftsman.internal.utils.DimensionConverter

internal class GridVisualizer(context: Context) : View(context) {

    private val paint = Paint(ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 1f
        color = Config.gridColor
    }

    private var gridSize = DimensionConverter.getPxOfDp(2f, context).toInt()

    fun updateGridSize(newSize: Int) {
        val newSizePx = DimensionConverter.getPxOfDp(newSize.toFloat(), context).toInt()
        if (gridSize != newSizePx) {
            gridSize = newSizePx
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        val heightFloat = height.toFloat()
        var indexFloat: Float
        for (index in 0..width step gridSize) {
            indexFloat = index.toFloat()
            canvas.drawLine(
                indexFloat,
                0f,
                indexFloat,
                heightFloat,
                paint
            )
        }
        val widthFloat = width.toFloat()
        for (index in 0..height step gridSize) {
            indexFloat = index.toFloat()
            canvas.drawLine(
                0f,
                indexFloat,
                widthFloat,
                indexFloat,
                paint
            )
        }
    }
}