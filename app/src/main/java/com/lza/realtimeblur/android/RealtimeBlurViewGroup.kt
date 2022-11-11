package com.lza.realtimeblur.android

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.util.DebugUtils
import android.util.Log
import android.widget.FrameLayout
import com.lza.realtimeblur.interfaces.BlurTarget
import com.lza.realtimeblur.renderScript.BlurCanvas
import com.lza.realtimeblur.renderScript.windowBlurService

class RealtimeBlurViewGroup : FrameLayout, BlurTarget {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    override val ctx: Context
        get() = super.getContext()
    override val isShow: Boolean
        get() = super.isShown()
    override var blurFactor: Float = 10f
    override var blurRadius: Float = 10f

    private val locationOnScreen = IntArray(2) { 0 }
    private val sourceRect = Rect()
    private val destinationRect = RectF()
    private val paint = Paint()
    private var drawBitmap: Bitmap? = null
    private val scale: Float
        get() = 1f / blurFactor

    init {
        setWillNotDraw(false)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Log.d("AAAAAAAA", "attach to window")
        windowBlurService.registerBlurTarget(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        Log.d("AAAAAAAA", "detach to window")
        windowBlurService.unRegisterBlurTarget(this)
    }

    override fun onRefreshBlurResult(bitmap: Bitmap, dirty: Boolean) {
        if (drawBitmap == null || drawBitmap != bitmap) {
            drawBitmap = bitmap
        }
//        this.scale = scale
        if (dirty) {
            Log.d("AAAAAAAA", "postInvalidate")
            postInvalidate()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        canvas ?: return
        if (canvas is BlurCanvas) {
            return
        }
        val bitmap = drawBitmap ?: return
        if (bitmap.isRecycled) {
            return
        }

        getLocationInWindow(locationOnScreen)

        val scaleX = locationOnScreen[0] * scale
        val scaleY = locationOnScreen[1] * scale

        val scaleWidth = measuredWidth * scale
        val scaleHeight = measuredHeight * scale

        sourceRect.set(scaleX.toInt(), scaleY.toInt(), (scaleX + scaleWidth).toInt(), (scaleY + scaleHeight).toInt())
        destinationRect.set(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat())

        canvas.save()
        canvas.drawBitmap(bitmap, sourceRect, destinationRect, paint)
        canvas.restore()

//        val color = 0xAAFFFFFF
//        paint.color = color.toInt()
//        canvas.drawRect(destinationRect, paint)
        super.onDraw(canvas)
    }

}