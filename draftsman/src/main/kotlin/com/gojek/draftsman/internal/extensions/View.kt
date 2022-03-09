package com.gojek.draftsman.internal.extensions

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE

internal fun View.gone() {
    visibility = GONE
}

internal fun View.visible() {
    visibility = VISIBLE
}