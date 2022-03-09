package com.gojek.draftsman.internal

import android.graphics.Color

internal object Config {

    var usePx = true

    var paddingEnabled = true

    var marginEnabled = true

    var gridEnabled = false

    val boundLineColor = Color.parseColor("#311B92")

    val selectedBoundColor = Color.parseColor("#2962FF")

    val marginColor = Color.parseColor("#cd0000")

    val paddingColor = Color.parseColor("#6676FF03")

    val gridColor = Color.parseColor("#777777")

    fun reset() {
        usePx = true
        paddingEnabled = true
        marginEnabled = true
        gridEnabled = false
    }
}