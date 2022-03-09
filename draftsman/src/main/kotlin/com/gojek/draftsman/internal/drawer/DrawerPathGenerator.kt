package com.gojek.draftsman.internal.drawer

import android.content.res.Resources
import android.graphics.Path
import android.graphics.RectF
import com.gojek.draftsman.internal.drawer.Orientation.*

internal class DrawerPathGenerator(
    private val height: Int,
    private val resources: Resources,
    private val sliderShapeDimension: Float,
    private val peekDistance: Float
) {

    fun getPath(orientation: Orientation, childDimension: Int): Pair<Path, RectF> {
        return when (orientation) {
            HORIZONTAL_RIGHT -> HorizontalRightPathGenerator(
                height,
                resources,
                sliderShapeDimension,
                peekDistance
            ).getPath(childDimension)
            HORIZONTAL_LEFT -> HorizontalLeftPathGenerator().getPath()
            VERTICAL_TOP -> VerticalTopPathGenerator().getPath()
            VERTICAL_BOTTOM -> VerticalBottomPathGenerator().getPath()
        }
    }

    private class HorizontalRightPathGenerator(
        private val height: Int,
        private val resources: Resources,
        private val sliderShapeDimension: Float,
        private val peekDistance: Float
    ) {

        fun getPath(childWidth: Int): Pair<Path, RectF> {

            val width = resources.displayMetrics.widthPixels.toFloat()
            val heightHalf = height.div(2f)
            val sliderShapeDimensionHalf = sliderShapeDimension.div(2)

            val rectF = RectF().apply {
                right = width
                left = width - sliderShapeDimensionHalf - peekDistance
                top = heightHalf - sliderShapeDimensionHalf
                bottom = top + sliderShapeDimension
            }

            val yOffset: Float = sliderShapeDimension * 0.05f
            val xOffset = sliderShapeDimensionHalf * 4 / 3

            val path = Path().apply {
                moveTo(sliderShapeDimension, heightHalf - sliderShapeDimensionHalf)
                rCubicTo(
                    -xOffset, yOffset,
                    -xOffset, sliderShapeDimension - yOffset,
                    0f, sliderShapeDimension
                )
                addRect(
                    sliderShapeDimension,
                    0f,
                    childWidth + sliderShapeDimension,
                    height.toFloat(),
                    Path.Direction.CCW
                )
            }

            return Pair(path, rectF)
        }
    }

    private class HorizontalLeftPathGenerator {

        fun getPath(): Pair<Path, RectF> {
            return Pair(Path(), RectF())
        }

    }

    private class VerticalTopPathGenerator {

        fun getPath(): Pair<Path, RectF> {
            return Pair(Path(), RectF())
        }

    }

    private class VerticalBottomPathGenerator {

        fun getPath(): Pair<Path, RectF> {
            return Pair(Path(), RectF())
        }

    }
}