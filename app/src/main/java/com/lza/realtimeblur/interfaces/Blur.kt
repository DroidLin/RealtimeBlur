package com.lza.realtimeblur.interfaces

import android.content.Context
import android.graphics.Bitmap

/**
 * @author liuzhongao
 * @Date 2022/11/10 13:31
 */
interface Blur {
    fun init(context: Context)
    fun blur(input: Bitmap, output: Bitmap, radius: Float)
    fun prepare(buffer: Bitmap): Boolean
    fun release(context: Context)
}