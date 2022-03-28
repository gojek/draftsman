package com.gojek.draftsman.internal.utils

internal fun getColorHex(color: Int): String {
    return String.format("#%06X %d%%", 0xFFFFFF and color, getAlphaPercentage(color))
}

private fun getAlphaPercentage(color: Int): Int {
    return (color shr 24 and 0xff) * 100 / 255
}