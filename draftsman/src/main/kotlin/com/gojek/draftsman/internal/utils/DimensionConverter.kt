package com.gojek.draftsman.internal.utils

import android.content.Context
import android.util.SparseArray
import android.util.TypedValue

internal object DimensionConverter {

    private val dpStore = SparseArray<Float>()

    private val spStore = SparseArray<Float>()

    fun getDpOf(px: Int, context: Context): Float {
        if (dpStore.indexOfKey(px) < 0) {
            dpStore.put(
                px, px.div(
                    context.resources.displayMetrics.density
                )
            )
        }

        return dpStore[px]
    }

    fun getSpOf(px: Int, context: Context): Float {
        if (spStore.indexOfKey(px) < 0) {
            spStore.put(
                px, px.div(
                    context.resources.displayMetrics.scaledDensity
                )
            )
        }

        return spStore[px]
    }

    fun getPxOfSp(sp: Float, context: Context): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            sp,
            context.resources.displayMetrics
        )
    }

    fun getPxOfDp(dp: Float, context: Context): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        )
    }
}