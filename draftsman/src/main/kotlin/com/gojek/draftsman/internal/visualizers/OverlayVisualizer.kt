package com.gojek.draftsman.internal.visualizers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.TextView
import com.gojek.draftsman.R
import com.gojek.draftsman.internal.extensions.doOnProgressChange
import com.gojek.draftsman.internal.drawer.dpToPx

class OverlayVisualizer(
    context: Context,
    onClickClose: () -> Unit
) : FrameLayout(context) {

    private val opacityControl: View

    private val opacityText: TextView

    init {
        setWillNotDraw(false)
        opacityControl =
            LayoutInflater.from(context).inflate(R.layout.overlay_opacity_control_layout, this)
                .findViewById(R.id.opacity_control)
        opacityText = findViewById(R.id.opacity)
        findViewById<SeekBar>(R.id.opacity_seekbar).doOnProgressChange {
            opacityText.text = "$it%"
            setOpacity()
            postInvalidate()
        }
        findViewById<View>(R.id.close_overlay).setOnClickListener {
            onClickClose()
        }
    }

    private var bitmap: BitmapDrawable? = null

    fun setOverlayImage(image: Bitmap) {
        bitmap = BitmapDrawable(resources, image)
        setOpacity()
        postInvalidate()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val margin = dpToPx(16, resources).toInt()
        opacityControl.layout(
            l + margin,
            b - margin - dpToPx(48, resources).toInt(),
            r - margin,
            b - margin
        )
    }

    override fun dispatchDraw(canvas: Canvas) {
        canvas.apply {
            if (null != bitmap) {
                bitmap?.setBounds(0, 0, width, height)
                bitmap?.draw(canvas)
            }
        }

        super.dispatchDraw(canvas)
    }

    private fun setOpacity() {
        val opacity = opacityText.text.replace(Regex("%"), "").toInt()
        bitmap?.alpha = 255 * opacity / 100
    }
}