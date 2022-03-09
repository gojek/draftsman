package com.gojek.draftsman.internal.drawer

import android.content.res.Resources
import android.util.TypedValue

internal fun dpToPx(dp: Int, resources: Resources) =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics)