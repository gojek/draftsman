package com.gojek.draftsman.internal.extensions

import android.view.ViewGroup

internal fun ViewGroup.removeAllViewGroups() {
    for (index in 0 until childCount) {
        if (getChildAt(index) is ViewGroup) {
            removeView(getChildAt(index))
        }
    }
}