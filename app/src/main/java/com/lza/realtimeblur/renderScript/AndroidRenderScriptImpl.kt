package com.lza.realtimeblur.renderScript

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import com.lza.realtimeblur.interfaces.Blur

/**
 * @author liuzhongao
 * @Date 2022/11/10 13:31
 */
val blurService: Blur
    get() = AndroidRenderScriptImpl

internal object AndroidRenderScriptImpl: Blur {

    private var renderScript: RenderScript? = null
    private var blurScript: ScriptIntrinsicBlur? = null
    private var blurInput: Allocation? = null
    private var blurOutput: Allocation? = null

    override fun init(context: Context) {
        if (renderScript != null && blurScript != null && blurInput != null && blurOutput != null ) {
            return
        }
        val renderScript = RenderScript.create(context)
        val blurScript = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))

        this.renderScript = renderScript
        this.blurScript = blurScript

        val bitmap = Bitmap.createBitmap(4, 4, Bitmap.Config.ARGB_8888)
        prepare(bitmap)
        bitmap.recycle()
    }

    @SuppressLint("SoonBlockedPrivateApi")
    override fun prepare(buffer: Bitmap): Boolean {
        val allocationInput = Allocation.createFromBitmap(renderScript, buffer, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT)
        val allocationOutput = Allocation.createTyped(renderScript, allocationInput.type)

        this.blurInput = allocationInput
        this.blurOutput = allocationOutput
        return true
    }

    override fun blur(input: Bitmap, output: Bitmap, radius: Float) {
        val blurScript = blurScript
        val blurInput = blurInput
        val blurOutput = blurOutput
        if (blurScript == null || blurInput == null || blurOutput == null) {
            return
        }
        blurInput.copyFrom(input)
        blurScript.setRadius(radius)
        blurScript.setInput(blurInput)
        blurScript.forEach(blurOutput)
        blurOutput.copyTo(output)
    }

    override fun release(context: Context) {
        blurInput?.destroy()
        blurOutput?.destroy()
        blurScript?.destroy()
        renderScript?.destroy()
        blurInput = null
        blurOutput = null
        blurScript = null
        renderScript = null
    }
}
