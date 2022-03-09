package com.gojek.draftsman.internal.utils

internal fun getColorHex(color: Int): String {
    return java.lang.String.format("#%06X", 0xFFFFFF and color)
}