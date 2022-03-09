package com.gojek.draftsman.internal.utils

import android.graphics.RectF

internal fun isAllSame(list: List<RectF>): Boolean {
    var isAllSame = true
    for (index in 0 until list.lastIndex) {
        isAllSame = list[index] == list[index + 1]
        if (!isAllSame) {
            break
        }
    }
    return isAllSame
}