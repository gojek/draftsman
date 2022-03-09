package com.gojek.draftsman.internal.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.MeasureSpec.getSize
import com.gojek.draftsman.internal.model.LayeredView
import com.gojek.draftsman.internal.utils.DimensionConverter
import com.gojek.draftsman.internal.utils.OnLayeredViewSelection
import com.gojek.draftsman.internal.drawer.dpToPx

internal class NestedViewBoundView @JvmOverloads constructor(
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
        strokeWidth = DimensionConverter.getPxOfDp(2f, context)
        color = Color.WHITE
    }

    private val textPaint = Paint(ANTI_ALIAS_FLAG).apply {
        textSize = DimensionConverter.getPxOfSp(14f, context)
        color = Color.WHITE
    }

    private val padding = DimensionConverter.getPxOfDp(32f, context)
    private val textPadding = DimensionConverter.getPxOfDp(8f, context)

    private val layeredViews = mutableListOf<LayeredView>()

    private var viewProcessing: Runnable? = null

    private var numberOfViews = 0

    var layeredViewSelection: OnLayeredViewSelection = {}

    fun showViewBounds(views: List<LayeredView>) {
        viewProcessing = Runnable {
            val strokeHalf = strokePaint.strokeWidth.div(2)
            var spacing: Float
            views.sortedBy { it.layer }
                .forEachIndexed { index, layeredView ->
                    spacing = index.times(padding).plus(strokeHalf)
                    layeredViews.add(
                        LayeredView(
                            layeredView.layer,
                            layeredView.view,
                            RectF(
                                spacing,
                                spacing,
                                width.minus(spacing),
                                height.minus(spacing)
                            )
                        )
                    )
                }
            invalidate()
            viewProcessing = null
        }

        numberOfViews = views.size
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = getSize(widthMeasureSpec)
        var h = dpToPx(220, resources).toInt()
        if (numberOfViews > 2) {
            h += (numberOfViews - 2) * padding.toInt() * 2
        }
        setMeasuredDimension(w, h)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            if (null != viewProcessing) {
                consumeViewProcessing()
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        var name: String
        layeredViews.forEach {
            name = it.view.javaClass.simpleName
            canvas.run {
                drawRect(it.position, strokePaint)
                drawText(
                    name,
                    it.position.left + textPadding,
                    it.position.top + textPaint.textSize + textPadding,
                    textPaint
                )
            }

        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            for (index in layeredViews.lastIndex downTo 0) {
                if (layeredViews[index].position.contains(event.x, event.y)) {
                    layeredViewSelection(layeredViews[index].view)
                    break
                }
            }
        }

        return true
    }

    private fun consumeViewProcessing() {
        post(viewProcessing)
    }
}