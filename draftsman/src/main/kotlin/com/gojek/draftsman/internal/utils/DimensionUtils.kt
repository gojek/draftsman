package com.gojek.draftsman.internal.utils

import android.content.Context
import com.gojek.draftsman.internal.Config

internal fun buildDimenString(
    dimension: Int,
    context: Context
) = if (Config.usePx) "$dimension px"
else
    "${
        DimensionConverter.getDpOf(
            dimension,
            context
        ).toInt()
    } dp"

internal fun buildTextSizeString(
    dimension: Float,
    context: Context
) = if (Config.usePx) "$dimension px"
else
    "${
        DimensionConverter.getSpOf(
            dimension.toInt(),
            context
        ).toInt()
    } sp"

internal fun buildGridSize(size: Int) = "${size}x${size}dp"
