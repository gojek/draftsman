package com.gojek.draftsman.internal

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.gojek.draftsman.R
import com.gojek.draftsman.internal.drawer.DrawerAdapter
import com.gojek.draftsman.internal.drawer.DrawerOptionHandler
import com.gojek.draftsman.internal.drawer.ModernDrawerLayout
import com.gojek.draftsman.internal.model.*
import com.gojek.draftsman.internal.utils.DimensionConverter
import com.gojek.draftsman.internal.utils.ExitListener
import com.gojek.draftsman.internal.utils.LayeredViewBuilder
import com.gojek.draftsman.internal.utils.getDrawerLayoutList
import com.gojek.draftsman.internal.visualizers.*

internal class DraftsmanLayout @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleRes: Int = 0
) : FrameLayout(
    context,
    attributeSet,
    defStyleRes
), ViewGroupCallback, DraftsmanCallback {

    private val borderPaint = Paint(ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

    private val infoVisualizer: InfoVisualizer by lazy {
        InfoVisualizer(
            this,
            { exitListener?.invoke() },
            {
                isInfoEnabled = false
                infoVisualizer.hide()
            },
            {
                Config.usePx = it
                invalidate()
            }
        )
    }

    private var exitListener: ExitListener? = null

    private lateinit var root: View

    private var layeredViewBuilder: LayeredViewBuilder? = null

    private var layerVisualizer: LayeringVisualizer? = null

    private var layeredViews = listOf<LayeredView>()

    private var selectedViews = mutableListOf<View>()

    private var isInfoEnabled = true

    private val drawerOptionHandler = DrawerOptionHandler(
        context,
        this,
        this
    )

    init {
        setWillNotDraw(false)
        Config.reset()
        addDrawerLayout()
    }

    fun init(
        topInset: Int,
        root: View,
        exitListener: ExitListener
    ) {
        this.root = root
        this.exitListener = exitListener
        layeredViewBuilder = LayeredViewBuilder(topInset)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        if (::root.isInitialized) {
            if (layeredViews.isEmpty()) {
                layeredViewBuilder?.let {
                    layeredViews = it.buildLayeredView(root)
                }
            }

            layeredViews.forEach {
                drawBound(canvas, it)
                if (selectedViews.contains(it.view)) {
                    showProperties(canvas, it)
                }
            }

            if (isInfoEnabled) {
                selectedViews.lastOrNull()?.let {
                    infoVisualizer.updateInfoView(it)
                }
            }
        }
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.actionMasked == MotionEvent.ACTION_UP &&
            layerVisualizer == null
        ) {
            val possibleViews = getSelectedViews(event)
            if (possibleViews.size == 1) {
                onSelectView(possibleViews[0].view)
            } else {
                showLayeredViews(possibleViews)
            }
        }

        return null == layerVisualizer
    }

    override fun addView(index: Int, view: View?) {
        addView(view, index)
    }

    override fun onDetachedFromWindow() {
        drawerOptionHandler.cleanUp()
        super.onDetachedFromWindow()
    }

    override fun changeRootView(view: View) {
        root = view
        layeredViewBuilder?.let {
            layeredViews = it.buildLayeredView(root)
        }
        selectedViews.clear()
        invalidate()
    }

    override fun closeDrawer() {
        findViewById<ModernDrawerLayout>(R.id.draftsman_drawer).collapse()
    }

    private fun drawBound(
        canvas: Canvas,
        layeredView: LayeredView
    ) {
        if (selectedViews.contains(layeredView.view)) {
            borderPaint.apply {
                color = Config.selectedBoundColor
                strokeWidth = DimensionConverter.getPxOfDp(2f, context)
            }
        } else {
            borderPaint.apply {
                color = Config.boundLineColor
                strokeWidth = 1f
            }
        }
        canvas.drawRect(layeredView.position, borderPaint)
    }

    private fun showProperties(
        canvas: Canvas,
        layeredView: LayeredView
    ) {
        if (Config.paddingEnabled) {
            PaddingVisualizer.draw(
                canvas,
                layeredView.view,
                layeredView.position
            )
        }
        if (Config.marginEnabled) {
            MarginVisualizer.draw(
                canvas,
                layeredView.view,
                layeredView.position
            )
        }
    }

    private fun getSelectedViews(event: MotionEvent): List<LayeredView> {
        val possibleViews = mutableListOf<LayeredView>()
        val views = layeredViews.filter {
            it.position.contains(event.x, event.y)
        }

        if (views.size == 1) {
            possibleViews.add(views[0])
        } else {
            val smallestArea = views.map { it.position.width().times(it.position.height()) }.min()
            val smallestAreaViews = views.filter {
                it.position.width().times(it.position.height()) == smallestArea
            }
            possibleViews.addAll(smallestAreaViews)
        }

        return possibleViews
    }

    private fun showLayeredViews(views: List<LayeredView>) {
        layerVisualizer = LayeringVisualizer(this).apply {
            showLayeredViews(views)
            setOnLayeredViewSelectListener {
                onSelectView(it)
                remove(this@DraftsmanLayout)
                layerVisualizer = null
            }
            setOnDismissListener(OnClickListener {
                remove(this@DraftsmanLayout)
                layerVisualizer = null
            })
        }
    }

    private fun onSelectView(view: View) {
        if (selectedViews.contains(view)) {
            selectedViews.remove(view)
        } else {
            selectedViews.add(view)
            isInfoEnabled = true
        }
        invalidate()
    }

    private fun addDrawerLayout() {
        val recyclerView = LayoutInflater.from(context).inflate(
            R.layout.draftsman_drawer_layout,
            this
        ).findViewById<RecyclerView>(R.id.draftsman_config_drawer)
        val adapter = DrawerAdapter(drawerOptionHandler.getDrawerInteractions()).also {
            drawerOptionHandler.adapter = it
        }
        recyclerView.adapter = adapter
        adapter.setData(getDrawerLayoutList())
    }
}

internal interface ViewGroupCallback {
    fun addView(index: Int, view: View?)
    fun invalidate()
    fun removeView(view: View?)
}

internal interface DraftsmanCallback {
    fun changeRootView(view: View)
    fun closeDrawer()
}
