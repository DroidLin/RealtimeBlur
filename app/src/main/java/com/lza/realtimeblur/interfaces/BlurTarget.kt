package com.lza.realtimeblur.interfaces

import android.graphics.Bitmap

/**
 * @author liuzhongao
 * @Date 2022/11/10 13:56
 */
interface BlurTarget {
    val isShow: Boolean
    fun onRefreshBlurResult(bitmap: Bitmap)
}