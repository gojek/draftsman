package com.gojek.draftsman.internal.visualizers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.gojek.draftsman.R
import com.gojek.draftsman.internal.model.LayeredView
import com.gojek.draftsman.internal.utils.OnLayeredViewSelection
import com.gojek.draftsman.internal.widgets.NestedViewBoundView

internal class LayeringVisualizer(parent: ViewGroup) {

    private val layout = LayoutInflater.from(parent.context).inflate(
        R.layout.view_layering_layout,
        parent,
        false
    ).also {
        parent.addView(it)
    }

    fun showLayeredViews(views: List<LayeredView>) {
        with(layout) {
            findViewById<TextView>(R.id.nested_view_text).text =
                "There are ${views.size} views with same boundaries here. Select the one you want to inspect"
            findViewById<NestedViewBoundView>(R.id.nested_view_bound_view).showViewBounds(views)
        }
    }

    fun setOnLayeredViewSelectListener(listener: OnLayeredViewSelection) {
        layout.findViewById<NestedViewBoundView>(R.id.nested_view_bound_view).layeredViewSelection =
            listener
    }

    fun setOnDismissListener(listener: View.OnClickListener) {
        layout.findViewById<View>(R.id.close_layering_view).setOnClickListener(listener)
    }

    fun remove(parent: ViewGroup) {
        parent.removeView(layout)
    }
}