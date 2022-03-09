package com.gojek.draftsman.internal.visualizers

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.RectF
import android.view.View
import com.gojek.draftsman.internal.Config
import com.gojek.draftsman.internal.utils.buildDimenString
import com.gojek.draftsman.internal.visualizers.TextVision.ORIENTATION_UNKNOWN

internal object PaddingVisualizer {

    private val paddingPaint by lazy {
        Paint(ANTI_ALIAS_FLAG).apply {
            color = Config.paddingColor
        }
    }

    fun draw(
        canvas: Canvas,
        view: View,
        locationRectF: RectF
    ) {
        val paddingRectF = RectF()
        if (view.paddingLeft > 0) {
            paddingRectF.set(
                locationRectF.left,
                locationRectF.top,
                locationRectF.left.plus(view.paddingLeft),
                locationRectF.bottom
            )
            canvas.drawRect(
                paddingRectF,
                paddingPaint
            )

            drawPaddingValues(
                view.context,
                canvas,
                view.paddingLeft,
                paddingRectF
            )
        }

        if (view.paddingRight > 0) {
            paddingRectF.set(
                locationRectF.right.minus(view.paddingRight),
                locationRectF.top,
                locationRectF.right,
                locationRectF.bottom
            )
            canvas.drawRect(
                paddingRectF,
                paddingPaint
            )
            drawPaddingValues(
                view.context,
                canvas,
                view.paddingRight,
                paddingRectF
            )
        }


        if (view.paddingTop > 0) {
            paddingRectF.set(
                locationRectF.left,
                locationRectF.top,
                locationRectF.right,
                locationRectF.top.plus(view.paddingTop)
            )
            canvas.drawRect(
                paddingRectF,
                paddingPaint
            )
            drawPaddingValues(
                view.context,
                canvas,
                view.paddingTop,
                paddingRectF
            )
        }
        if (view.paddingBottom > 0) {
            paddingRectF.set(
                locationRectF.left,
                locationRectF.bottom.minus(view.paddingBottom),
                locationRectF.right,
                locationRectF.bottom

            )
            canvas.drawRect(
                paddingRectF,
                paddingPaint
            )
            drawPaddingValues(
                view.context,
                canvas,
                view.paddingBottom,
                paddingRectF
            )
        }
    }

    private fun drawPaddingValues(
        context: Context,
        canvas: Canvas,
        padding: Int,
        locationRectF: RectF
    ) {
        val text = buildDimenString(
            padding,
            context
        )

        val orientation =
            TextVision.measureText(
                context,
                text,
                locationRectF
            )
        if (orientation != ORIENTATION_UNKNOWN) {
            TextVision.drawText(
                canvas,
                text,
                orientation,
                locationRectF
            )
        }
    }
}