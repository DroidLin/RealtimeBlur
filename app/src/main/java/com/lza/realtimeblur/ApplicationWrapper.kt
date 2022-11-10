package com.lza.realtimeblur

import android.app.Application
import android.content.Context

/**
 * @author liuzhongao
 * @Date 2022/11/10 13:39
 */
object ApplicationWrapper: Application() {
    public override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }
}