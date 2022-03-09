package com.gojek.draftsman.internal.utils

import android.graphics.RectF
import android.view.View
import android.view.ViewGroup
import com.gojek.draftsman.internal.model.LayeredView

internal class LayeredViewBuilder(
    private val topInset: Int
) {

    private var locationRectF = RectF()

    private val location = IntArray(2)

    private val layeredViews = mutableListOf<LayeredView>()

    fun buildLayeredView(
        view: View
    ): List<LayeredView> {
        layeredViews.clear()
        locateViews(view, 0)
        return layeredViews
    }

    private fun locateViews(
        view: View,
        layer: Int
    ) {
        if (view is ViewGroup) {
            locateViewGroup(view, layer)
        } else {
            if (view.visibility == View.VISIBLE) {
                locate(view, layer)
            }
        }
    }

    private fun locateViewGroup(
        viewGroup: ViewGroup,
        layer: Int
    ) {
        locate(viewGroup, layer)
        for (index in 0 until viewGroup.childCount) {
            locateViews(viewGroup.getChildAt(index), layer.plus(1))
        }
    }

    private fun locate(
        view: View,
        layer: Int
    ) {
        view.getLocationOnScreen(location)
        val left = location[0].toFloat()
        val top = location[1].minus(topInset).toFloat()
        locationRectF = RectF(
            left,
            top,
            left.plus(view.width),
            top.plus(view.height)
        )

        layeredViews.add(
            LayeredView(
                layer,
                view,
                locationRectF
            )
        )
    }
}