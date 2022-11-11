package com.lza.realtimeblur.renderScript

import android.graphics.Bitmap
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.Window
import com.lza.realtimeblur.interfaces.BlurTarget
import java.lang.Exception
import java.util.*

/**
 * @author liuzhongao
 * @Date 2022/11/10 14:08
 */
class WindowBlurImpl(private val window: Window, private val blurFactor: () -> Float, private var blurRadius: () -> Float) {

    private val targetList: LinkedList<BlurTarget> = LinkedList()
    private val realFactor: Float
        get() = blurFactor().coerceAtLeast(1f)

    private val scaleValue: Float
        get() = 1f / blurFactor()

    private var blurCanvas: BlurCanvas? = null

    private var windowBgBitmap: Bitmap? = null
    private var blurBitmap: Bitmap? = null
    private var dirty: Boolean = false

    private val decorView: View
        get() = window.decorView

    val isEmpty: Boolean
        get() = targetList.isEmpty()

    private val viewTreeObserver = ViewTreeObserver.OnPreDrawListener {
        val show = targetList.all { it.isShow }
        val canvas = this.blurCanvas
        var windowBgBitmap = this.windowBgBitmap
        var blurBitmap = this.blurBitmap

        if (!prepare() || !show || windowBgBitmap == null || blurBitmap == null || canvas == null) {
            Log.d("AAAAAAAA", "skip blur operation")
            return@OnPreDrawListener true
        }
        windowBgBitmap = this.windowBgBitmap
        blurBitmap = this.blurBitmap

        if (windowBgBitmap == null || blurBitmap == null || windowBgBitmap.isRecycled || blurBitmap.isRecycled) {
            return@OnPreDrawListener true
        }

        windowBgBitmap.eraseColor(0xFFFFFF)

        val restoreCount = canvas.save()
        try {
            canvas.scale(scaleValue, scaleValue)
            decorView.background?.draw(canvas)
            decorView.draw(canvas)
        } catch (e:Exception) {
            e.printStackTrace()
        } finally {
            canvas.restoreToCount(restoreCount)
        }

        val blurRadius = blurRadius
        if (blurRadius() in 1f..25f) {
            blurService.blur(windowBgBitmap, blurBitmap, blurRadius())
        }

        val iterator = targetList.iterator()
        val dirty = this.dirty
        while (iterator.hasNext()) {
            val target = iterator.next()
            target.onRefreshBlurResult(bitmap = blurBitmap, dirty = dirty)
        }
        this.dirty = false

        return@OnPreDrawListener true
    }

    init {
        blurService.init(window.context)
        window.decorView.viewTreeObserver.addOnPreDrawListener(viewTreeObserver)
    }

    private fun prepare(): Boolean {
        if (targetList.isEmpty()) {
            return false
        }

        val windowBackgroundBitmap = this.windowBgBitmap
        val scaleWidth = (decorView.width / realFactor).toInt()
        val scaleHeight = (decorView.height / realFactor).toInt()

        val needRegenerateDrawBitmap = blurCanvas == null || windowBackgroundBitmap == null || windowBackgroundBitmap.width != scaleWidth || windowBackgroundBitmap.height != scaleHeight
        if (needRegenerateDrawBitmap) {
            Log.d("AAAAAAAA", "new draw bitmap")
            this.dirty = true
            this.windowBgBitmap?.recycle()
            this.blurBitmap?.recycle()

            val bgBitmap = Bitmap.createBitmap(scaleWidth, scaleHeight, Bitmap.Config.ARGB_8888)
            val blurBitmap = Bitmap.createBitmap(scaleWidth, scaleHeight, Bitmap.Config.ARGB_8888)
            if (blurCanvas == null) {
                blurCanvas = BlurCanvas()
            }
            blurCanvas?.setBitmap(bgBitmap)

            this.windowBgBitmap = bgBitmap
            this.blurBitmap = blurBitmap

            blurService.prepare(bgBitmap)
        }
        return true
    }

    fun addBlurTarget(target: BlurTarget) {
        Log.d("AAAAAAAA", "add target")
        if (!targetList.contains(target)) {
            this.targetList.add(target)
        }
    }

    fun removeBlurTarget(target: BlurTarget) {
        Log.d("AAAAAAAA", "remove target")
        targetList.remove(target)
    }

    fun release() {
        if (targetList.isNotEmpty()) {
            return
        }
        window.decorView.viewTreeObserver.removeOnPreDrawListener(viewTreeObserver)

        windowBgBitmap?.recycle()
        blurBitmap?.recycle()

        blurCanvas = null
        windowBgBitmap = null
        blurBitmap = null
    }
}
