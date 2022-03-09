package com.gojek.draftsman.internal.model

import android.graphics.RectF
import android.view.View

internal data class LayeredView(
    val layer: Int,
    val view: View,
    val position: RectF
)