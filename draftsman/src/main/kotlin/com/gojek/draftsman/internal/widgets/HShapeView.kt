package com.gojek.draftsman.internal.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Path
import android.graphics.RectF
import com.gojek.draftsman.internal.utils.DimensionConverter

internal class HShapeView(context: Context) {

    private val length = DimensionConverter.getPxOfDp(16f, context)

    private val paint = Paint(ANTI_ALIAS_FLAG).apply {
        strokeWidth = DimensionConverter.getPxOfDp(1f, context)
        style = Paint.Style.STROKE
    }

    fun draw(
        color: Int,
        rectF: RectF,
        isVertical: Boolean,
        canvas: Canvas
    ) {
        paint.color = color
        canvas.drawPath(
            getPath(
                rectF,
                isVertical
            ),
            paint
        )
    }

    private fun getPath(
        rectF: RectF,
        isVertical: Boolean
    ) = if (isVertical) getVerticalH(rectF)
    else getHorizontalH(rectF)

    private fun getVerticalH(rectF: RectF): Path {
        val halfStroke = paint.strokeWidth.div(2)
        val path = Path()
        path.apply {
            moveTo(
                rectF.centerX() - length.div(2),
                rectF.bottom - halfStroke
            )
            rLineTo(length, 0f)
            moveTo(
                rectF.centerX(),
                rectF.bottom - halfStroke
            )
            rLineTo(0f, -(rectF.height() - halfStroke))
            rMoveTo(-length.div(2f), 0f)
            rLineTo(length, 0f)
        }
        return path
    }

    private fun getHorizontalH(rectF: RectF): Path {
        val halfStroke = paint.strokeWidth.div(2)
        val path = Path()
        path.apply {
            moveTo(
                rectF.left + halfStroke,
                rectF.centerY() - length.div(2)
            )
            rLineTo(0f, length)
            moveTo(
                rectF.left + halfStroke,
                rectF.centerY()
            )
            rLineTo(rectF.width() - halfStroke, 0f)
            rMoveTo(0f, -length.div(2f))
            rLineTo(0f, length)
        }
        return path
    }
}