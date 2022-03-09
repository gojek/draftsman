package com.gojek.draftsman.internal.visualizers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import com.gojek.draftsman.R
import com.gojek.draftsman.internal.extensions.gone
import com.gojek.draftsman.internal.extensions.visible
import com.gojek.draftsman.internal.utils.*
import com.gojek.draftsman.internal.widgets.ColorView

internal class InfoVisualizer(
    parent: ViewGroup,
    exitListener: ExitListener,
    infoDismissListener: InfoDismissListener,
    private val configChange: DimensionConfigChange
) {

    private val layout = LayoutInflater.from(parent.context).inflate(
        R.layout.draftsman_info_layout,
        parent,
        false
    ).also {
        it.findViewById<View>(R.id.exit_draftsman).setOnClickListener {
            exitListener()
        }
        it.findViewById<View>(R.id.close_info_view).setOnClickListener {
            infoDismissListener()
        }
        parent.addView(it, parent.childCount - 1)
    }

    fun updateInfoView(
        view: View
    ) {

        setText(
            R.id.info_widget,
            view.javaClass.name
        )

        setText(
            R.id.info_width,
            "Width ${
                buildDimenString(
                    view.width,
                    view.context
                )
            }"
        )

        setText(
            R.id.info_height,
            "Height ${
                buildDimenString(
                    view.height,
                    view.context
                )
            }"
        )

        if (view is TextView &&
            view.text.isNotBlank()
        ) {
            setText(
                R.id.info_text_size,
                "TextSize ${
                    buildTextSizeString(
                        view.textSize,
                        view.context
                    )
                }"
            )

            setText(
                R.id.info_text_color,
                "TextColor ${getColorHex(view.currentTextColor)}"
            )

            with(layout) {
                findViewById<View>(R.id.info_text_color_container).visible()
                findViewById<ColorView>(R.id.info_text_color_view).color = view.currentTextColor
            }
        } else {
            with(layout) {
                findViewById<View>(R.id.info_text_color_container).gone()
                findViewById<View>(R.id.info_text_size).gone()
            }
        }

        with(layout) {
            findViewById<View>(R.id.info_view_container).visible()
            findViewById<Switch>(R.id.dop_px_switch).setOnCheckedChangeListener { _, isChecked ->
                configChange(isChecked)
            }
        }
    }

    fun hide() {
        layout.findViewById<View>(R.id.info_view_container).gone()
    }

    private fun setText(
        textViewId: Int,
        textToShow: String
    ) {
        layout.findViewById<TextView>(textViewId).apply {
            text = textToShow
            visible()
        }
    }

}