package com.gojek.draftsman.internal.visualizers

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import com.gojek.draftsman.internal.utils.DimensionConverter

private const val MAX_TEXT_SIZE = 12f
private const val MIN_TEXT_SIZE = 8f

internal object TextVision {

    private val textPaint by lazy {
        TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            style = Paint.Style.FILL
        }
    }

    fun measureText(
        context: Context,
        text: String,
        locationRectF: RectF
    ): Int {
        textPaint.textSize = DimensionConverter.getPxOfSp(
            MAX_TEXT_SIZE,
            context
        )
        var orientation =
            ORIENTATION_UNKNOWN
        var textWidth: Float
        val minTextSize = DimensionConverter.getPxOfSp(
            MIN_TEXT_SIZE,
            context
        )
        val textBounds = Rect()
        while (textPaint.textSize >= minTextSize
        ) {
            textWidth = textPaint.measureText(text)
            textPaint.getTextBounds(
                text,
                0,
                text.length,
                textBounds
            )
            if (locationRectF.width() > textWidth &&
                locationRectF.height() > textBounds.height()
            ) {
                orientation =
                    ORIENTATION_HORIZONTAL
                break
            } else if (locationRectF.height() > textWidth &&
                locationRectF.width() > textBounds.height()
            ) {
                orientation =
                    ORIENTATION_VERTICAL
                break
            }

            textPaint.textSize -= context.resources.displayMetrics.scaledDensity
        }

        return orientation
    }

    fun drawText(
        canvas: Canvas,
        text: String,
        orientation: Int,
        locationRectF: RectF
    ) {
        val textWidth = textPaint.measureText(text)
        val textBounds = Rect()
        textPaint.getTextBounds(
            text,
            0,
            text.length,
            textBounds
        )
        val textHeight = textBounds.height()

        if (orientation == ORIENTATION_HORIZONTAL) {

            val startOffset = locationRectF.width()
                .minus(textWidth)
                .div(2f)


            canvas.drawText(
                text,
                locationRectF.left.plus(startOffset),
                locationRectF.centerY().plus(
                    textHeight.div(2f)
                ),
                textPaint
            )
        } else {

            val path = Path().apply {
                moveTo(
                    locationRectF.centerX().minus(textHeight.div(2f)),
                    locationRectF.centerY().minus(textWidth.div(2f))
                )
                lineTo(
                    locationRectF.centerX().minus(textHeight.div(2f)),
                    locationRectF.centerY().plus(textWidth.div(2f))
                )
            }

            canvas.drawTextOnPath(
                text,
                path,
                0f,
                0f,
                textPaint
            )
        }
    }

    const val ORIENTATION_HORIZONTAL = 1
    const val ORIENTATION_VERTICAL = 2
    const val ORIENTATION_UNKNOWN = -1
}