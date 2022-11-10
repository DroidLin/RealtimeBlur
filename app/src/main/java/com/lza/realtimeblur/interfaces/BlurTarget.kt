package com.lza.realtimeblur.interfaces

import android.content.Context
import android.graphics.Bitmap

/**
 * @author liuzhongao
 * @Date 2022/11/10 13:56
 */
interface BlurTarget {
    val ctx: Context
    val isShow: Boolean
    val blurFactor: Float
    val blurRadius: Float
    fun onRefreshBlurResult(bitmap: Bitmap, scale: Float)
}