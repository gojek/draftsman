package com.gojek.draftsman

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import com.gojek.draftsman.internal.DraftsmanLayout

class Draftsman {

    private fun installVision(
        activity: Activity,
        view: View
    ) {

        val draftsman = DraftsmanLayout(activity)
        val location = IntArray(2)
        activity.findViewById<ViewGroup>(android.R.id.content).apply {
            addView(draftsman)
            getLocationOnScreen(location)
            draftsman.init(location[1], view) { uninstall(activity) }
        }
    }

    companion object {

        fun install(activity: Activity) {
            val root = activity.findViewById<ViewGroup>(android.R.id.content)
            val content = root.getChildAt(0)
            Draftsman().installVision(activity, content)
        }

        fun uninstall(activity: Activity) {
            val root = activity.findViewById<ViewGroup>(android.R.id.content)
            for (index in 0 until root.childCount) {
                val child = root.getChildAt(index)
                if (child is DraftsmanLayout) {
                    root.removeView(child)
                    break
                }
            }
        }
    }
}
