package com.lza.realtimeblur.renderScript

import android.graphics.Bitmap
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
class WindowBlurImpl(private val window: Window, private val blurFactor: Float, private val blurRadius: Float) {

    private val targetList: LinkedList<BlurTarget> = LinkedList()
    private val realFactor: Float
        get() = blurFactor.coerceAtLeast(1f).coerceAtMost(20f)

    private val scaleValue: Float
        get() = 1f / blurFactor

    private var blurCanvas: BlurCanvas? = null

    private var windowBgBitmap: Bitmap? = null
    private var blurBitmap: Bitmap? = null

    private val decorView: View
        get() = window.decorView

    private val viewTreeObserver = ViewTreeObserver.OnPreDrawListener {
        val show = targetList.all { it.isShow }
        val windowBgBitmap = this.windowBgBitmap
        val blurBitmap = this.blurBitmap
        val canvas = this.blurCanvas

        if (!prepare() || windowBgBitmap == null || blurBitmap == null || canvas == null || !show) {
            return@OnPreDrawListener false
        }

        windowBgBitmap.eraseColor(0xFFFFFF)

        val restoreCount = canvas.save()
        try {
            decorView.background?.draw(canvas)
            decorView.draw(canvas)
        } catch (e:Exception) {
            e.printStackTrace()
        } finally {
            canvas.restoreToCount(restoreCount)
        }

        blurService.blur(windowBgBitmap, blurBitmap, blurRadius, scaleValue)

        val iterator = targetList.iterator()
        while (iterator.hasNext()) {
            val target = iterator.next()
            target.onRefreshBlurResult(bitmap = blurBitmap)
        }

        return@OnPreDrawListener true
    }

    init {
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
        }

        return true
    }


    fun addBlurTarget(target: BlurTarget) {

    }

    fun removeBlurTarget(target: BlurTarget) {

    }
}
