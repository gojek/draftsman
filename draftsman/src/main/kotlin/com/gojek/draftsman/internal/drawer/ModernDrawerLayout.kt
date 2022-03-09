package com.gojek.draftsman.internal.drawer

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.*
import android.view.View
import android.view.View.MeasureSpec.*
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import com.gojek.draftsman.R
import kotlin.math.abs
import kotlin.math.min

private val TAG = ModernDrawerLayout::class.java.simpleName

internal class ModernDrawerLayout @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleRes: Int = 0
) : ViewGroup(
    context,
    attributeSet,
    defStyleRes
) {

    private val sliderShapeDimension = dpToPx(72, resources)

    private var orientation = Orientation.HORIZONTAL_RIGHT

    private var peekDistance = 0f

    private var bgDrawable: Drawable = ColorDrawable(Color.LTGRAY)

    private var sliderBitmap: Bitmap? = null

    private var expanded = false

    private val bitmapStartingPoint = PointF()

    private val touchArea = RectF()

    private val sliderIconRect = Rect()

    private var drawerOpenIcon: Drawable? = null

    private var drawerCloseIcon: Drawable? = null

    private var drawerIcon: Drawable? = null

    private var previousX = 0f

    private var maxTranslation = 0f

    private var animator: Animator? = null

    private var motionDownTime: Long = 0

    private var sliderPath = Path()

    private val shadowPaint = Paint(ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        setShadowLayer(dpToPx(8, context.resources), 0f, 0f, Color.LTGRAY)
    }

    private val overlayPaint = Paint(ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#999999")
        alpha = 0
    }

    init {
        setWillNotDraw(false)
        attributeSet?.let(::initAttrs)
    }

    fun setIcons(openIcon: Drawable, closeIcon: Drawable) {
        drawerOpenIcon = openIcon
        drawerCloseIcon = closeIcon
        drawerIcon = drawerOpenIcon
    }

    fun collapse() {
        if (expanded) {
            collapseDrawer()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        require(childCount <= 1) { "ModernDrawerLayout can have only one child" }

        var width = getSize(widthMeasureSpec)
        var height = getSize(heightMeasureSpec)

        var widthMeasureMode = getMode(widthMeasureSpec)
        var heightMeasureMode = getMode(heightMeasureSpec)

        if (widthMeasureMode != EXACTLY && heightMeasureMode != EXACTLY) {
            if (isInEditMode) {
                if (widthMeasureMode == AT_MOST) {
                    widthMeasureMode = EXACTLY
                } else if (widthMeasureMode == UNSPECIFIED) {
                    widthMeasureMode = EXACTLY
                    width = 300
                }
                if (heightMeasureMode == AT_MOST) {
                    heightMeasureMode = EXACTLY
                } else if (heightMeasureMode == UNSPECIFIED) {
                    heightMeasureMode = EXACTLY
                    height = 300
                }

            } else {
                throw IllegalArgumentException("ModernDrawerLayout has to be measured exactly")
            }
        }

        if (orientation == Orientation.HORIZONTAL_LEFT ||
            orientation == Orientation.HORIZONTAL_RIGHT
        ) {
            width += resources.displayMetrics.widthPixels - peekDistance.toInt()
        } else {
            height += resources.displayMetrics.heightPixels - peekDistance.toInt()
        }

        setMeasuredDimension(width, height)


        val childLp = getChildAt(0).layoutParams

        val childWidthMeasureSpec =
            getChildMeasureSpec(widthMeasureSpec, paddingLeft + paddingRight, childLp.width)
        val childHeightMeasureSpec =
            getChildMeasureSpec(heightMeasureSpec, paddingTop + paddingBottom, childLp.height)
        getChildAt(0).measure(childWidthMeasureSpec, childHeightMeasureSpec)
        maxTranslation = -min(
            width - resources.displayMetrics.widthPixels - peekDistance - sliderShapeDimension,
            getChildAt(0).measuredWidth - peekDistance
        )
    }

    override fun onLayout(changed: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        val child = getChildAt(0)
        child.layout(
            resources.displayMetrics.widthPixels - peekDistance.toInt(),
            paddingTop,
            resources.displayMetrics.widthPixels - peekDistance.toInt() + child.measuredWidth,
            paddingTop + measuredHeight
        )
        if (changed) {
            generateSliderBitmap()
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        canvas.apply {
            drawPaint(overlayPaint)
            val count = save()
            translate(bitmapStartingPoint.x, bitmapStartingPoint.y)
            drawPath(sliderPath, shadowPaint)
            drawBitmap(sliderBitmap!!, 0f, 0f, null)
            restoreToCount(count)
        }
        drawerIcon?.bounds = sliderIconRect
        drawerIcon?.draw(canvas)
        super.dispatchDraw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return when (event.actionMasked) {
            ACTION_DOWN -> {
                motionDownTime = System.currentTimeMillis()
                touchArea.contains(event.x, event.y)
            }
            ACTION_UP -> {
                if (System.currentTimeMillis() - motionDownTime <= 300) {
                    toggleState()
                } else {
                    expandOrCollapse()
                }
                previousX = 0f
                true
            }
            ACTION_MOVE -> {
                moveDrawer(event.rawX)
                false
            }
            else -> false
        }
    }

    override fun onDetachedFromWindow() {
        if (animator?.isRunning == true) {
            animator?.cancel()
        }
        super.onDetachedFromWindow()
    }

    private fun generateSliderBitmap() {

        bitmapStartingPoint.x =
            resources.displayMetrics.widthPixels - peekDistance - sliderShapeDimension
        bitmapStartingPoint.y = 0f

        val result = DrawerPathGenerator(
            height,
            resources,
            sliderShapeDimension,
            peekDistance
        ).getPath(
            orientation,
            getChildAt(0).measuredWidth
        )

        sliderBitmap = Bitmap.createBitmap(
            getChildAt(0).measuredWidth + sliderShapeDimension.toInt(),
            height,
            Bitmap.Config.ARGB_8888
        )
        if (null != background) {
            if (bgDrawable != background) {
                bgDrawable = background
                setBackgroundResource(0)
            } else {
                bgDrawable = ColorDrawable(Color.WHITE)
            }
        }
        bgDrawable.bounds = Rect(
            0,
            0,
            getChildAt(0).measuredWidth + sliderShapeDimension.toInt(),
            height
        )
        val canvas = Canvas(sliderBitmap!!).apply {
            clipPath(result.first)
        }
        bgDrawable.draw(canvas)
        touchArea.set(result.second)
        calculateIconDrawRect()
        sliderPath = result.first
    }

    private fun expandDrawer() {
        val expandAnimation = ObjectAnimator.ofFloat(
            this, View.TRANSLATION_X,
            maxTranslation
        ).apply {
            duration = 500
            interpolator = AccelerateDecelerateInterpolator()
            addListener(object : DefaultAnimationListener() {
                override fun onAnimationEnd(animation: Animator?) {
                    expanded = true
                    onStateChanged()
                }
            })
        }

        val overlayAnimation = ValueAnimator.ofInt(overlayPaint.alpha, 127).apply {
            duration = 500
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener {
                overlayPaint.alpha = it.animatedValue as Int
                postInvalidate()
            }
        }

        animator = AnimatorSet().apply {
            playTogether(overlayAnimation, expandAnimation)
            start()
        }
        expanded = true
    }

    private fun collapseDrawer() {
        val collapseAnimation = ObjectAnimator.ofFloat(
            this, View.TRANSLATION_X,
            0f
        ).apply {
            duration = 500
            interpolator = AccelerateDecelerateInterpolator()
            addListener(object : DefaultAnimationListener() {
                override fun onAnimationEnd(animation: Animator?) {
                    expanded = false
                    onStateChanged()
                }
            })
        }

        val overlayAnimation = ValueAnimator.ofInt(overlayPaint.alpha, 0).apply {
            duration = 500
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener {
                overlayPaint.alpha = it.animatedValue as Int
                postInvalidate()
            }
        }

        animator = AnimatorSet().apply {
            playTogether(overlayAnimation, collapseAnimation)
            start()
        }
        expanded = false
    }

    private fun toggleState() {
        if (expanded) {
            collapseDrawer()
        } else {
            expandDrawer()
        }
    }

    private fun calculateIconDrawRect() {
        val vCenter = (touchArea.top + (touchArea.bottom - touchArea.top) / 2).toInt()
        val hCenter = (touchArea.left + (touchArea.right - touchArea.left) / 2).toInt()
        val iconDimensionHalf = dpToPx(16, resources).toInt()
        sliderIconRect.apply {
            top = vCenter - iconDimensionHalf
            bottom = vCenter + iconDimensionHalf
            left = hCenter - iconDimensionHalf
            right = hCenter + iconDimensionHalf
        }
    }

    private fun initAttrs(attributeSet: AttributeSet) {
        val typedArray =
            context.obtainStyledAttributes(attributeSet, R.styleable.ModernDrawerLayout)
        peekDistance =
            typedArray.getDimension(R.styleable.ModernDrawerLayout_peekDistance, peekDistance)
        drawerOpenIcon = typedArray.getDrawable(R.styleable.ModernDrawerLayout_drawerOpenIcon)
        drawerCloseIcon = typedArray.getDrawable(R.styleable.ModernDrawerLayout_drawerCloseIcon)
        typedArray.recycle()
        drawerIcon = drawerOpenIcon
    }

    private fun onStateChanged() {
        drawerIcon = if (expanded) drawerCloseIcon else drawerOpenIcon
        invalidate()
    }

    private fun moveDrawer(x: Float) {
        if (previousX != 0f) {
            val dx = x - previousX
            previousX = x
            var newTranslationX = translationX + dx
            if (newTranslationX < maxTranslation) {
                newTranslationX = maxTranslation
            } else if (newTranslationX > 0) {
                newTranslationX = 0f
            }
            translationX = newTranslationX
            overlayPaint.alpha = (127 * abs(newTranslationX / maxTranslation)).toInt()
            postInvalidate()
        } else {
            previousX = x
        }
    }

    private fun expandOrCollapse() {
        if (translationX != 0f || translationX != maxTranslation) {
            val distanceToExpand = maxTranslation - translationX
            if (abs(distanceToExpand) > abs(translationX)) {
                collapseDrawer()
            } else {
                expandDrawer()
            }
        }
    }
}