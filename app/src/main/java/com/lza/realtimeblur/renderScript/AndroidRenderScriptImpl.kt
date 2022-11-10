package com.lza.realtimeblur.renderScript

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
        val renderScript = RenderScript.create(context)
        val blurScript = ScriptIntrinsicBlur.create(
            AndroidRenderScriptImpl.renderScript, Element.U8_4(
                AndroidRenderScriptImpl.renderScript
            ))

        val bitmap = Bitmap.createBitmap(4, 4, Bitmap.Config.ARGB_8888)
        val allocationInput = Allocation.createFromBitmap(renderScript, bitmap, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT)
        val allocationOutput = Allocation.createTyped(renderScript, allocationInput.getType())
        bitmap.recycle()

        AndroidRenderScriptImpl.renderScript = renderScript
        AndroidRenderScriptImpl.blurScript = blurScript
        blurInput = allocationInput
        blurOutput = allocationOutput
    }

    override fun blur(input: Bitmap, output: Bitmap, radius: Float) {
        val blurScript = blurScript
        val blurInput = blurInput
        val blurOutput = blurOutput
        if (blurScript == null || blurInput == null || blurOutput == null) {
            return
        }

        blurInput.copyFrom(input)
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
