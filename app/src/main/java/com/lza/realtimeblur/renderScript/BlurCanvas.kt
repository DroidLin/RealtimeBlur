package com.lza.realtimeblur.renderScript

import android.graphics.Bitmap
import android.graphics.Canvas

/**
 * @author liuzhongao
 * @Date 2022/11/10 14:39
 */
class BlurCanvas: Canvas {
    constructor() : super()
    constructor(bitmap: Bitmap) : super(bitmap)
}