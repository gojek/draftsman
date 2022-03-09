package com.gojek.draftsman.internal.visualizers

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.view.View
import android.view.ViewGroup
import com.gojek.draftsman.internal.Config
import com.gojek.draftsman.internal.utils.buildDimenString
import com.gojek.draftsman.internal.visualizers.TextVision.ORIENTATION_UNKNOWN
import com.gojek.draftsman.internal.widgets.HShapeView

internal object MarginVisualizer {

    fun draw(
        canvas: Canvas,
        view: View,
        locationRectF: RectF
    ) {
        val layoutParams = view.layoutParams
        if (layoutParams is ViewGroup.MarginLayoutParams) {
            val marginRectF = RectF()
            if (layoutParams.leftMargin > 0) {
                marginRectF.set(
                    locationRectF.left.minus(
                        layoutParams.leftMargin
                    ),
                    locationRectF.top,
                    locationRectF.left,
                    locationRectF.bottom
                )

                drawValues(
                    canvas,
                    view.context,
                    layoutParams.leftMargin,
                    marginRectF
                )
            }

            if (layoutParams.rightMargin > 0) {
                marginRectF.set(
                    locationRectF.right,
                    locationRectF.top,
                    locationRectF.right.plus(
                        layoutParams.rightMargin
                    ),
                    locationRectF.bottom
                )

                drawValues(
                    canvas,
                    view.context,
                    layoutParams.rightMargin,
                    marginRectF
                )
            }

            if (layoutParams.topMargin > 0) {
                marginRectF.set(
                    locationRectF.left,
                    locationRectF.top.minus(
                        layoutParams.topMargin
                    ),
                    locationRectF.right,
                    locationRectF.top
                )

                drawValues(
                    canvas,
                    view.context,
                    layoutParams.topMargin,
                    marginRectF
                )
            }

            if (layoutParams.bottomMargin > 0) {
                marginRectF.set(
                    locationRectF.left,
                    locationRectF.bottom,
                    locationRectF.right,
                    locationRectF.bottom.plus(
                        layoutParams.bottomMargin
                    )
                )

                drawValues(
                    canvas,
                    view.context,
                    layoutParams.bottomMargin,
                    marginRectF
                )
            }
        }
    }

    private fun drawValues(
        canvas: Canvas,
        context: Context,
        margin: Int,
        marginRectF: RectF
    ) {
        val text = buildDimenString(
            margin,
            context
        )
        val orientation =
            TextVision.measureText(
                context,
                text,
                marginRectF
            )

        HShapeView(context).draw(
            Config.marginColor,
            marginRectF,
            margin == marginRectF.height().toInt(),
            canvas
        )

        if (orientation != ORIENTATION_UNKNOWN) {
            TextVision.drawText(
                canvas,
                text,
                orientation,
                marginRectF
            )
        }
    }
}